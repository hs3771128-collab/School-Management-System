package com.school.mamagement.repository;

import com.school.mamagement.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {
    boolean existsByAcademicYearIdAndSchoolClassIdAndNameIgnoreCase(Long y, Long c, String n);

    List<FeeStructure> findByAcademicYearId(Long y);
}
