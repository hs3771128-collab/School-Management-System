package com.school.mamagement.repository;

import com.school.mamagement.entity.Enrollment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentIdAndAcademicYearId(Long studentId, Long yearId);

    Optional<Enrollment> findByStudentIdAndAcademicYearId(Long studentId, Long yearId);

    List<Enrollment> findByAcademicYearId(Long yearId);

    List<Enrollment> findByStudentId(Long studentId);

    long countBySectionIdAndStatus(Long sectionId, com.school.mamagement.enums.EnrollmentStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Enrollment e where e.id = :id")
    Optional<Enrollment> findByIdForUpdate(@Param("id") Long id);
}
