package com.school.mamagement.dto.response;

import com.school.mamagement.enums.AccountStatus;
import com.school.mamagement.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class CurrentUserResponse {
    private Long id;
    private String username;
    private UserRole role;
    private AccountStatus status;
}
