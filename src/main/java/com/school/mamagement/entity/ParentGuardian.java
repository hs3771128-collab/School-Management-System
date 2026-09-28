package com.school.mamagement.entity;

import com.school.mamagement.enums.RelationshipType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "parent_guardians",
        indexes = @Index(name = "idx_parent_guardian_student", columnList = "student_id"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParentGuardian extends BasePerson {
    @Column(name = "cnic", length = 13)
    private String cnic;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false, length = 20)
    private RelationshipType relationshipType;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_parent_guardian_student"))
    private Student student;
}
