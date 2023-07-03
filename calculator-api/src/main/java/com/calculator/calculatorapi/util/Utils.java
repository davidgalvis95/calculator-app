package com.calculator.calculatorapi.util;

import com.calculator.calculatorapi.config.UserDetailsInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class Utils {
    public static String convertCurrentUriToNextPageUri(final HttpServletRequest request,
                                                        final int pageNumber,
                                                        final int pageSize,
                                                        final String additionalParams,
                                                        final int totalPages) {
        String nextPageUri = null;
        if(pageNumber + 1 <= totalPages) {
            nextPageUri = request.getScheme() +
                    request.getServerName() +
                    request.getRequestURI() +
                    "?pageNumber=" + (pageNumber + 1) +
                    "&pageSize=" + pageSize +
                    additionalParams;
        }
        return nextPageUri;
    }

    public static UUID getLoggedUserId() {
        final Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsInfo) {
            return ((UserDetailsInfo) principal).getId();
        }
        return null;
    }
}

