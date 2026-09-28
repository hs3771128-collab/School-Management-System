package com.school.mamagement.repository;

import com.school.mamagement.entity.SystemCodeSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface SystemCodeSequenceRepository
        extends JpaRepository<SystemCodeSequence, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select s
            from SystemCodeSequence s
            where s.prefix = :prefix
            """)
    Optional<SystemCodeSequence> findByPrefixForUpdate(
            @Param("prefix") String prefix
    );
}