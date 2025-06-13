package org.diarymoodanalyzer.exception;

/**
 * Thrown to indicate the method has been passed invalid password.
 */
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
