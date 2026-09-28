package com.school.mamagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(
        name = "teacher_assignments",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_teacher_assignment",
                        columnNames = {
                                "teacher_id",
                                "academic_year_id",
                                "section_id",
                                "subject_id"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_teacher_assignment_teacher_year",
                        columnList = "teacher_id,academic_year_id"
                ),
                @Index(
                        name = "idx_teacher_assignment_section_year",
                        columnList = "section_id,academic_year_id"
                ),
                @Index(
                        name = "idx_teacher_assignment_subject_year",
                        columnList = "subject_id,academic_year_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherAssignment extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "teacher_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_teacher_assignment_teacher"
            )
    )
    private Teacher teacher;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "academic_year_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_teacher_assignment_year"
            )
    )
    private AcademicYear academicYear;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "section_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_teacher_assignment_section"
            )
    )
    private Section section;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "subject_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_teacher_assignment_subject"
            )
    )
    private Subject subject;
}