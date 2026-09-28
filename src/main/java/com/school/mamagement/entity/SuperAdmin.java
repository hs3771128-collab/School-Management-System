package com.school.mamagement.entity;

import com.school.mamagement.enums.AccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "super_admins",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_super_admin_code", columnNames = "super_admin_code"),
                @UniqueConstraint(name = "uk_super_admin_username", columnNames = "username")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SuperAdmin extends BasePerson {

    @Column(name = "super_admin_code", nullable = false, unique = true, length = 50)
    private String superAdminCode;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;
}
