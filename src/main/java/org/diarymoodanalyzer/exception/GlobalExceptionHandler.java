package org.diarymoodanalyzer.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for Controller classes with {@link org.springframework.web.bind.annotation.RestController} annotation.
 * Return ResponseEntity with error message and status code consistently.
 * <br/>
 * Centralize exception handling to ensure consistent response.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException e) {
        logger.warn("DuplicateEmailException: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT,
                "Duplicate email",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(FailCountExceedException.class)
    public ResponseEntity<ErrorResponse> handleFailCountExceed(FailCountExceedException e) {
        logger.warn("FailCountExceedException: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Fail count exceed",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEmail(InvalidEmailException e) {
        logger.warn("InvalidEmailException: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid email format",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException e) {
        logger.warn("InvalidPasswordException: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid credential",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(NotAuthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthenticated(NotAuthenticatedException e) {
        logger.warn("NoAuthenticatedException: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                "There is no valid authentication",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<ErrorResponse> handleNoPermission(NoPermissionException e) {
        logger.warn("NoPermissionException: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                "You have not permission",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
        logger.warn("NotFoundException: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "Entity not found",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(NotVerifiedEmailException.class)
    public ResponseEntity<ErrorResponse> handleNotVerifiedEmail(NotVerifiedEmailException e) {
        logger.warn("NotVerifiedEmailException: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.FORBIDDEN,
                "Email is not verified",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        logger.warn("IllegalArgumentException: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request parameter or body",
                e.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception e) {
        logger.warn("UnexpectedException: {}", e.getMessage());

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "Unexpected error has occurred"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
