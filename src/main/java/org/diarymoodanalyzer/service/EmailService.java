package org.diarymoodanalyzer.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final RedisService redisService;

    //Using JavaMailSender
    private final JavaMailSender mailSender;

    //Using Thymeleaf for render email template
    private final TemplateEngine templateEngine;

    /**
     * Default sender of SMTP server.
     * It should be set `From` header to send email by SMTP
     */
    private static final String DEFAULT_FROM_MAIL = "systemfile36@gmail.com";

    /**
     * Prefix for key of Redis value contain verification code
     */
    private static final String EMAIL_VERIFY_PREFIX = "email:verify:";

    /**
     * Prefix for key of Redis value contain verified flag
     */
    private static final String EMAIL_VERIFIED_PREFIX = "email:verified:";

    /**
     * Template name of verification code
     */
    private static final String VERIFY_TEMPLATE_NAME = "verification-code-email";

    /**
     * Default expire minutes of verification code
     */
    private static final long VERIFY_EXPIRE_MINUTES = 5;

    /**
     * Prefix for key of Redis value contain fail count of verification
     */
    private static final String EMAIL_FAIL_COUNT_PREFIX = "email:failcount:";

    /**
     * Max fail count of verification code
     */
    private static final long EMAIL_MAX_FAIL_COUNT = 5;

    /**
     * Default expire minutes of verification fail count
     */
    private static final long VERIFY_FAIL_COUNT_MINUTES = 5;

    /**
     * Send email as SimpleMailMessage
     * @param email email of user who will receive the email this function sends
     * @param subject subject of email
     * @param content content(Text) of email
     */
    public void sendEmail(String email, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setFrom(DEFAULT_FROM_MAIL); // Set From header
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

    /**
     * Send email as MimeMessage with Thymeleaf HTML template and variables
     * @param email email of user who will receive the email this function sends
     * @param subject subject of email
     * @param templateName name of Thymeleaf HTML template in `resources/templates`
     * @param variables variables of Thymeleaf Context for template
     */
    public void sendEmail(String email, String subject, String templateName, Map<String, Object> variables ){
        try {
            //Create MIME email message for send(MimeMessage)
            MimeMessage message = mailSender.createMimeMessage();

            //Helper to populate MimeMessage as SimpleMailMessage
            MimeMessageHelper helper = new MimeMessageHelper(message);

            //Thymeleaf context for process template
            Context context = new Context();
            context.setVariables(variables);

            //Process template specified by name
            String htmlString = templateEngine.process(templateName, context);

            helper.setTo(email);
            helper.setFrom(DEFAULT_FROM_MAIL); // Set From header
            helper.setSubject(subject);

            //Set Text as HTML
            helper.setText(htmlString, true);

            //send email
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send verification code to `email`
     * @param email email of user who will receive the verification code
     */
    public void sendVerificationCode(String email) {
        // Generate and save verification code
        String code = generateVerificationCode();
        redisService.saveWithTTL(EMAIL_VERIFY_PREFIX + email, code, VERIFY_EXPIRE_MINUTES);

        // Set Model for verification-code-email template
        Map<String, Object> variables = Map.of(
                "code", code, // set verification code
                "expireMin", VERIFY_EXPIRE_MINUTES, //set expire minutes
                // set expire time
                "expireDateTime", LocalDateTime.now().plusMinutes(VERIFY_EXPIRE_MINUTES)
        );

        // temporary hard-coded email subject
        sendEmail(email, "이메일 인증 코드", VERIFY_TEMPLATE_NAME, variables);
    }

    /**
     * Validate verification code.
     * @param email email to check verification code
     * @param code verification code
     * @return true if code is valid, otherwise false
     * @throws FailCountExceedException If fail count of email has exceeded
     */
    public boolean verifyCode(String email, String code) throws FailCountExceedException {

        // When fail count exceed, throw exception
        if(getFailCount(email) > EMAIL_MAX_FAIL_COUNT) {
            throw new FailCountExceedException("Fail count exceed: " + email);
        }

        String key = EMAIL_VERIFY_PREFIX + email;

        String savedCode = redisService.get(key);

        // If key does not exist, return false
        if(savedCode == null) return false;

        if(savedCode.equals(code)) {
            // Set verified flag and delete verification code.
            setVerified(email, true);
            redisService.delete(key);

            return true;
        } else {
            // Increment fail count
            incrementFailCount(email);
            return false;
        }
    }

    /**
     * Check email is verified. <br/>
     * WARNING: this method deletes the key.
     * If called a second time, it will return false even if the key was already verified
     * @param email email to check verified
     * @return true when `email` is verified, otherwise return false.
     */
    public boolean isVerified(String email) {
        String key = EMAIL_VERIFIED_PREFIX + email;
        return redisService.getFlagAndDelete(key);
    }

    /**
     * Increment fail count of verification code
     * @param email email to increase fail count
     */
    private void incrementFailCount(String email) throws IllegalStateException {
        String key = EMAIL_FAIL_COUNT_PREFIX + email;

        // Expires `VERIFY_FAIL_COUNT_MINUTES` minutes after th first increment call
        redisService.incrementWithTTL(key, VERIFY_FAIL_COUNT_MINUTES);
    }

    /**
     * Return fail count of verification code
     * @param email email to get fail count
     * @return fail count. if key does not exist, return 0
     */
    private long getFailCount(String email) {
        String key = EMAIL_FAIL_COUNT_PREFIX + email;
        String failCountStr = redisService.get(key);

        return failCountStr != null ? Long.parseLong(failCountStr) : 0;
    }

    /**
     * Set verified flag
     * @param email email to set verified
     * @param isVerified verified flag to set
     */
    private void setVerified(String email, boolean isVerified) {
        String key = EMAIL_VERIFIED_PREFIX + email;
        redisService.save(key, isVerified);
    }

    /**
     * Generate 6-digit verification code randomly
     * @return Generated 6-digit verification code string
     */
    private String generateVerificationCode() {
        int randNum = new SecureRandom().nextInt(1000000);

        //Convert integer to 6-digit code
        return String.format("%06d", randNum);
    }

    /**
     * Thrown to indicate fail count exceed on verification code
     */
    public static class FailCountExceedException extends RuntimeException {
        public FailCountExceedException(String message) {
            super(message);
        }
    }
}
