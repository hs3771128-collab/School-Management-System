package com.school.mamagement.dto.request;

import com.school.mamagement.enums.RelationshipType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParentGuardianCreateRequest {

    @NotBlank(message = "Student code is required")
    @Size(max = 50, message = "Student code must not exceed 50 characters")
    private String studentCode;

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(max = 30, message = "Phone must not exceed 30 characters")
    private String phone;

    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @Size(max = 20, message = "CNIC must not exceed 20 characters")
    private String cnic;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    @NotNull(message = "Relationship type is required")
    private RelationshipType relationshipType;
    @jakarta.validation.constraints.NotBlank(message = "Username is required")
    @jakarta.validation.constraints.Size(min = 3, max = 80, message = "Username must be between 3 and 80 characters")
    @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username may contain only letters, numbers, dot, underscore and hyphen")
    private String username;

    @jakarta.validation.constraints.NotBlank(message = "Password is required")
    @jakarta.validation.constraints.Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

}
