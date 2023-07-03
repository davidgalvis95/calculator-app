package com.calculator.calculatorapi.controller;

import com.calculator.calculatorapi.dto.StandardResponseDto;
import com.calculator.calculatorapi.dto.operations.*;
import com.calculator.calculatorapi.dto.record.RecordDto;
import com.calculator.calculatorapi.dto.record.RecordListResponse;
import com.calculator.calculatorapi.models.OperationState;
import com.calculator.calculatorapi.models.OperationType;
import com.calculator.calculatorapi.service.calculation.CalculationService;
import com.calculator.calculatorapi.service.record.RecordService;
import com.github.javafaker.Faker;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperationsControllerTest<T extends OperationValues, U> {

    @Mock
    private CalculationService<T, U> calculationService;

    @Mock
    private RecordService recordService;

    @Mock
    private HttpServletRequest request;

    private OperationsController<T, U> operationsController;

    private Faker faker;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
        operationsController = new OperationsController<>(calculationService, recordService);
    }

    @Test
    public void testGetUserOperationRecords() {
        // Given
        final int pageNumber = 1;
        final int pageSize = 10;
        final OperationType operationType = OperationType.ADDITION;
        final RecordListResponse recordListResponse = buildRecordListResponseDto();
        final StandardResponseDto<RecordListResponse> expectedResponseDto = new StandardResponseDto<>(
                recordListResponse,
                "Records retrieved successfully",
                null
        );
        when(recordService.getUserRecords(
                any(HttpServletRequest.class),
                eq(pageNumber),
                eq(pageSize),
                eq(operationType))
        ).thenReturn(recordListResponse);

        // When
        final ResponseEntity<StandardResponseDto<RecordListResponse>> response = operationsController.getUserOperationRecords(
                request, pageNumber, pageSize, operationType.name());

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponseDto, response.getBody());
        verify(recordService, times(1)).getUserRecords(request, pageNumber, pageSize, operationType);
    }

    @Test
    public void testCalculate() {
        // Given
        final OperationRequest<T> operationRequest = (OperationRequest<T>) new NumberOperationRequest();
        final OperationResponse<U> operationResponse = (OperationResponse<U>) buildOperationResult();
        final StandardResponseDto<OperationResponse<U>> expectedResponseDto = new StandardResponseDto<>(
                operationResponse,
                "Operation completed",
                null
        );
        when(calculationService.processOperation(operationRequest)).thenReturn(operationResponse);
        // When
        final ResponseEntity<StandardResponseDto<OperationResponse<U>>> response = operationsController.calculate(operationRequest);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponseDto, response.getBody());
        verify(calculationService, times(1)).processOperation(operationRequest);
    }

    private RecordListResponse buildRecordListResponseDto() {
        return RecordListResponse.builder()
                .page(1)
                .totalPages(5)
                .nextPageToken(faker.internet().url())
                .records(List.of(RecordDto.builder()
                        .id(UUID.randomUUID())
                        .operationType(OperationType.ADDITION)
                        .operationId(UUID.randomUUID())
                        .operationState(OperationState.SUCCEEDED)
                        .amount(3)
                        .userId(UUID.randomUUID())
                        .dateTime(LocalDateTime.now())
                        .userBalance(97)
                        .userEmail(faker.internet().emailAddress())
                        .build()))
                .build();
    }

    private OperationResponse<Double> buildOperationResult() {
        final OperationResult<Double> operationResult = new NumberOperationResult(10);
        return new OperationResponse<>(
                UUID.randomUUID(),
                faker.internet().emailAddress(),
                OperationType.ADDITION,
                OperationState.SUCCEEDED,
                new NumberOperationValues(5, 5),
                1,
                99,
                LocalDateTime.now(),
                operationResult
        );
    }
}
