package com.calculator.calculatorapi.service.calculation;

import com.calculator.calculatorapi.config.UserDetailsInfo;
import com.calculator.calculatorapi.dto.exception.ObjectNotFoundException;
import com.calculator.calculatorapi.dto.operations.*;
import com.calculator.calculatorapi.models.*;
import com.calculator.calculatorapi.repository.OperationRepository;
import com.calculator.calculatorapi.repository.RecordRepository;
import com.calculator.calculatorapi.repository.UserRepository;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NumberOperationsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private NumberOperationsService numberOperationsService;

    private Faker faker;


    @BeforeEach
    public void setUp() {
        faker = new Faker();
        MockitoAnnotations.openMocks(this);
        numberOperationsService = new NumberOperationsService(recordRepository, operationRepository, userRepository);
    }

    @Test
    public void testProcessOperationWithAddition() {
        final NumberOperationValues operands = new NumberOperationValues(5, 3);
        final OperationRequest<NumberOperationValues> operationRequest =
                new NumberOperationRequest(operands, OperationType.ADDITION);
        final User user = new User();
        user.setId(UUID.randomUUID());
        user.setBalance(10);
        final Operation operation = new Operation();
        operation.setCost(2);
        final OperationResponse<Double> expectedResponse = new OperationResponse<>(
                UUID.randomUUID(),
                user.getEmail(),
                OperationType.ADDITION,
                OperationState.SUCCEEDED,
                operands,
                operation.getCost(),
                user.getBalance() - operation.getCost(),
                LocalDateTime.now(),
                NumberOperationResult.builder().result(8.0).build()
        );
        final UserDetailsInfo userDetailsInfo = buildUserDetailsInfo(user.getId());
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsInfo);
        when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
        when(operationRepository.findOperationByType(OperationType.ADDITION)).thenReturn(Optional.of(operation));

        final OperationResponse<Double> response = numberOperationsService.processOperation(operationRequest);

        assertEquals(expectedResponse.getUsername(), response.getUsername());
        assertEquals(expectedResponse.getType(), response.getType());
        assertEquals(expectedResponse.getState(), response.getState());
        assertEquals(expectedResponse.getOperands(), response.getOperands());
        assertEquals(expectedResponse.getOperationCost(), response.getOperationCost());
        assertEquals(expectedResponse.getCurrentUserBalance(), response.getCurrentUserBalance());
        assertEquals(expectedResponse.getOperationResult(), response.getOperationResult());
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getPrincipal();
        verify(recordRepository, times(1)).save(any());
        verify(userRepository, times(1)).findUserById(user.getId());
        verify(operationRepository, times(1)).findOperationByType(OperationType.ADDITION);
    }

    @Test
    public void testProcessOperationWithInvalidOperationType() {
        // Given
        final NumberOperationValues operands = new NumberOperationValues(5, 3);
        final OperationRequest<NumberOperationValues> operationRequest = new NumberOperationRequest(operands, OperationType.UNKNOWN);
        // When
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                numberOperationsService.processOperation(operationRequest));
        // Then
        final String expectedMessage = "Invalid operation type: UNKNOWN, please choose one of the following: [ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, SQUARE_ROOT]";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testProcessOperationWithUserNotFoundInRepo() {
        // Given
        final NumberOperationValues operands = new NumberOperationValues(5, 3);
        final OperationRequest<NumberOperationValues> operationRequest = new NumberOperationRequest(operands, OperationType.SUBTRACTION);
        final UUID assumedUserId = UUID.randomUUID();
        final String expectedMessage = "User with id " +assumedUserId+ " not found";
        final UserDetailsInfo userDetailsInfo = buildUserDetailsInfo(assumedUserId);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsInfo);
        when(userRepository.findUserById(assumedUserId)).thenReturn(Optional.empty());
        //When
        final ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                numberOperationsService.processOperation(operationRequest));
        // Then
        assertEquals(expectedMessage, exception.getMessage());
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getPrincipal();
        verify(userRepository, times(1)).findUserById(assumedUserId);
    }

    @Test
    public void testProcessOperationWithUnrecognizedUser() {
        // Given
        final NumberOperationValues operands = new NumberOperationValues(5, 3);
        final OperationRequest<NumberOperationValues> operationRequest = new NumberOperationRequest(operands, OperationType.SUBTRACTION);
        final String expectedMessage = "Unrecognized connected user, aborting transaction";
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);
        //When
        final BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                numberOperationsService.processOperation(operationRequest));
        // Then
        assertEquals(expectedMessage, exception.getMessage());
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getPrincipal();
    }

    @Test
    public void testProcessOperationWithInsufficientBalance() {
        // Given
        final NumberOperationValues operands = new NumberOperationValues(5, 3);
        final OperationRequest<NumberOperationValues> operationRequest =
                new NumberOperationRequest(operands, OperationType.MULTIPLICATION);
        final User user = new User();
        user.setId(UUID.randomUUID());
        user.setBalance(2);
        final Operation operation = new Operation();
        operation.setCost(3);
        final String expectedMessage = "User has less balance than required to perform MULTIPLICATION, " +
                "current balance:  2, current cost: 3";
        final UserDetailsInfo userDetailsInfo = buildUserDetailsInfo(user.getId());
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsInfo);
        when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
        when(operationRepository.findOperationByType(OperationType.MULTIPLICATION)).thenReturn(Optional.of(operation));
        // When
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                numberOperationsService.processOperation(operationRequest));
        // Then
        assertEquals(expectedMessage, exception.getMessage());
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getPrincipal();
        verify(userRepository, times(1)).findUserById(user.getId());
        verify(operationRepository, times(1)).findOperationByType(OperationType.MULTIPLICATION);
    }

    private UserDetailsInfo buildUserDetailsInfo(UUID userId) {
        return new UserDetailsInfo(
                User.builder()
                        .id(userId)
                        .email(faker.internet().emailAddress())
                        .status(UserStatus.ACTIVE)
                        .password(faker.internet().password())
                        .roles(Set.of(new Role(UUID.randomUUID(), RoleType.USER)))
                        .balance(0)
                        .build()
        );
    }
}