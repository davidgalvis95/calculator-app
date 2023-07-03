package com.calculator.calculatorapi.config.handler;

import com.calculator.calculatorapi.dto.exception.ObjectNotFoundException;
import com.calculator.calculatorapi.dto.StandardResponseDto;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(ConstraintViolationException ex) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(BadCredentialsException ex) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(IllegalArgumentException ex) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(IllegalStateException ex) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler({ExpiredJwtException.class, UnsupportedJwtException.class, MalformedJwtException.class, SignatureException.class})
    public ResponseEntity<StandardResponseDto<?>> handler(SecurityException ex) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), "Jwt is either expired or malformed")
                , HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({UsernameNotFoundException.class, AuthenticationServiceException.class})
    public ResponseEntity<StandardResponseDto<?>> handler(AuthenticationException ex) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), "Error while authenticating the user")
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(ObjectNotFoundException ex) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null)
                , HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<StandardResponseDto<?>> handler(RuntimeException ex) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponseDto<?>> handler(Exception ex) {
        return new ResponseEntity<>(buildStandardResponse(ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private StandardResponseDto<?> buildStandardResponse(final String errorMessage, final String customMessage) {
        return new StandardResponseDto<>(
                null,
                customMessage,
                errorMessage
        );
    }
}
