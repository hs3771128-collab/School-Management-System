package com.school.mamagement.entity;

import com.school.mamagement.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@MappedSuperclass
@Getter @Setter
public abstract class BasePerson extends BaseEntity {
    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Email @Size(max = 150)
    @Column(unique = true, length = 150)
    private String email;

    @Size(max = 30)
    @Column(length = 30)
    private String phone;

    @Size(max = 500)
    @Column(length = 500)
    private String address;
}
