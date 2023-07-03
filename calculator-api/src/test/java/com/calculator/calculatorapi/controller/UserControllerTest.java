package com.calculator.calculatorapi.controller;

import com.calculator.calculatorapi.dto.StandardResponseDto;
import com.calculator.calculatorapi.dto.user.UserDto;
import com.calculator.calculatorapi.dto.user.UserListResponse;
import com.calculator.calculatorapi.models.RoleType;
import com.calculator.calculatorapi.models.UserStatus;
import com.calculator.calculatorapi.service.user.UserService;
import com.github.javafaker.Faker;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    private UserController userController;

    private Faker faker;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
    }

    @Test
    public void testAddUserBalance() {
        // Given
        final int newBalance = 100;
        final StandardResponseDto<?> expectedResponseDto = new StandardResponseDto<>(
                null,
                "Balance amount of 100 added to the user, new balance: 100",
                null
        );
        when(userService.addBalanceToUser()).thenReturn(newBalance);
        // When
        final ResponseEntity<StandardResponseDto<?>> response = userController.addUserBalance();
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponseDto, response.getBody());
        verify(userService, times(1)).addBalanceToUser();
    }

    @Test
    public void testDeactivateUser() {
        // Given
        final UUID userId = UUID.randomUUID();
        final StandardResponseDto<?> expectedResponseDto = new StandardResponseDto<>(
                null,
                "User deactivated successfully",
                null
        );
        // When
        final ResponseEntity<StandardResponseDto<?>> response = userController.deactivateUser(userId);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponseDto, response.getBody());
        verify(userService, times(1)).switchUserStatus(true, userId);
    }

    @Test
    public void testActivateUser() {
        // Given
        final UUID userId = UUID.randomUUID();
        final StandardResponseDto<?> expectedResponseDto = new StandardResponseDto<>(
                null,
                "User activated successfully",
                null
        );
        // When
        final ResponseEntity<StandardResponseDto<?>> response = userController.activateUser(userId);
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponseDto, response.getBody());
        verify(userService, times(1)).switchUserStatus(false, userId);
    }

    @Test
    public void testGetUsers() {
        // Given
        final int pageNumber = 1;
        final int pageSize = 10;
        final UserStatus userStatus = UserStatus.ACTIVE;
        final UserListResponse userListResponse = buildUserListResponse();
        final StandardResponseDto<UserListResponse> expectedResponseDto = new StandardResponseDto<>(
                userListResponse,
                "Records retrieved successfully",
                null
        );
        when(userService.getCalculatorUsers(
                any(HttpServletRequest.class),
                eq(pageNumber),
                eq(pageSize),
                eq(userStatus))
        ).thenReturn(userListResponse);
        // When
        final ResponseEntity<StandardResponseDto<UserListResponse>> response = userController.getUsers(
                request, pageNumber, pageSize, userStatus.name());
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponseDto, response.getBody());
        verify(userService, times(1)).getCalculatorUsers(request, pageNumber, pageSize, userStatus);
    }

    private UserListResponse buildUserListResponse() {
        return UserListResponse.builder()
                .page(1)
                .totalPages(5)
                .nextPageToken(faker.internet().url())
                .users(List.of(
                        UserDto.builder()
                                .balance(100)
                                .email(faker.internet().emailAddress())
                                .status(UserStatus.ACTIVE)
                                .roles(Set.of(RoleType.USER.name()))
                                .id(UUID.randomUUID())
                                .build()
                ))
                .build();
    }
}
