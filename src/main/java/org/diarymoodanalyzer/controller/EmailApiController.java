package org.diarymoodanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.dto.request.SendVerificationCodeRequest;
import org.diarymoodanalyzer.dto.request.VerifyCodeRequest;
import org.diarymoodanalyzer.dto.response.VerifyCodeResponse;
import org.diarymoodanalyzer.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailApiController {

    private final EmailService emailService;

    /**
     * Send verification code to <code>req.getEmail()</code>
     * @param req Request DTO with <code>email</code>
     * @return Response with 200 when success
     */
    @PostMapping("/send/verification-code")
    public ResponseEntity<Void> sendVerificationCode(
            @RequestBody SendVerificationCodeRequest req
    ) {
        emailService.sendVerificationCode(req.getEmail());
        return ResponseEntity.ok().build();
    }

    /**
     * Verify <code>req.getEmail()</code> by 6-digit <code>req.getCode()</code>
     * @param req Request DTO with <code>email</code> and <code>code</code>
     * @return ResponseEntity with Response DTO <code>VerifyCodeResponse</code>
     */
    @PostMapping("/verify")
    public ResponseEntity<VerifyCodeResponse> verifyCode(
            @RequestBody VerifyCodeRequest req
    ) {
        VerifyCodeResponse response;
        try {
            boolean result = emailService.verifyCode(req.getEmail(), req.getCode());
            response = new VerifyCodeResponse(result, false);

            return ResponseEntity.ok(response);
        } catch (EmailService.FailCountExceedException e) {
            // When exceed fail count, set `isFailCountExceed` flag as false
            response = new VerifyCodeResponse(false, true);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}
