package com.calculator.calculatorapi.service.calculation;

import com.calculator.calculatorapi.dto.exception.ObjectNotFoundException;
import com.calculator.calculatorapi.dto.operations.NumberOperationResult;
import com.calculator.calculatorapi.dto.operations.NumberOperationValues;
import com.calculator.calculatorapi.dto.operations.OperationRequest;
import com.calculator.calculatorapi.dto.operations.OperationResponse;
import com.calculator.calculatorapi.models.*;
import com.calculator.calculatorapi.models.Record;
import com.calculator.calculatorapi.repository.OperationRepository;
import com.calculator.calculatorapi.repository.RecordRepository;
import com.calculator.calculatorapi.repository.UserRepository;
import com.calculator.calculatorapi.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NumberOperationsService implements CalculationService<NumberOperationValues, Double> {

    private final RecordRepository recordRepository;
    private final OperationRepository operationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NumberOperationsService(final RecordRepository recordRepository,
                                   final OperationRepository operationRepository, UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.operationRepository = operationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OperationResponse<Double> processOperation(OperationRequest<NumberOperationValues> operationRequest) {
        final int a = operationRequest.getOperands().getA();
        final int b = operationRequest.getOperands().getB();

        double result = switch (operationRequest.getOperationType()) {
            case ADDITION -> a + b;
            case SUBTRACTION -> a - b;
            case MULTIPLICATION -> a * b;
            case DIVISION -> (double) a / (double) b;
            case SQUARE_ROOT -> Math.sqrt(a);
            default -> throw new IllegalStateException("Invalid operation type: " + operationRequest.getOperationType() +
                    ", please choose one of the following: [ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, SQUARE_ROOT]");
        };

        final UUID userId = Utils.getLoggedUserId();
        if (userId != null) {
            return processOperationResult(operationRequest, result, userId, a);
        } else {
            throw new BadCredentialsException("Unrecognized connected user, aborting transaction");
        }
    }

    private OperationResponse<Double> processOperationResult(
            final OperationRequest<NumberOperationValues> operationRequest,
            final double result,
            final UUID userId,
            final int a) {
        final User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id " + userId + " not found"));
        final OperationType operationType = operationRequest.getOperationType();
        final Operation operation = operationRepository.findOperationByType(operationType)
                .orElseThrow(() -> new ObjectNotFoundException("Operation of type " + operationType + " not found"));
        final int userBalance = user.getBalance();
        final int operationCost = operation.getCost();
        if (userBalance < operationCost) {
            throw new IllegalStateException("User has less balance than required to perform " + operationType +
                    ", current balance:  " + userBalance + ", current cost: " + operationCost);
        }

        final int newUserBalance = user.getBalance() - operationCost;
        user.setBalance(newUserBalance);
        final LocalDateTime dateTime = LocalDateTime.now();

        recordRepository.save(Record.builder()
                .amount(operationCost)
                .dateTime(dateTime)
                .userBalance(newUserBalance)
                .userId(user)
                .operationId(operation)
                .operationState(OperationState.SUCCEEDED)
                .build());

        return new OperationResponse<>(
                UUID.randomUUID(),
                user.getEmail(),
                operationType,
                OperationState.SUCCEEDED,
                operationType == OperationType.SQUARE_ROOT ? new NumberOperationValues(a, null) : operationRequest.getOperands(),
                operationCost,
                newUserBalance,
                dateTime,
                NumberOperationResult.builder().result(result).build());
    }
}
