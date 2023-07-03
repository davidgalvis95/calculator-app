package com.calculator.calculatorapi.controller;

import com.calculator.calculatorapi.dto.StandardResponseDto;
import com.calculator.calculatorapi.dto.authentication.JwtResponse;
import com.calculator.calculatorapi.dto.authentication.LoginRequest;
import com.calculator.calculatorapi.dto.authentication.SignupRequest;
import com.calculator.calculatorapi.dto.user.UserDto;
import com.calculator.calculatorapi.models.RoleType;
import com.calculator.calculatorapi.models.UserStatus;
import com.calculator.calculatorapi.service.security.AuthenticationService;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    private Faker faker;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
    }

    @Test
    public void testAuthenticateUser() {
        // Given
        final LoginRequest loginRequest = new LoginRequest(faker.internet().emailAddress(), faker.internet().password());
        final JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken("jhfvduv7eavfadsvfdvflisav8qwegt8r4brfv")
                .id(UUID.randomUUID())
                .email(loginRequest.getEmail())
                .roles(Set.of(RoleType.USER.name()))
                .tokenType("Bearer")
                .build();
        final StandardResponseDto<JwtResponse> expectedResponseDto = new StandardResponseDto<>(
                jwtResponse,
                "User authenticated successfully",
                null
        );
        when(authenticationService.authenticateUser(loginRequest)).thenReturn(jwtResponse);
        // When
        final ResponseEntity<StandardResponseDto<JwtResponse>> response = authController.authenticateUser(loginRequest);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponseDto, response.getBody());
        verify(authenticationService, times(1)).authenticateUser(loginRequest);
    }

    @Test
    public void testRegisterUser() {
        // Given
        final SignupRequest signupRequest = new SignupRequest(faker.internet().emailAddress(), faker.internet().password(), UserStatus.ACTIVE, Set.of(RoleType.USER));
        final UserDto userDto = UserDto.builder()
                .balance(100)
                .email(signupRequest.getEmail())
                .status(signupRequest.getStatus())
                .roles(Set.of(RoleType.USER.name()))
                .id(UUID.randomUUID())
                .build();
        final StandardResponseDto<UserDto> expectedResponseDto = new StandardResponseDto<>(
                userDto,
                "User created successfully",
                null
        );
        when(authenticationService.registerUser(signupRequest)).thenReturn(userDto);
        // When
        final ResponseEntity<StandardResponseDto<UserDto>> response = authController.registerUser(signupRequest);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponseDto, response.getBody());
        verify(authenticationService, times(1)).registerUser(signupRequest);
    }
}
