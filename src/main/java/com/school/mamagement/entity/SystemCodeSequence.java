package com.school.mamagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "system_code_sequences",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_system_code_sequence_prefix",
                        columnNames = "prefix"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class SystemCodeSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String prefix;

    @Column(name = "next_value", nullable = false)
    private Long nextValue;

    public SystemCodeSequence(String prefix, Long nextValue) {
        this.prefix = prefix;
        this.nextValue = nextValue;
    }
}