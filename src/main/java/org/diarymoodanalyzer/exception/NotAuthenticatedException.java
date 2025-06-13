package org.diarymoodanalyzer.exception;

/**
 * Thrown to indicate the request doesn't have valid authentication information.
 */
public class NotAuthenticatedException extends RuntimeException {
    public NotAuthenticatedException(String message) {
        super(message);
    }
}
