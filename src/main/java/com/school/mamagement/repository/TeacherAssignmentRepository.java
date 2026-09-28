package com.school.mamagement.repository;

import com.school.mamagement.entity.TeacherAssignment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherAssignmentRepository
        extends JpaRepository<TeacherAssignment, Long> {

    boolean existsByTeacherIdAndAcademicYearIdAndSectionIdAndSubjectId(
            Long teacherId,
            Long academicYearId,
            Long sectionId,
            Long subjectId
    );

    boolean existsByTeacherIdAndAcademicYearIdAndSectionIdAndSubjectIdAndIdNot(
            Long teacherId,
            Long academicYearId,
            Long sectionId,
            Long subjectId,
            Long id
    );

    List<TeacherAssignment> findByTeacherIdAndAcademicYearId(
            Long teacherId,
            Long academicYearId
    );

    List<TeacherAssignment> findBySectionIdAndAcademicYearId(
            Long sectionId,
            Long academicYearId
    );

    List<TeacherAssignment> findBySubjectIdAndAcademicYearId(
            Long subjectId,
            Long academicYearId
    );

    List<TeacherAssignment> findByAcademicYearId(
            Long academicYearId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select a
            from TeacherAssignment a
            where a.id = :id
            """)
    Optional<TeacherAssignment> findByIdForUpdate(
            @Param("id") Long id
    );
}