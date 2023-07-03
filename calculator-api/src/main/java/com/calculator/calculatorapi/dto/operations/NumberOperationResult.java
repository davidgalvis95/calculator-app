package com.calculator.calculatorapi.dto.operations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NumberOperationResult implements OperationResult<Double> {

    private double result;

    public NumberOperationResult() {
    }

    @Override
    public Double getResult() {
        return result;
    }
}
