package com.school.mamagement.entity;

import com.school.mamagement.enums.StudentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "students",
        uniqueConstraints = @UniqueConstraint(name = "uk_student_code", columnNames = "student_code"),
        indexes = @Index(name = "idx_student_status", columnList = "status"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student extends BasePerson {
    @NotBlank @Size(max = 50)
    @Column(name = "student_code", nullable = false, unique = true, length = 50)
    private String studentCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StudentStatus status;
}
