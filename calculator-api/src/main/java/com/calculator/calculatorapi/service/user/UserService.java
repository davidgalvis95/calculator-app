package com.calculator.calculatorapi.service.user;

import com.calculator.calculatorapi.dto.exception.ObjectNotFoundException;
import com.calculator.calculatorapi.dto.user.UserDto;
import com.calculator.calculatorapi.dto.user.UserListResponse;
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
        final List<User> users = usersPage.getContent();

        final String nextPageUri = Utils.convertCurrentUriToNextPageUri(
                request,
                pageNumber,
                pageSize,
                userStatusString,
                totalPages);

        return UserListResponse.builder()
                .totalPages(totalPages)
                .nextPageToken(nextPageUri)
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
