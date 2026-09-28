package com.school.mamagement.repository;

import com.school.mamagement.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByExamIdAndStudentId(Long examId, Long studentId);
}
