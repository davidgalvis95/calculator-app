package com.calculator.calculatorapi.dto.operations;

import com.calculator.calculatorapi.config.serialization.OperationRequestDeserializer;
import com.calculator.calculatorapi.models.OperationType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = OperationRequestDeserializer.class)
public interface OperationRequest<T extends OperationValues> {
    T getOperands();
    OperationType getOperationType();
}
