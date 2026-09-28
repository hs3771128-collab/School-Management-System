package com.school.mamagement.dto.response;

import com.school.mamagement.enums.Gender;
import com.school.mamagement.enums.StudentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class StudentResponse {


    private Long id;

    private String studentCode;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String email;

    private String phone;

    private String address;

    private StudentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
