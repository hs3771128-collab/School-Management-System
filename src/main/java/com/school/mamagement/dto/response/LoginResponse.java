package com.school.mamagement.dto.response;

import com.school.mamagement.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private long expiresInSeconds;
    private String username;
    private UserRole role;
    private LocalDateTime expiresAt;
}
