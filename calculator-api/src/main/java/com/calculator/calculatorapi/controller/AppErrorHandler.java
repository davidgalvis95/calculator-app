package com.calculator.calculatorapi.controller;

import com.calculator.calculatorapi.dto.exception.BadJwtException;
import com.calculator.calculatorapi.dto.exception.ObjectNotFoundException;
import com.calculator.calculatorapi.dto.StandardResponseDto;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@AllArgsConstructor
@RestControllerAdvice
public class AppErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(ConstraintViolationException ex, WebRequest request) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(BadCredentialsException ex, WebRequest request) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(IllegalArgumentException ex, WebRequest request) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(IllegalStateException ex, WebRequest request) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(ExpiredJwtException ex, WebRequest request) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), "Jwt is either expired or malformed")
                , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadJwtException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(SecurityException ex, WebRequest request) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null)
                , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UsernameNotFoundException.class, AuthenticationServiceException.class})
    public ResponseEntity<StandardResponseDto<?>> handler(AuthenticationException ex, WebRequest request) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), "Error while authenticating the user")
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(ObjectNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null)
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(RuntimeException ex, WebRequest request) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponseDto<?>> handler(Exception ex, WebRequest request) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<Object> handleAccessDeniedException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), "Access denied to resource"), HttpStatus.FORBIDDEN);
    }

    private StandardResponseDto<?> buildStandardResponse(final String errorMessage, final String customMessage) {
        return new StandardResponseDto<>(
                null,
                customMessage,
                errorMessage
        );
    }
}
