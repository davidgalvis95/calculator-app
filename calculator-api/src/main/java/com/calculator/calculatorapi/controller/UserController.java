package com.calculator.calculatorapi.controller;

import com.calculator.calculatorapi.dto.StandardResponseDto;
import com.calculator.calculatorapi.dto.user.UserListResponse;
import com.calculator.calculatorapi.models.UserStatus;
import com.calculator.calculatorapi.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/balance-funding")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<StandardResponseDto<?>> addUserBalance() {
        final int newBalance = userService.addBalanceToUser();
        final StandardResponseDto<?> standardResponseDto = new StandardResponseDto<>(
                null,
                "Balance amount of " + UserService.USER_BALANCE + " added to the user, new balance: " + newBalance,
                null
        );
        return ResponseEntity.ok().body(standardResponseDto);
    }

    @PostMapping("/deactivate/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDto<?>> deactivateUser(@PathVariable final UUID userId) {
        userService.switchUserStatus(true, userId);
        final StandardResponseDto<?> standardResponseDto = new StandardResponseDto<>(
                null,
                "User deactivated successfully",
                null
        );
        return ResponseEntity.ok().body(standardResponseDto);
    }

    @PostMapping("/activate/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDto<?>> activateUser(@PathVariable final UUID userId) {
        userService.switchUserStatus(false, userId);
        final StandardResponseDto<?> standardResponseDto = new StandardResponseDto<>(
                null,
                "User activated successfully",
                null
        );
        return ResponseEntity.ok().body(standardResponseDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDto<UserListResponse>> getUsers(
            final HttpServletRequest request,
            @RequestParam @NotNull final int pageNumber,
            @RequestParam @NotNull final int pageSize,
            @RequestParam String userStatus
    ) {

        UserStatus userStatusConverted;
        try {
            userStatusConverted = UserStatus.valueOf(userStatus);
        } catch (IllegalArgumentException | NullPointerException e) {
            log.info("User status " + userStatus + " not found, defaulting to null");
            userStatusConverted = null;
        }
        final UserListResponse userListResponse = userService.getCalculatorUsers(
                request,
                pageNumber,
                pageSize,
                userStatusConverted
        );
        final StandardResponseDto<UserListResponse> standardResponseDto = new StandardResponseDto<>(
                userListResponse,
                "Records retrieved successfully",
                null
        );
        return ResponseEntity.ok(standardResponseDto);
    }
}
