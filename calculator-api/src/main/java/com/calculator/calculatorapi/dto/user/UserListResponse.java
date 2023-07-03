package com.calculator.calculatorapi.dto.user;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class UserListResponse {
    int page;
    int totalPages;
    String nextPageToken;
    List<UserDto> users;
}
