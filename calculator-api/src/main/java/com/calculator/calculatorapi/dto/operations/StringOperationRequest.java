package com.calculator.calculatorapi.dto.operations;

import com.calculator.calculatorapi.models.OperationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
public class StringOperationRequest implements OperationRequest<StringOperationValues> {

    @NotNull
    private StringOperationValues operands;

    @Getter
    @NotNull
    private OperationType operationType;

    @Override
    public StringOperationValues getOperands() {
        return operands;
    }

    public StringOperationRequest() {}
}
