package com.calculator.calculatorapi.service.calculation;

import com.calculator.calculatorapi.dto.operations.OperationRequest;
import com.calculator.calculatorapi.dto.operations.OperationResponse;
import com.calculator.calculatorapi.dto.operations.OperationValues;
import org.springframework.stereotype.Service;

@Service
public interface CalculationService<T extends OperationValues, U> {
    OperationResponse<U> processOperation(OperationRequest<T> operationRequest);
}
