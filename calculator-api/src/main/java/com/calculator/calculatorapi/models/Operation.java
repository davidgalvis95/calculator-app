package com.calculator.calculatorapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "operation")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "operation_id")
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(unique=true)
    private OperationType type;

    @NotNull
    private Integer cost;

    @OneToMany(mappedBy = "operationId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Record> records;

    public Operation() {}
}
