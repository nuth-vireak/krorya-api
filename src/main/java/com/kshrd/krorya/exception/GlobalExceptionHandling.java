package com.kshrd.krorya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

@RestControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ProblemDetail handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.getMessage());
        problemDetail.setTitle("Email Already Exists");
        problemDetail.setType(URI.create("http://localhost:8080/api/v1/conflict"));
        problemDetail.setProperty("timestamp", new Date());
        return problemDetail;
    }

    @ExceptionHandler(CustomNotFoundException.class)
    public ProblemDetail handleCustomNotFoundException(CustomNotFoundException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("Not Found");
        problemDetail.setType(URI.create("http://localhost:8080/api/v1/not-found"));
        problemDetail.setProperty("timestamp", new Date());
        return problemDetail;
    }
}
