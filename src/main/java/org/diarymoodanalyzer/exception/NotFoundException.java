package org.diarymoodanalyzer.exception;

/**
 * Thrown to indicate specified entity is not found
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
