package com.school.mamagement.dto.request;

import com.school.mamagement.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CreateAuthAccountRequest {
    @NotBlank @Size(min = 3, max = 80)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username may contain only letters, numbers, dot, underscore and hyphen")
    private String username;

    @NotBlank @Size(min = 8, max = 128)
    private String password;

    @NotNull
    private UserRole role;

    @Positive private Long adminId;
    @Positive private Long superAdminId;
    @Positive private Long teacherId;
    @Positive private Long studentId;
    @Positive private Long parentGuardianId;
}
