package com.school.mamagement.repository;

import com.school.mamagement.entity.SuperAdmin;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SuperAdminRepository
        extends JpaRepository<SuperAdmin, Long> {

    Optional<SuperAdmin> findBySuperAdminCode(
            String superAdminCode
    );

    boolean existsBySuperAdminCode(
            String superAdminCode
    );

    Optional<SuperAdmin> findByUsernameIgnoreCase(
            String username
    );

    boolean existsByUsernameIgnoreCase(
            String username
    );

    boolean existsByEmail(
            String email
    );


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select superAdmin from SuperAdmin superAdmin where superAdmin.id = :id")
    Optional<SuperAdmin> findByIdForUpdate(@Param("id") Long id);
}