package com.school.mamagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "subjects",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_subject_code", columnNames = "code"),
                @UniqueConstraint(name = "uk_subject_name", columnNames = "name")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subject extends BaseEntity {
    @NotBlank @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String code;

    @NotBlank @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}
