package com.school.mamagement.repository;

import com.school.mamagement.entity.Section;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    boolean existsBySchoolClassIdAndNameIgnoreCase(Long schoolClassId, String name);

    List<Section> findBySchoolClassId(Long schoolClassId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Section s where s.id = :id")
    Optional<Section> findByIdForUpdate(@Param("id") Long id);

}
