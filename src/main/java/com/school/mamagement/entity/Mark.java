package com.school.mamagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

@Entity
@Table(name = "marks",
        uniqueConstraints = @UniqueConstraint(name = "uk_mark_exam_subject_student", columnNames = {"exam_subject_id", "student_id"}),
        indexes = {
                @Index(name = "idx_mark_exam_subject", columnList = "exam_subject_id"),
                @Index(name = "idx_mark_student", columnList = "student_id")
        })
@Check(constraints = "marks_obtained >= 0")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Mark extends BaseEntity {
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_subject_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_mark_exam_subject"))
    private ExamSubject examSubject;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_mark_student"))
    private Student student;

    @NotNull @DecimalMin(value = "0.00")
    @Column(name = "marks_obtained", nullable = false, precision = 8, scale = 2)
    private BigDecimal marksObtained;
}
