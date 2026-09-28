package com.school.mamagement.repository;

import com.school.mamagement.entity.Admin;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByAdminCode(String adminCode);

    boolean existsByAdminCode(String adminCode);

    Optional<Admin> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select admin from Admin admin where admin.id = :id")
    Optional<Admin> findByIdForUpdate(@Param("id") Long id);
}
