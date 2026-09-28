package com.school.mamagement.entity;

import com.school.mamagement.enums.ExamStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.LocalDate;

@Entity
@Table(name = "exams",
        uniqueConstraints = @UniqueConstraint(name = "uk_exam_year_name", columnNames = {"academic_year_id", "name"}),
        indexes = {
                @Index(name = "idx_exam_year", columnList = "academic_year_id"),
                @Index(name = "idx_exam_status", columnList = "status")
        })
@Check(constraints = "end_date >= start_date")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Exam extends BaseEntity {
    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_exam_year"))
    private AcademicYear academicYear;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExamStatus status = ExamStatus.PLANNED;
}
