package com.school.mamagement.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @Schema(description = "Username used for authentication", example = "superadmin")
    @NotBlank(message = "Username is required")
    @Size(max = 80, message = "Username must not exceed 80 characters")
    private String username;

    @Schema(description = "Password used for authentication", example = "ExamplePassword123!")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;
}