package com.calculator.calculatorapi.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "record")
public class Record {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "record_id")
    private UUID id;

    @NotNull
    @ManyToOne()
    @JoinColumn(name = "operation_id")
    private Operation operationId;

    @NotNull
    @ManyToOne()
    @JoinColumn(name = "user_id")
    private User userId;

    @NotNull
    private Integer amount;

    @NotNull
    private Integer userBalance;

    @Enumerated(EnumType.STRING)
    @NotNull
    private OperationState operationState;

    @NotNull
    private LocalDateTime dateTime;

    public Record() {}
}
