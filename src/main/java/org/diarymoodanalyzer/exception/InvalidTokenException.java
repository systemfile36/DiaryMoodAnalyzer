package org.diarymoodanalyzer.exception;

/**
 * Thrown to indicate the token is invalid.
 *
 * @see org.diarymoodanalyzer.service.TokenService
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}
