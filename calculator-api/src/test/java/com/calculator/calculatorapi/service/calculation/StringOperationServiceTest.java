package com.calculator.calculatorapi.service.calculation;

import com.calculator.calculatorapi.config.UserDetailsInfo;
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
public class StringOperationServiceTest {

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
    
    private StringOperationService stringOperationService;
    
    private Faker faker;


    @BeforeEach
    public void setUp() {
        faker = new Faker();
        MockitoAnnotations.openMocks(this);
        stringOperationService = new StringOperationService(recordRepository, operationRepository, userRepository);
    }

    @Test
    public void testProcessOperationWithValidOperationType() {
        final StringOperationValues operands = new StringOperationValues(0.5, 10);
        final OperationRequest<StringOperationValues> operationRequest = new StringOperationRequest(operands, OperationType.RANDOM_STRING);
        final User user = new User();
        user.setId(UUID.randomUUID());
        user.setBalance(10);
        final Operation operation = new Operation();
        operation.setCost(2);
        final OperationResponse<String> expectedResponse = new OperationResponse<>(
                UUID.randomUUID(),
                user.getEmail(),
                OperationType.RANDOM_STRING,
                OperationState.SUCCEEDED,
                operands,
                2,
                8,
                LocalDateTime.now(),
                StringOperationResult.builder().result("K2tWtnztPs").build()
        );
        final UserDetailsInfo userDetailsInfo = buildUserDetailsInfo(user.getId());
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsInfo);
        when(userRepository.findUserById(any())).thenReturn(Optional.of(user));
        when(operationRepository.findOperationByType(any())).thenReturn(Optional.of(operation));

        final OperationResponse<String> response = stringOperationService.processOperation(operationRequest);

        assertEquals(expectedResponse.getUsername(), response.getUsername());
        assertEquals(expectedResponse.getType(), response.getType());
        assertEquals(expectedResponse.getState(), response.getState());
        assertEquals(expectedResponse.getOperands(), response.getOperands());
        assertEquals(expectedResponse.getOperationCost(), response.getOperationCost());
        assertEquals(expectedResponse.getCurrentUserBalance(), response.getCurrentUserBalance());
        assertEquals(expectedResponse.getOperationResult().getResult().length(), response.getOperationResult().getResult().length());
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getPrincipal();
        verify(userRepository, times(1)).findUserById(user.getId());
        verify(operationRepository, times(1)).findOperationByType(OperationType.RANDOM_STRING);
        verify(recordRepository, times(1)).save(any());
    }

    @Test
    public void testProcessOperationWithInvalidOperationType() {
        StringOperationValues operands = new StringOperationValues(0.5, 10);
        OperationRequest<StringOperationValues> operationRequest = new StringOperationRequest(operands, OperationType.ADDITION);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                stringOperationService.processOperation(operationRequest));

        String expectedMessage = "Invalid operation type, please choose one of the following: [RANDOM_STRING]";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testProcessOperationWithUnrecognizedUser() {
        StringOperationValues operands = new StringOperationValues(0.5, 10);
        OperationRequest<StringOperationValues> operationRequest = new StringOperationRequest(operands, OperationType.RANDOM_STRING);
        final String expectedMessage = "Unrecognized connected user, aborting transaction";
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);
        //When
        final BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                stringOperationService.processOperation(operationRequest));
        // Then
        assertEquals(expectedMessage, exception.getMessage());
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getPrincipal();
    }

    @Test
    public void testProcessOperationWithInsufficientBalance() {
        // Prepare test data
        StringOperationValues operands = new StringOperationValues(0.5, 10);
        OperationRequest<StringOperationValues> operationRequest = new StringOperationRequest(operands, OperationType.RANDOM_STRING);
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setBalance(2);
        final Operation operation = new Operation();
        operation.setCost(3);
        final String expectedMessage = "User has less balance than required to perform RANDOM_STRING, " +
                "current balance:  2, current cost: 3";
        final UserDetailsInfo userDetailsInfo = buildUserDetailsInfo(user.getId());
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsInfo);
        when(userRepository.findUserById(user.getId())).thenReturn(Optional.of(user));
        when(operationRepository.findOperationByType(OperationType.RANDOM_STRING)).thenReturn(Optional.of(operation));
        // When
        final IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                stringOperationService.processOperation(operationRequest));
        // Then
        assertEquals(expectedMessage, exception.getMessage());
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getPrincipal();
        verify(userRepository, times(1)).findUserById(user.getId());
        verify(operationRepository, times(1)).findOperationByType(OperationType.RANDOM_STRING);
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
