package org.diarymoodanalyzer.exception;

/**
 * Thrown to indicate the method has been passed invalid email
 * <br/>
 * This exception indicate the email is invalid format.
 */
public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
