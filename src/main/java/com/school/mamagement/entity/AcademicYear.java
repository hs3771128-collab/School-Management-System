package com.school.mamagement.entity;

import com.school.mamagement.enums.AcademicYearStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.LocalDate;

@Entity
@Table(name = "academic_years",
        uniqueConstraints = @UniqueConstraint(name = "uk_academic_year_name", columnNames = "name"),
        indexes = {
                @Index(name = "idx_academic_year_status", columnList = "status"),
                @Index(name = "idx_academic_year_dates", columnList = "start_date,end_date")
        })
@Check(constraints = "end_date >= start_date")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AcademicYear extends BaseEntity {
 @NotBlank @Size(max = 50)
 @Column(nullable = false, length = 50)
 private String name;

 @NotNull
 @Column(name = "start_date", nullable = false)
 private LocalDate startDate;

 @NotNull
 @Column(name = "end_date", nullable = false)
 private LocalDate endDate;

 @Enumerated(EnumType.STRING)
 @Column(nullable = false, length = 20)
 private AcademicYearStatus status = AcademicYearStatus.PLANNED;
}
