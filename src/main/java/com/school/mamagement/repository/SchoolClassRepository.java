package com.school.mamagement.repository;

import com.school.mamagement.entity.SchoolClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {
    boolean existsByNameIgnoreCase(String name);

    Optional<SchoolClass> findByNameIgnoreCase(String name);
}
