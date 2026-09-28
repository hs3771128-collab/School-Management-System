package com.school.mamagement.repository;

import com.school.mamagement.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    boolean existsByAcademicYearIdAndNameIgnoreCase(Long yearId, String name);

    List<Exam> findByAcademicYearId(Long yearId);
}
