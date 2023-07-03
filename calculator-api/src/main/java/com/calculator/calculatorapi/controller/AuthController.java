package com.calculator.calculatorapi.controller;

import com.calculator.calculatorapi.dto.StandardResponseDto;
import com.calculator.calculatorapi.dto.authentication.JwtResponse;
import com.calculator.calculatorapi.dto.authentication.LoginRequest;
import com.calculator.calculatorapi.dto.authentication.SignupRequest;
import com.calculator.calculatorapi.dto.user.UserDto;
import com.calculator.calculatorapi.service.security.AuthenticationService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signin")
    public ResponseEntity<StandardResponseDto<JwtResponse>> authenticateUser(
            @Valid @RequestBody final LoginRequest loginRequest
    ) {
        final StandardResponseDto<JwtResponse> standardResponseDto = new StandardResponseDto<>(
                authenticationService.authenticateUser(loginRequest),
                "User authenticated successfully",
                null
        );
        return ResponseEntity.ok().body(standardResponseDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<StandardResponseDto<UserDto>> registerUser(
            @Valid @RequestBody final SignupRequest signUpRequest
    ) {
        final StandardResponseDto<UserDto> standardResponseDto = new StandardResponseDto<>(
                authenticationService.registerUser(signUpRequest),
                "User created successfully",
                null
        );
        return ResponseEntity.ok().body(standardResponseDto);
    }
}
