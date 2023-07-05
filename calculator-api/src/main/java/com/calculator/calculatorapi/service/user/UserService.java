package com.calculator.calculatorapi.service.user;

import com.calculator.calculatorapi.dto.exception.ObjectNotFoundException;
import com.calculator.calculatorapi.dto.user.UserDto;
import com.calculator.calculatorapi.dto.user.UserListResponse;
import com.calculator.calculatorapi.models.Role;
import com.calculator.calculatorapi.models.RoleType;
import com.calculator.calculatorapi.models.User;
import com.calculator.calculatorapi.models.UserStatus;
import com.calculator.calculatorapi.repository.UserRepository;
import com.calculator.calculatorapi.util.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public static final int USER_BALANCE = 100;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int addBalanceToUser() {
        final UUID userId = Utils.getLoggedUserId();
        if (userId != null) {
            final User user = userRepository.findUserById(userId)
                    .orElseThrow(() -> new ObjectNotFoundException("User with id " + userId + " not found"));
            final int newBalance = USER_BALANCE + user.getBalance();
            user.setBalance(newBalance);
            userRepository.save(user);
            return newBalance;
        } else {
            throw new BadCredentialsException("Unrecognized connected user, aborting transaction");
        }
    }

    public void switchUserStatus(final boolean isDeactivationRequest, final UUID userId) {
        final User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id " + userId + " not found"));
        user.setStatus(isDeactivationRequest ? UserStatus.INACTIVE : UserStatus.ACTIVE);
        userRepository.save(user);
    }

    public void grandAdminAccess(final UUID userId) {
        final User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id " + userId + " not found"));
        final Set<Role> roles = user.getRoles();
        if(roles.stream().anyMatch(role -> role.getType().equals(RoleType.ADMIN))) {
            throw new IllegalArgumentException("Cannot change user to ADMIN because it's already an ADMIN");
        }else{
            roles.add(Role.builder().type(RoleType.ADMIN).build());
        }
        userRepository.save(user);
    }

    public UserListResponse getCalculatorUsers(final HttpServletRequest request,
                                               final int pageNumber,
                                               final int pageSize,
                                               final UserStatus userStatus) {
        Page<User> usersPage;
        String userStatusString = null;
        if (userStatus != null) {
            usersPage = userRepository.findAllByStatus(userStatus, PageRequest.of(pageNumber - 1, pageSize));
            userStatusString = "userStatus=" + userStatus;
        } else {
            usersPage = userRepository.findAll(PageRequest.of(pageNumber - 1, pageSize));
        }
        final int totalPages = usersPage.getTotalPages();
        final long totalElements = usersPage.getTotalElements();
        final List<User> users = usersPage.getContent();

        String nextPageUri = null;
        if (pageNumber < totalPages) {
            nextPageUri = Utils.convertCurrentUriToPageUri(
                    request,
                    pageNumber,
                    pageSize,
                    userStatusString,
                    totalPages,
                    true);
        }

        String prevPageUri = null;
        if (pageNumber > 1) {
            prevPageUri = Utils.convertCurrentUriToPageUri(
                    request,
                    pageNumber,
                    pageSize,
                    userStatusString,
                    totalPages,
                    true);
        }

        return UserListResponse.builder()
                .totalPages(totalPages)
                .totalUsers(totalElements)
                .nextPageToken(nextPageUri)
                .prevPageToken(prevPageUri)
                .page(pageNumber)
                .users(mapUsersToUserDtos(users))
                .build();
    }

    private List<UserDto> mapUsersToUserDtos(final List<User> users) {
        return users.stream().map(user ->
                UserDto.builder()
                        .id(user.getId())
                        .balance(user.getBalance())
                        .email(user.getEmail())
                        .status(user.getStatus())
                        .roles(user.getRoles()
                                .stream()
                                .map(role -> role.getType().name())
                                .collect(Collectors.toSet()))
                        .build()
        ).collect(Collectors.toList());
    }
}
