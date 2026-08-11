package com.blackgoku.oms.payment.exception;

import com.blackgoku.oms.payment.dto.response.ApiErrorResponse;
import com.blackgoku.oms.payment.dto.response.FieldErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        List<FieldErrorResponse> errors =
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error ->
                                FieldErrorResponse.builder()
                                        .field(error.getField())
                                        .message(error.getDefaultMessage())
                                        .build()
                        )
                        .toList();

        ApiErrorResponse response =
                ApiErrorResponse.builder()
                        .success(false)
                        .message("Validation failed")
                        .errors(errors)
                        .timestamp(Instant.now())
                        .path(request.getRequestURI())
                        .build();

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            PaymentNotFoundException ex,
            HttpServletRequest request
    ) {
        return build(
                ex.getMessage(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler({
            InvalidPaymentStateException.class,
            PaymentAlreadyExistsException.class,
            IdempotencyKeyConflictException.class
    })
    public ResponseEntity<ApiErrorResponse> handleConflict(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return build(
                ex.getMessage(),
                HttpStatus.CONFLICT,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request
    ) {
        return build(
                "Something went wrong",
                HttpStatus.INTERNAL_SERVER_ERROR,
                request
        );
    }

    private ResponseEntity<ApiErrorResponse> build(
            String message,
            HttpStatus status,
            HttpServletRequest request
    ) {

        ApiErrorResponse response =
                ApiErrorResponse.builder()
                        .success(false)
                        .message(message)
                        .timestamp(Instant.now())
                        .path(request.getRequestURI())
                        .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }
}