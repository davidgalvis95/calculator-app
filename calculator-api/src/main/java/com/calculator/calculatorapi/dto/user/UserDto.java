package com.calculator.calculatorapi.dto.user;

import com.calculator.calculatorapi.models.Role;
import com.calculator.calculatorapi.models.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Value
@Builder
public class UserDto {
    UUID id;

    String email;

    UserStatus status;

    Integer balance;

    Set<String> roles;
}
