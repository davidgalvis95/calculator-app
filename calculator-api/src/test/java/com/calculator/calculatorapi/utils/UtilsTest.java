package com.calculator.calculatorapi.utils;

import com.calculator.calculatorapi.config.UserDetailsInfo;
import com.calculator.calculatorapi.models.Role;
import com.calculator.calculatorapi.models.RoleType;
import com.calculator.calculatorapi.models.User;
import com.calculator.calculatorapi.models.UserStatus;
import com.calculator.calculatorapi.util.Utils;
import com.github.javafaker.Faker;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UtilsTest {

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private HttpServletRequest request;

    @Test
    public void testConvertCurrentUriToNextPageUri() {
        // Given
        final int pageNumber = 1;
        final int pageSize = 10;
        final String additionalParams = "&operationType=ADDITION";
        final int totalPages = 5;
        final String expectedNextPageUri = "https://randomHost/api/v1/randomUri?pageNumber=2&pageSize=10"
                + additionalParams;
        when(request.getScheme()).thenReturn("https://");
        when(request.getServerName()).thenReturn("randomHost");
        when(request.getRequestURI()).thenReturn("/api/v1/randomUri");
        // When
        final String nextPageUri = Utils.convertCurrentUriToNextPageUri(request,
                pageNumber,
                pageSize,
                additionalParams,
                totalPages);
        // Then
        assertEquals(expectedNextPageUri, nextPageUri);
        verify(request, times(1)).getScheme();
        verify(request, times(1)).getServerName();
        verify(request, times(1)).getRequestURI();
    }

    @Test
    public void testGetLoggedUserId() {
        // Given
        final Faker faker = new Faker();
        final UserDetailsInfo userDetailsInfo = new UserDetailsInfo(
                User.builder()
                        .id(UUID.randomUUID())
                        .email(faker.internet().emailAddress())
                        .status(UserStatus.ACTIVE)
                        .password(faker.internet().password())
                        .roles(Set.of(new Role(UUID.randomUUID(), RoleType.USER)))
                        .balance(100)
                .build());
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetailsInfo);
        // When
        final UUID loggedUserId = Utils.getLoggedUserId();
        // Then
        assertEquals(userDetailsInfo.getId(), loggedUserId);
        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getPrincipal();
    }
}
