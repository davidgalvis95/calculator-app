package com.calculator.calculatorapi.service.calculation;


import com.calculator.calculatorapi.dto.operations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Primary
@RequiredArgsConstructor
public class CalculationServiceProxy<T extends OperationValues, U> implements CalculationService<T, U> {
    private final List<CalculationService<T, U>> importantServices;

    @Override
    public OperationResponse<U> processOperation(OperationRequest<T> operationRequest) {
        return getImportantService(operationRequest).processOperation(operationRequest);
    }

    private CalculationService<T, U> getImportantService(OperationRequest<T> request) {
        if(request instanceof StringOperationRequest) {
            return matchService(StringOperationService.class);
        }else if (request instanceof NumberOperationRequest) {
            return matchService(NumberOperationsService.class);
        }else {
            throw new RuntimeException("No service instance of " + CalculationService.class + " matches required criteria");
        }
    }

    private CalculationService<T, U> matchService(Class<?> importantServiceClass) {
        return importantServices.stream()
                .filter(importantServiceClass::isInstance)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No service instance of " + CalculationService.class + " matches required criteria"));
    }
}
