package com.school.mamagement.repository;

import com.school.mamagement.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository
        extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsernameIgnoreCase(
            String username
    );

    boolean existsByUsernameIgnoreCase(
            String username
    );

    Optional<UserAccount> findByAdminId(Long adminId);

    boolean existsByAdminId(Long adminId);

    Optional<UserAccount> findBySuperAdminId(Long superAdminId);

    boolean existsBySuperAdminId(Long superAdminId);

    Optional<UserAccount> findByTeacherId(Long teacherId);

    boolean existsByTeacherId(Long teacherId);

    Optional<UserAccount> findByStudentId(Long studentId);

    boolean existsByStudentId(Long studentId);

    boolean existsByParentGuardianId(
            Long parentGuardianId
    );

    long count();
}