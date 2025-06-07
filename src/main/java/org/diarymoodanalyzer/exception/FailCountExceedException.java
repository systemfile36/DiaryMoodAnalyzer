package org.diarymoodanalyzer.exception;

/**
 * Thrown to indicate fail count exceed on verification code.
 *
 * @see org.diarymoodanalyzer.service.EmailService
 */
public class FailCountExceedException extends RuntimeException {
    public FailCountExceedException(String message) {
        super(message);
    }
}
