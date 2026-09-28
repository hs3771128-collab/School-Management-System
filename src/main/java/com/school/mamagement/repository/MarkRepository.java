package com.school.mamagement.repository;

import com.school.mamagement.entity.Mark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByExamSubjectIdAndStudentId(Long examSubjectId, Long studentId);

    boolean existsByExamSubjectIdAndStudentId(Long examSubjectId, Long studentId);
}
