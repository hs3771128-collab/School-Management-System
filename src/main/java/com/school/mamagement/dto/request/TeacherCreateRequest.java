package com.school.mamagement.dto.request;

import com.school.mamagement.enums.Gender;
import com.school.mamagement.enums.TeacherStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TeacherCreateRequest {


    @NotBlank(message = "First name is required")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100)
    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    @Email(message = "Invalid email format")
    @Size(max = 150)
    private String email;

    @Size(max = 11)
    private String phone;

    @Size(max = 500)
    private String address;

    @Size(max = 200)
    private String qualification;

    private LocalDate joiningDate;

    private TeacherStatus status;


    @jakarta.validation.constraints.NotBlank(message = "Username is required")
    @jakarta.validation.constraints.Size(min = 3, max = 80, message = "Username must be between 3 and 80 characters")
    @jakarta.validation.constraints.Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username may contain only letters, numbers, dot, underscore and hyphen")
    private String username;

    @jakarta.validation.constraints.NotBlank(message = "Password is required")
    @jakarta.validation.constraints.Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
    private String password;

}
