package com.school.mamagement.dto.request;

import com.school.mamagement.enums.Gender;
import com.school.mamagement.enums.TeacherStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TeacherUpdateRequest {


    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    @Email(message = "Invalid email format")
    @Size(max = 150)
    private String email;

    @Size(max = 30)
    private String phone;

    @Size(max = 500)
    private String address;

    @Size(max = 200)
    private String qualification;

    private LocalDate joiningDate;

    private TeacherStatus status;

}
