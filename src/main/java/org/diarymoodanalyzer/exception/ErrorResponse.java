package org.diarymoodanalyzer.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

/**
 * DTO class for Exception handling,
 * <br/>
 * It will be used by exception handler of
 * {@link GlobalExceptionHandler GlobalExceptionHandler}
 *
 * @see GlobalExceptionHandler
 */
@Getter
public class ErrorResponse {
    private final LocalDateTime timeStamp;

    private final int status;

    private final String error;

    private final String message;

    public ErrorResponse(HttpStatusCode status, String error, String message) {
        // Set to now
        this.timeStamp = LocalDateTime.now();

        // Get value from `HttpStatusCode`
        this.status = status.value();
        this.error = error; this.message = message;
    }
}
