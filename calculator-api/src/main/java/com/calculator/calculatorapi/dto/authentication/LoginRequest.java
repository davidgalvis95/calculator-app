package com.calculator.calculatorapi.dto.authentication;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
@Valid
public class LoginRequest {
    @NotNull
    String email;

    @NotNull
    String password;
}
