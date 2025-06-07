package org.diarymoodanalyzer.exception;

/**
 * Thrown to indicate the email of
 *  {@link org.diarymoodanalyzer.domain.User User} entity is duplicated.
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
