package com.calculator.calculatorapi.service.record;

import com.calculator.calculatorapi.config.UserDetailsInfo;
import com.calculator.calculatorapi.dto.record.RecordDto;
import com.calculator.calculatorapi.dto.record.RecordListResponse;
import com.calculator.calculatorapi.models.*;
import com.calculator.calculatorapi.models.Record;
import com.calculator.calculatorapi.repository.OperationRepository;
import com.calculator.calculatorapi.repository.RecordRepository;
import com.calculator.calculatorapi.repository.UserRepository;
import com.github.javafaker.Faker;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecordServiceTest {

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private OperationRepository operationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private RecordService recordService;

    private Faker faker;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
        MockitoAnnotations.openMocks(this);
        recordService = new RecordService(recordRepository, operationRepository, userRepository);
    }

    @Test
    public void testGetUserRecordsWithoutOperationType() {
        int pageNumber = 1;
        int pageSize = 10;
        UUID userId = UUID.randomUUID();
        OperationType operationType = null;

        User user = new User();
        user.setId(userId);

        List<Record> records = new ArrayList<>();
        records.add(createRecord(userId, OperationType.MULTIPLICATION));
        records.add(createRecord(userId, OperationType.ADDITION));
        records.add(createRecord(userId, OperationType.SUBTRACTION));

        Page<Record> recordsPage = createPage(records);

        final UserDetailsInfo userDetailsInfo = buildUserDetailsInfo(userId);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsInfo);
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        when(recordRepository.findRecordsByUserIdOrderByDateTimeDesc(user, PageRequest.of(0, pageSize)))
                .thenReturn(recordsPage);

        RecordListResponse response = recordService.getUserRecords(request, pageNumber, pageSize, operationType);

        assertEquals(recordsPage.getTotalPages(), response.getTotalPages());
        assertEquals(pageNumber, response.getPage());
        assertEquals(records.size(), response.getRecords().size());

        for (int i = 0; i < records.size(); i++) {
            RecordDto expectedRecordDto = mapRecordToRecordDto(records.get(i), user);
            RecordDto actualRecordDto = response.getRecords().get(i);
            assertEquals(expectedRecordDto, actualRecordDto);
        }

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getPrincipal();
        verify(userRepository).findUserById(userId);
        verify(recordRepository).findRecordsByUserIdOrderByDateTimeDesc(user, PageRequest.of(0, pageSize));
    }

    @Test
    public void testGetUserRecordsWithOperationType() {
        int pageNumber = 1;
        int pageSize = 10;
        UUID userId = UUID.randomUUID();
        OperationType operationType = OperationType.ADDITION;

        User user = new User();
        user.setId(userId);

        List<Record> records = new ArrayList<>();
        records.add(createRecord(userId, OperationType.ADDITION));
        records.add(createRecord(userId, OperationType.ADDITION));

        Page<Record> recordsPage = createPage(records);

        final UserDetailsInfo userDetailsInfo = buildUserDetailsInfo(userId);
        final Operation operation = Operation.builder().type(OperationType.ADDITION).build();
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsInfo);
        when(userRepository.findUserById(userId)).thenReturn(Optional.of(user));
        when(operationRepository.findOperationByType(operationType)).thenReturn(Optional.of(operation));
        when(recordRepository.findRecordsByUserIdAndOperationIdOrderByDateTimeDesc(
                user,
                operation,
                PageRequest.of(0, pageSize)
        )).thenReturn(recordsPage);

        RecordListResponse response = recordService.getUserRecords(request, pageNumber, pageSize, operationType);

        assertEquals(recordsPage.getTotalPages(), response.getTotalPages());
        assertEquals(pageNumber, response.getPage());
        assertEquals(records.size(), response.getRecords().size());

        for (int i = 0; i < records.size(); i++) {
            RecordDto expectedRecordDto = mapRecordToRecordDto(records.get(i), user);
            RecordDto actualRecordDto = response.getRecords().get(i);
            assertEquals(expectedRecordDto, actualRecordDto);
        }

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getPrincipal();
        verify(userRepository).findUserById(userId);
        verify(recordRepository).findRecordsByUserIdAndOperationIdOrderByDateTimeDesc(
                user,
                operationRepository.findOperationByType(operationType).orElseThrow(),
                PageRequest.of(0, pageSize)
        );
    }

    @Test
    public void testGetUserRecordsWithInvalidUser() {
        int pageNumber = 1;
        int pageSize = 10;
        OperationType operationType = OperationType.ADDITION;

        final UserDetailsInfo userDetailsInfo = buildUserDetailsInfoWithNullId();

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsInfo);
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                recordService.getUserRecords(request, pageNumber, pageSize, operationType));

        String expectedMessage = "Unrecognized connected user, aborting transaction";
        assertEquals(expectedMessage, exception.getMessage());

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getPrincipal();
        verify(userRepository, never()).findUserById(any(UUID.class));
        verify(recordRepository, never()).findRecordsByUserIdOrderByDateTimeDesc(any(User.class), any(PageRequest.class));
        verify(recordRepository, never()).findRecordsByUserIdAndOperationIdOrderByDateTimeDesc(
                any(User.class),
                any(Operation.class),
                any(PageRequest.class)
        );
    }

    private Record createRecord(UUID userId, OperationType operationType) {
        Record record = new Record();
        record.setId(UUID.randomUUID());
        record.setAmount(100);
        record.setUserBalance(200);
        record.setDateTime(LocalDateTime.now());
        record.setOperationId(createOperation(operationType));
        return record;
    }

    private Operation createOperation(OperationType operationType) {
        Operation operation = new Operation();
        operation.setId(UUID.randomUUID());
        operation.setType(operationType);
        return operation;
    }

    private RecordDto mapRecordToRecordDto(Record record, User user) {
        Operation operation = record.getOperationId();
        return RecordDto.builder()
                .id(record.getId())
                .userId(user.getId())
                .userEmail(user.getEmail())
                .amount(record.getAmount())
                .userBalance(record.getUserBalance())
                .operationId(operation.getId())
                .operationType(operation.getType())
                .operationState(record.getOperationState())
                .dateTime(record.getDateTime())
                .build();
    }

    private <T> Page<T> createPage(List<T> content) {
        return new PageImpl<>(content, PageRequest.of(0, content.size()), content.size());
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

    private UserDetailsInfo buildUserDetailsInfoWithNullId() {
        return new UserDetailsInfo(
                User.builder()
                        .id(null)
                        .email(faker.internet().emailAddress())
                        .status(UserStatus.ACTIVE)
                        .password(faker.internet().password())
                        .roles(Set.of(new Role(UUID.randomUUID(), RoleType.USER)))
                        .balance(0)
                        .build()
        );
    }
}
