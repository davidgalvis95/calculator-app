package com.calculator.calculatorapi.dto.operations;

import com.calculator.calculatorapi.models.OperationType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
public class NumberOperationRequest implements OperationRequest<NumberOperationValues> {

    @NotNull
    private NumberOperationValues operands;

    @Getter
    @NotNull
    private OperationType operationType;

    @Override
    public NumberOperationValues getOperands() {
        return operands;
    }

    public NumberOperationRequest() {}
}
