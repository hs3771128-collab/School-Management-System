package com.school.mamagement.entity;

import com.school.mamagement.enums.TeacherStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "teachers",
        indexes = @Index(name = "idx_teacher_status", columnList = "status"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends BasePerson {
    @NotBlank @Size(max = 50)
    @Column(name = "employee_code", nullable = false, unique = true, length = 50)
    private String employeeCode;

    @Size(max = 200)
    @Column(length = 200)
    private String qualification;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TeacherStatus status = TeacherStatus.ACTIVE;
}
