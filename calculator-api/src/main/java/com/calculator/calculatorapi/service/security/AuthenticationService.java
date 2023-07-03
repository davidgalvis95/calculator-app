package com.calculator.calculatorapi.service.security;

import com.calculator.calculatorapi.config.UserDetailsInfo;
import com.calculator.calculatorapi.dto.authentication.JwtResponse;
import com.calculator.calculatorapi.dto.authentication.LoginRequest;
import com.calculator.calculatorapi.dto.authentication.SignupRequest;
import com.calculator.calculatorapi.dto.user.UserDto;
import com.calculator.calculatorapi.models.Role;
import com.calculator.calculatorapi.models.RoleType;
import com.calculator.calculatorapi.models.User;
import com.calculator.calculatorapi.repository.RoleRepository;
import com.calculator.calculatorapi.repository.UserRepository;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log
public class AuthenticationService {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    private static final int INITIAL_USER_BALANCE = 100;

    public JwtResponse authenticateUser(final LoginRequest loginRequest) {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt;
        if (authentication.isAuthenticated()) {
            jwt = jwtService.generateToken(loginRequest.getEmail());
        } else {
            throw new UsernameNotFoundException("invalid user request");
        }
        final UserDetailsInfo userDetails = (UserDetailsInfo) authentication.getPrincipal();
        final Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return JwtResponse.builder()
                .accessToken(jwt)
                .tokenType("Bearer")
                .roles(roles)
                .email(userDetails.getUsername())
                .id(userDetails.getId())
                .build();
    }

    public UserDto registerUser(final SignupRequest signUpRequest) {
        if (userRepository.existsUserByEmail(signUpRequest.getEmail())) {
            throw new BadCredentialsException("User already exists");
        }
        final Set<RoleType> userRoles = signUpRequest.getRoles();
        if (userRoles.isEmpty()) {
            throw new BadCredentialsException("Roles are not present, please add at least one");
        }
        final Set<Role> roles = getAllRolesMatchingGivenTypes(userRoles);
        final User user = userRepository.save(User.builder()
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .status(signUpRequest.getStatus())
                .roles(roles)
                .balance(INITIAL_USER_BALANCE)
                .build());

        return UserDto.builder()
                .id(user.getId())
                .balance(user.getBalance())
                .email(user.getEmail())
                .status(user.getStatus())
                .roles(user.getRoles()
                        .stream()
                        .map(role -> role.getType().name())
                        .collect(Collectors.toSet()))
                .build();
    }

    private Set<Role> getAllRolesMatchingGivenTypes(Set<RoleType> roleTypes) {
        return roleTypes.stream()
                .map(type -> roleRepository.findRoleByType(type)
                        .orElseThrow(() -> new BadCredentialsException("User role " + type + " does not exist!")))
                .collect(Collectors.toSet());
    }
}
