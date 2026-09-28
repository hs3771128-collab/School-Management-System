package com.school.mamagement.dto.response;

import com.school.mamagement.enums.AccountStatus;
import com.school.mamagement.enums.Gender;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class SuperAdminResponse {

    private Long id;
    private String superAdminCode;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;
    private String phone;
    private String address;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
