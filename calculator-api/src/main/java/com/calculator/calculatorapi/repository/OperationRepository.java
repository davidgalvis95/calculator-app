package com.calculator.calculatorapi.repository;

import com.calculator.calculatorapi.models.Operation;
import com.calculator.calculatorapi.models.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, Long> {
    Optional<Operation> findOperationByType(OperationType operationType);

}
