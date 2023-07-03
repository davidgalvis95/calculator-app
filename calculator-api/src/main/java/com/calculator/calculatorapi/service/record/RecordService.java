package com.calculator.calculatorapi.service.record;

import com.calculator.calculatorapi.dto.exception.ObjectNotFoundException;
import com.calculator.calculatorapi.dto.record.RecordDto;
import com.calculator.calculatorapi.dto.record.RecordListResponse;
import com.calculator.calculatorapi.models.Operation;
import com.calculator.calculatorapi.models.OperationType;
import com.calculator.calculatorapi.models.Record;
import com.calculator.calculatorapi.models.User;
import com.calculator.calculatorapi.repository.OperationRepository;
import com.calculator.calculatorapi.repository.RecordRepository;
import com.calculator.calculatorapi.repository.UserRepository;
import com.calculator.calculatorapi.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RecordService {
    private final RecordRepository recordRepository;
    private final OperationRepository operationRepository;
    private final UserRepository userRepository;

    public RecordService(final RecordRepository recordRepository,
                         final OperationRepository operationRepository,
                         final UserRepository userRepository) {
        this.recordRepository = recordRepository;
        this.operationRepository = operationRepository;
        this.userRepository = userRepository;
    }

    public RecordListResponse getUserRecords(final HttpServletRequest request,
                                             final int pageNumber,
                                             final int pageSize,
                                             final OperationType operationType) {
        final UUID userId = Utils.getLoggedUserId();
        if (userId != null) {
            final User user = userRepository.findUserById(userId)
                    .orElseThrow(() -> new ObjectNotFoundException("User with id " + userId + " not found"));
            Page<Record> recordsPage;
            String operationsTypeString = null;

            if (operationType == null) {
                recordsPage = getRecords(pageNumber, pageSize, user);
            } else {
                recordsPage = getRecordsByOperationType(pageNumber, pageSize, operationType, user);
                operationsTypeString = "operationType=" + operationType;
            }

            final int totalPages = recordsPage.getTotalPages();
            final String nextPageUri = Utils.convertCurrentUriToNextPageUri(
                    request,
                    pageNumber,
                    pageSize,
                    operationsTypeString,
                    totalPages);

            final List<Record> records = recordsPage.getContent();
            final List<RecordDto> recordDtos = records.stream()
                    .map(record -> mapRecordToRecordDto(record, user))
                    .toList();

            return RecordListResponse.builder()
                    .totalPages(totalPages)
                    .page(pageNumber)
                    .nextPageToken(nextPageUri)
                    .records(recordDtos)
                    .build();
        } else {
            throw new BadCredentialsException("Unrecognized connected user, aborting transaction");
        }
    }

    private Page<Record> getRecordsByOperationType(int pageNumber, int pageSize, OperationType operationType, User user) {
        final Operation operation = operationRepository.findOperationByType(operationType)
                .orElseThrow(() -> new ObjectNotFoundException("Could not find operation for type " + operationType));
        return recordRepository.findRecordsByUserIdAndOperationIdOrderByDateTimeDesc(
                user,
                operation,
                PageRequest.of(pageNumber - 1, pageSize)
        );
    }

    private Page<Record> getRecords(int pageNumber, int pageSize, User user) {
        return recordRepository.findRecordsByUserIdOrderByDateTimeDesc(
                user,
                PageRequest.of(pageNumber - 1, pageSize)
        );
    }

    private RecordDto mapRecordToRecordDto(Record record, User user) {

        final Operation operation = record.getOperationId();
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
}
