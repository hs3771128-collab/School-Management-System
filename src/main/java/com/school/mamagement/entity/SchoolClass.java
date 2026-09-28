package com.school.mamagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "school_classes",
        uniqueConstraints = @UniqueConstraint(name = "uk_school_class_name", columnNames = "name"))
@Check(constraints = "grade_level > 0")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SchoolClass extends BaseEntity {
    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Min(1)
    @Column(name = "grade_level", nullable = false)
    private Integer gradeLevel;

    @Column(nullable = false)
    private boolean active = true;
}
