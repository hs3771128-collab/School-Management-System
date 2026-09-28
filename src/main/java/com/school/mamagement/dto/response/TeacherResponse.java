package com.school.mamagement.dto.response;

import com.school.mamagement.enums.Gender;
import com.school.mamagement.enums.TeacherStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class TeacherResponse {

    private Long id;

    private String employeeCode;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String email;

    private String phone;

    private String address;

    private String qualification;

    private LocalDate joiningDate;

    private TeacherStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
