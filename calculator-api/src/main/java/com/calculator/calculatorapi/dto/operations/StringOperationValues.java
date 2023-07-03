package com.calculator.calculatorapi.dto.operations;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
@Valid
public class StringOperationValues implements OperationValues {
    Double seed;

    @NotNull
    Integer size;
}
