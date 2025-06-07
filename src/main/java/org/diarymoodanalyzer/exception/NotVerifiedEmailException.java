package org.diarymoodanalyzer.exception;

/**
 * Thrown to indicate the email has not been verified.
 * <br/>
 * The email should be verified by {@link org.diarymoodanalyzer.service.EmailService EmailService} before sign up.
 */
public class NotVerifiedEmailException extends RuntimeException {
    public NotVerifiedEmailException(String message) {
        super(message);
    }
}
