package com.school.mamagement.repository;

import com.school.mamagement.entity.AcademicYear;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AcademicYearRepository extends JpaRepository<AcademicYear, Long> {
    boolean existsByNameIgnoreCase(String name);

    Optional<AcademicYear> findByNameIgnoreCase(String name);

    long countByStatus(com.school.mamagement.enums.AcademicYearStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select y from AcademicYear y where y.id = :id")
    Optional<AcademicYear> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select y from AcademicYear y where y.status = com.school.mamagement.enums.AcademicYearStatus.ACTIVE")
    java.util.List<AcademicYear> findActiveForUpdate();
}
