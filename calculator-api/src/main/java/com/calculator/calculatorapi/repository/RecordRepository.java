package com.calculator.calculatorapi.repository;

import com.calculator.calculatorapi.models.Operation;
import com.calculator.calculatorapi.models.Record;
import com.calculator.calculatorapi.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    Optional<Record> findRecordById(UUID id);
    Page<Record> findRecordsByUserIdOrderByDateTimeDesc(User user, Pageable pageable);
    Page<Record> findRecordsByUserIdAndOperationIdOrderByDateTimeDesc(User user, Operation operation, Pageable pageable);
}
