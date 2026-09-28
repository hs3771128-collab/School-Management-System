package com.school.mamagement.dto.request;

import com.school.mamagement.enums.AccountStatus;
import com.school.mamagement.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "Request used to initialize the system with the first Super Admin")
public class BootstrapRequest {

    // ============================================================
    // AUTHENTICATION CREDENTIALS
    // ============================================================

    @Schema(
            description = "Username for the first Super Admin login",
            example = "superadmin"
    )
    @NotBlank(message = "Username is required")
    @Size(
            min = 3,
            max = 80,
            message = "Username must be between 3 and 80 characters"
    )
    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Username may contain only letters, numbers, dots, underscores and hyphens"
    )
    private String username;


    @Schema(
            description = "Password for the first Super Admin login",
            example = "StrongPassword123!"
    )
    @NotBlank(message = "Password is required")
    @Size(
            min = 12,
            max = 128,
            message = "Password must be between 12 and 128 characters"
    )
    private String password;


    // ============================================================
    // SUPER ADMIN PROFILE
    // ============================================================

    @Schema(
            description = "First name of the first Super Admin",
            example = "System"
    )
    @NotBlank(message = "First name is required")
    @Size(
            max = 100,
            message = "First name must not exceed 100 characters"
    )
    private String firstName;


    @Schema(
            description = "Last name of the first Super Admin",
            example = "Administrator"
    )
    @NotBlank(message = "Last name is required")
    @Size(
            max = 100,
            message = "Last name must not exceed 100 characters"
    )
    private String lastName;


    @Schema(
            description = "Date of birth of the first Super Admin",
            example = "1990-01-01"
    )
    private LocalDate dateOfBirth;


    @Schema(
            description = "Gender of the first Super Admin",
            example = "MALE"
    )
    private Gender gender;


    @Schema(
            description = "Email address of the first Super Admin",
            example = "superadmin@school.com"
    )
    @Email(message = "Email must be valid")
    @Size(
            max = 150,
            message = "Email must not exceed 150 characters"
    )
    private String email;


    @Schema(
            description = "Phone number of the first Super Admin",
            example = "03001234567"
    )
    @Size(
            max = 30,
            message = "Phone must not exceed 30 characters"
    )
    private String phone;


    @Schema(
            description = "Address of the first Super Admin",
            example = "School Management System"
    )
    @Size(
            max = 500,
            message = "Address must not exceed 500 characters"
    )
    private String address;


    @Schema(
            description = "Initial status of the Super Admin",
            example = "ACTIVE"
    )
    @NotNull(message = "Status is required")
    private AccountStatus status;
}