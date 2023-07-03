package com.calculator.calculatorapi.models;

public enum UserStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    final String value;
    UserStatus(String value) {
        this.value = value;
    }
}
