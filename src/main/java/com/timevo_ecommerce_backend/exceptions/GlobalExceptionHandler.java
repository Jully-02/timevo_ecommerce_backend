package com.timevo_ecommerce_backend.exceptions;

import com.timevo_ecommerce_backend.responses.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler(Exception.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public ResponseEntity<Response> handleGeneralException (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Response.builder()
                            .message(e.getMessage())
                            .status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .build()
            );
        }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<?> handleResourceNotFoundException(DataNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Response.builder()
                        .status(HttpStatus.NOT_FOUND)
                        .message(exception.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(ExistDataException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<?> handleResourceExistDataException(ExistDataException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Response.builder()
                        .status(HttpStatus.CONFLICT)
                        .message(exception.getMessage())
                        .build()
        );
    }
}
