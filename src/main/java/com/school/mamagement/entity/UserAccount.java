package com.school.mamagement.entity;

import com.school.mamagement.enums.AccountStatus;
import com.school.mamagement.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_accounts", uniqueConstraints = @UniqueConstraint(name = "uk_user_account_username", columnNames = "username"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserAccount extends BaseEntity {
    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(name = "admin_id", unique = true)
    private Long adminId;
    @Column(name = "super_admin_id", unique = true)
    private Long superAdminId;
    @Column(name = "teacher_id", unique = true)
    private Long teacherId;
    @Column(name = "student_id", unique = true)
    private Long studentId;
    @Column(name = "parent_guardian_id", unique = true)
    private Long parentGuardianId;
}
