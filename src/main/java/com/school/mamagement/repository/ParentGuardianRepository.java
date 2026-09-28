package com.school.mamagement.repository;

import com.school.mamagement.entity.ParentGuardian;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParentGuardianRepository extends JpaRepository<ParentGuardian, Long> {
    List<ParentGuardian> findByStudentId(Long studentId);
    boolean existsByStudentId(Long studentId);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select parentGuardian from ParentGuardian parentGuardian where parentGuardian.id = :id")
    Optional<ParentGuardian> findByIdForUpdate(@Param("id") Long id);
}
