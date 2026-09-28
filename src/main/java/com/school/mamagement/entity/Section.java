package com.school.mamagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "sections",
        uniqueConstraints = @UniqueConstraint(name = "uk_section_class_name", columnNames = {"school_class_id", "name"}),
        indexes = @Index(name = "idx_section_class", columnList = "school_class_id"))
@Check(constraints = "capacity > 0")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Section extends BaseEntity {
    @NotBlank
    @Column(nullable = false, length = 50)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "school_class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_class"))
    private SchoolClass schoolClass;

    @NotNull @Min(1)
    @Column(nullable = false)
    private Integer capacity = 40;

    @Column(nullable = false)
    private boolean active = true;
}
