package com.codefactory.urlshortener.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public GlobalExceptionHandler() {}

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidationException(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage() != null ? ex.getMessage() : "Validation failed");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid field")
                        .collect(Collectors.joining(" ;"))
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Unknown error";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error: " + message);
    }
}