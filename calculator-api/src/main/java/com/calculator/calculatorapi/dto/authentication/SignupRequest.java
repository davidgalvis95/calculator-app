package com.calculator.calculatorapi.dto.authentication;

import com.calculator.calculatorapi.models.RoleType;
import com.calculator.calculatorapi.models.UserStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.util.Set;

@Value
@Valid
public class SignupRequest {

    @NotNull
    String email;

    @NotNull
    String password;

    @NotNull
    UserStatus status;

    @NotNull
    @Size(min = 1)
    Set<RoleType> roles;
}
