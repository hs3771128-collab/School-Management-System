package com.school.mamagement.repository;

import com.school.mamagement.entity.ExamSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamSubjectRepository extends JpaRepository<ExamSubject, Long> {
    boolean existsByExamIdAndSubjectId(Long examId, Long subjectId);

    Optional<ExamSubject> findByExamIdAndSubjectId(Long examId, Long subjectId);

    List<ExamSubject> findByExamId(Long examId);
}
