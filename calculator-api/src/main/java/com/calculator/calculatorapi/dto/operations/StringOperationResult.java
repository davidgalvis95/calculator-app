package com.calculator.calculatorapi.dto.operations;

import com.calculator.calculatorapi.dto.operations.OperationResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StringOperationResult implements OperationResult<String> {

    private String result;

    public StringOperationResult() {
    }

    @Override
    public String getResult() {
        return result;
    }
}
