package com.calculator.calculatorapi.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.Set;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Value
public class JwtResponse {

    @NotBlank
    String accessToken;

    @NotBlank
    String tokenType;

    @NotBlank
    UUID id;

    @NotBlank
    String email;

    @NotBlank
    Set<String> roles;
}
