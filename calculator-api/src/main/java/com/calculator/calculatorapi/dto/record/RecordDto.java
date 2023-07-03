package com.calculator.calculatorapi.dto.record;

import com.calculator.calculatorapi.models.Operation;
import com.calculator.calculatorapi.models.OperationState;
import com.calculator.calculatorapi.models.OperationType;
import com.calculator.calculatorapi.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class RecordDto {
    private UUID id;

    private UUID userId;

    private String userEmail;

    private Integer amount;

    private Integer userBalance;

    private UUID operationId;

    private OperationType operationType;

    private OperationState operationState;

    private LocalDateTime dateTime;

    public RecordDto() {}
}
