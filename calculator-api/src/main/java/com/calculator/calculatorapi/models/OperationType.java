package com.calculator.calculatorapi.models;

public enum OperationType {
    ADDITION("addition"),
    SUBTRACTION("subtraction"),
    MULTIPLICATION("multiplication"),
    DIVISION("division"),
    SQUARE_ROOT("square root"),
    RANDOM_STRING("random string"),
    UNKNOWN("unknown");

    private final String value;
    OperationType(String value) {
        this.value = value;
    }

    public String value(OperationType operationType) {
        return operationType.value;
    }
}
