package com.school.mamagement.entity;

import com.school.mamagement.enums.EnrollmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.LocalDate;

@Entity
@Table(name = "enrollments",
        uniqueConstraints = @UniqueConstraint(name = "uk_enrollment_student_year", columnNames = {"student_id", "academic_year_id"}),
        indexes = {
                @Index(name = "idx_enrollment_student", columnList = "student_id"),
                @Index(name = "idx_enrollment_year", columnList = "academic_year_id"),
                @Index(name = "idx_enrollment_section_status", columnList = "section_id,status")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Enrollment extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_student"))
    private Student student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_year"))
    private AcademicYear academicYear;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "school_class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_class"))
    private SchoolClass schoolClass;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false, foreignKey = @ForeignKey(name = "fk_enrollment_section"))
    private Section section;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @NotNull
    @Column(name = "enrolled_at", nullable = false)
    private LocalDate enrolledAt = LocalDate.now();
}
