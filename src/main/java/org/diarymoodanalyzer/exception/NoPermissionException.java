package org.diarymoodanalyzer.exception;

/**
 * Thrown to indicate current authenticated user doesn't have permission or proper authority for specific endpoint.
 */
public class NoPermissionException extends RuntimeException {
    public NoPermissionException(String message) {
        super(message);
    }
}
