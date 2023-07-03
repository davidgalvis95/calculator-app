package com.calculator.calculatorapi.service.security;

import com.calculator.calculatorapi.config.UserDetailsInfo;
import com.calculator.calculatorapi.dto.authentication.JwtResponse;
import com.calculator.calculatorapi.dto.authentication.LoginRequest;
import com.calculator.calculatorapi.dto.authentication.SignupRequest;
import com.calculator.calculatorapi.dto.user.UserDto;
import com.calculator.calculatorapi.models.Role;
import com.calculator.calculatorapi.models.RoleType;
import com.calculator.calculatorapi.models.User;
import com.calculator.calculatorapi.models.UserStatus;
import com.calculator.calculatorapi.repository.RoleRepository;
import com.calculator.calculatorapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(
                jwtService,
                userRepository,
                roleRepository,
                passwordEncoder,
                authenticationManager
        );
    }

    @Test
    public void testAuthenticateUser() {
        String email = "user@example.com";
        String password = "password";
        LoginRequest loginRequest = new LoginRequest(email, password);
        UserDetailsInfo userDetailsInfo = createUserDetailsInfo(email, password);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetailsInfo);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        when(jwtService.generateToken(email)).thenReturn("jwt-token");

        JwtResponse response = authenticationService.authenticateUser(loginRequest);

        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(new HashSet<>(Arrays.asList("ROLE_USER")), response.getRoles());
        assertEquals(email, response.getEmail());
        assertEquals(userDetailsInfo.getId(), response.getId());
    }

    @Test
    public void testAuthenticateUserWithInvalidCredentials() {
        String email = "user@example.com";
        String password = "password";
        LoginRequest loginRequest = new LoginRequest(email, password);

        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                authenticationService.authenticateUser(loginRequest));

        String expectedMessage = "invalid user request";
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testRegisterUser() {
        String email = "user@example.com";
        String password = "password";
        SignupRequest signupRequest = new SignupRequest(
                email,
                password,
                UserStatus.ACTIVE,
                new HashSet<>(List.of(RoleType.USER))
        );
        Role role = Role.builder().type(RoleType.USER).build();
        when(roleRepository.findRoleByType(RoleType.USER)).thenReturn(Optional.of(role));

        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setBalance(100);
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(new HashSet<>(List.of(role)));
        when(userRepository.save(any())).thenReturn(user);

        UserDto response = authenticationService.registerUser(signupRequest);

        assertEquals(user.getId(), response.getId());
        assertEquals(100, response.getBalance());
        assertEquals(email, response.getEmail());
        assertEquals(UserStatus.ACTIVE, response.getStatus());
        assertEquals(new HashSet<>(List.of("USER")), response.getRoles());
    }

    @Test
    public void testRegisterUserWithExistingEmail() {
        String email = "user@example.com";
        String password = "password";
        SignupRequest signupRequest = new SignupRequest(
                email,
                password,
                UserStatus.ACTIVE,
                new HashSet<>(List.of(RoleType.USER))
        );
        when(userRepository.existsUserByEmail(email)).thenReturn(true);
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                authenticationService.registerUser(signupRequest));
        String expectedMessage = "User already exists";
        assertEquals(expectedMessage, exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testRegisterUserWithNoRoles() {
        String email = "user@example.com";
        String password = "password";
        SignupRequest signupRequest = new SignupRequest(
                email,
                password,
                UserStatus.ACTIVE,
                new HashSet<>()
        );
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () ->
                authenticationService.registerUser(signupRequest));

        String expectedMessage = "Roles are not present, please add at least one";
        assertEquals(expectedMessage, exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    private UserDetailsInfo createUserDetailsInfo(String email, String password) {
        return new UserDetailsInfo(User.builder()
                .email(email)
                .password(password)
                .status(UserStatus.ACTIVE)
                .roles(Set.of(Role.builder().type(RoleType.USER).build()))
                .balance(100)
                .build());
    }
}
