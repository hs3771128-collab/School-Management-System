package com.school.mamagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Check;

import java.math.BigDecimal;

@Entity
@Table(name = "fee_structures",
        uniqueConstraints = @UniqueConstraint(name = "uk_fee_structure", columnNames = {"academic_year_id", "school_class_id", "name"}),
        indexes = {
                @Index(name = "idx_fee_structure_year", columnList = "academic_year_id"),
                @Index(name = "idx_fee_structure_class", columnList = "school_class_id")
        })
@Check(constraints = "amount > 0")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class FeeStructure extends BaseEntity {
    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "academic_year_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fee_structure_year"))
    private AcademicYear academicYear;

    @NotNull @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "school_class_id", nullable = false, foreignKey = @ForeignKey(name = "fk_fee_structure_class"))
    private SchoolClass schoolClass;

    @NotBlank @Column(nullable = false, length = 100)
    private String name;

    @NotNull @DecimalMin(value = "0.01")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private boolean active = true;
}
