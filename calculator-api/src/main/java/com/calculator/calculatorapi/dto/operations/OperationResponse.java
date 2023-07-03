package com.calculator.calculatorapi.dto.operations;

import com.calculator.calculatorapi.models.OperationState;
import com.calculator.calculatorapi.models.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OperationResponse<T> {

    private UUID id;

    private String username;

    private OperationType type;

    private OperationState state;

    private OperationValues operands;

    private Integer operationCost;

    private Integer currentUserBalance;

    private LocalDateTime dateTime;

    private OperationResult<T> operationResult;
}
