package com.school.mamagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

@Entity
@Table(name = "exam_subjects",
        uniqueConstraints = @UniqueConstraint(name = "uk_exam_subject", columnNames = {"exam_id", "subject_id"}),
        indexes = {
                @Index(name = "idx_exam_subject_exam", columnList = "exam_id"),
                @Index(name = "idx_exam_subject_subject", columnList = "subject_id")
        })
@Check(constraints = "max_marks > 0 AND passing_marks >= 0 AND passing_marks <= max_marks")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ExamSubject extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_exam_subject_exam"))
    private Exam exam;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_exam_subject_subject"))
    private Subject subject;

    @NotNull @DecimalMin(value = "0.01")
    @Column(name = "max_marks", nullable = false, precision = 8, scale = 2)
    private BigDecimal maxMarks;

    @NotNull @DecimalMin(value = "0.00")
    @Column(name = "passing_marks", nullable = false, precision = 8, scale = 2)
    private BigDecimal passingMarks;
}
