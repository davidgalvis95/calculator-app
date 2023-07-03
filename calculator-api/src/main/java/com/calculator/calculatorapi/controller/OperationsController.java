package com.calculator.calculatorapi.controller;

import com.calculator.calculatorapi.dto.StandardResponseDto;
import com.calculator.calculatorapi.dto.operations.OperationRequest;
import com.calculator.calculatorapi.dto.operations.OperationResponse;
import com.calculator.calculatorapi.dto.operations.OperationValues;
import com.calculator.calculatorapi.dto.record.RecordListResponse;
import com.calculator.calculatorapi.dto.user.UserDto;
import com.calculator.calculatorapi.models.OperationType;
import com.calculator.calculatorapi.service.calculation.CalculationService;
import com.calculator.calculatorapi.service.record.RecordService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1")
@Slf4j
public class OperationsController<T extends OperationValues, U> {

    private final CalculationService<T, U> calculationService;

    private final RecordService recordService;

    @Autowired
    public OperationsController(final CalculationService<T, U> calculationService,
                                final RecordService recordService) {
        this.calculationService = calculationService;
        this.recordService = recordService;
    }

    @GetMapping("/records")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<StandardResponseDto<RecordListResponse>> getUserOperationRecords(
            final HttpServletRequest request,
            @RequestParam @NotNull final int pageNumber,
            @RequestParam @NotNull final int pageSize,
            @RequestParam final String operationType
    ) {
        OperationType operationsTypeConverted;
        try {
            operationsTypeConverted = OperationType.valueOf(operationType);
        } catch (IllegalArgumentException | NullPointerException e) {
            log.info("Operation type " + operationType + " not found, defaulting to null");
            operationsTypeConverted = null;
        }
        final RecordListResponse recordListResponse = recordService.getUserRecords(
                request,
                pageNumber,
                pageSize,
                operationsTypeConverted
        );
        final StandardResponseDto<RecordListResponse> standardResponseDto = new StandardResponseDto<>(
                recordListResponse,
                "Records retrieved successfully",
                null
        );
        return ResponseEntity.ok(standardResponseDto);
    }

    @PostMapping("/calculate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<StandardResponseDto<OperationResponse<U>>> calculate(
            @Valid @RequestBody OperationRequest<T> operationRequest
    ) {
        final StandardResponseDto<OperationResponse<U>> standardResponseDto = new StandardResponseDto<>(
                calculationService.processOperation(operationRequest),
                "Operation completed",
                null
        );
        return ResponseEntity.ok(standardResponseDto);
    }
}
