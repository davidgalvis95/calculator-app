package com.calculator.calculatorapi.dto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class BadJwtException extends RuntimeException{
    public BadJwtException(String message) {
        super(message);
    }
}
