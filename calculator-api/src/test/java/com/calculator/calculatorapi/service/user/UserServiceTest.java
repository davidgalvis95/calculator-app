package com.calculator.calculatorapi.service.user;

import com.calculator.calculatorapi.config.UserDetailsInfo;
import com.calculator.calculatorapi.dto.exception.ObjectNotFoundException;
import com.calculator.calculatorapi.dto.user.UserDto;
import com.calculator.calculatorapi.dto.user.UserListResponse;
import com.calculator.calculatorapi.models.Role;
import com.calculator.calculatorapi.models.RoleType;
import com.calculator.calculatorapi.models.User;
import com.calculator.calculatorapi.models.UserStatus;
import com.calculator.calculatorapi.repository.UserRepository;
import com.github.javafaker.Faker;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private UserService userService;

    private Faker faker;

    @BeforeEach
    public void setUp() {
        faker = new Faker();
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    public void testAddBalanceToUser() {
        final User user = new User();
        user.setId(UUID.randomUUID());
        user.setBalance(50);
        final UserDetailsInfo userDetailsInfo = buildUserDetailsInfo(user.getId());
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsInfo);
        when(userRepository.findUserById(any())).thenReturn(Optional.of(user));

        final int newBalance = userService.addBalanceToUser();

        assertEquals(150, newBalance);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSwitchUserStatus() {
        final UUID userId = UUID.randomUUID();
        final User user = new User();
        user.setId(userId);
        when(userRepository.findUserById(any())).thenReturn(Optional.of(user));
        userService.switchUserStatus(true, userId);
        assertEquals(UserStatus.INACTIVE, user.getStatus());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testSwitchUserStatusWithUnrecognizedUser() {
        final UUID userId = UUID.randomUUID();

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                userService.switchUserStatus(true, userId));
        String expectedMessage = "User with id " + userId + " not found";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testGetCalculatorUsers() {
        final List<User> users = new ArrayList<>();
        users.add(createUser(UUID.randomUUID(), "user1@example.com", UserStatus.ACTIVE));
        users.add(createUser(UUID.randomUUID(), "user2@example.com", UserStatus.INACTIVE));
        final Page<User> userPage = new PageImpl<>(users);
        final List<UserDto> expectedUsers = mapUsersToUserDtos(users);
        when(userRepository.findAll(PageRequest.of(0, 2))).thenReturn(userPage);

        final UserListResponse response = userService.getCalculatorUsers(request, 1, 2, null);

        assertEquals(1, response.getTotalPages());
        assertNull(response.getNextPageToken());
        assertEquals(1, response.getPage());
        assertEquals(expectedUsers, response.getUsers());
        verify(userRepository).findAll(PageRequest.of(0, 2));
    }

    @Test
    public void testGetCalculatorUsersWithUserStatus() {
        final List<User> users = new ArrayList<>();
        users.add(createUser(UUID.randomUUID(), "user1@example.com", UserStatus.ACTIVE));
        users.add(createUser(UUID.randomUUID(), "user2@example.com", UserStatus.INACTIVE));
        final Page<User> userPage = new PageImpl<>(users);
        final List<UserDto> expectedUsers = mapUsersToUserDtos(users);

        when(userRepository.findAllByStatus(UserStatus.ACTIVE, PageRequest.of(0, 2))).thenReturn(userPage);

        final UserListResponse response = userService.getCalculatorUsers(request, 1, 2, UserStatus.ACTIVE);

        assertEquals(1, response.getTotalPages());
        assertNull(response.getNextPageToken());
        assertEquals(1, response.getPage());
        assertEquals(expectedUsers, response.getUsers());
        verify(userRepository).findAllByStatus(UserStatus.ACTIVE, PageRequest.of(0, 2));
    }

    private User createUser(UUID id, String email, UserStatus status) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setStatus(status);
        return user;
    }

    private List<UserDto> mapUsersToUserDtos(List<User> users) {
        return users.stream().map(user ->
                UserDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .balance(user.getBalance())
                        .status(user.getStatus())
                        .roles(user.getRoles()
                                .stream()
                                .map(r -> r.getType().name())
                                .collect(Collectors.toSet()))
                        .build()).collect(Collectors.toList());

    }

    private UserDetailsInfo buildUserDetailsInfo(UUID userId) {
        return new UserDetailsInfo(
                User.builder()
                        .id(userId)
                        .email(faker.internet().emailAddress())
                        .status(UserStatus.ACTIVE)
                        .password(faker.internet().password())
                        .roles(Set.of(new Role(UUID.randomUUID(), RoleType.USER)))
                        .balance(0)
                        .build()
        );
    }
}
