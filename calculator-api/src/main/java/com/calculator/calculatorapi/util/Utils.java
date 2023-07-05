package com.calculator.calculatorapi.util;

import com.calculator.calculatorapi.config.UserDetailsInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class Utils {
    public static String convertCurrentUriToPageUri(final HttpServletRequest request,
                                                        final int pageNumber,
                                                        final int pageSize,
                                                        final String additionalParams,
                                                        final int totalPages,
                                                        final boolean isNextPage) {
        String nextPageUri = null;
        if(pageNumber + 1 <= totalPages) {
            nextPageUri = request.getRequestURI() +
                    "?pageNumber=" + (isNextPage ? pageNumber + 1 : pageNumber - 1) +
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

