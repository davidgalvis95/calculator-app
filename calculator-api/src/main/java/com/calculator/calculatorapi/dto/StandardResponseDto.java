package com.calculator.calculatorapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class StandardResponseDto<V> {
    private V payload;
    private String message;
    private String error;
}
