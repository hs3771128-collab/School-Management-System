package com.school.mamagement.repository;

import com.school.mamagement.entity.Teacher;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface  TeacherRepository extends JpaRepository<Teacher,Long> {
    Optional<Teacher> findByEmployeeCode(String employeeCode);
    boolean existsByEmployeeCode(String employeeCode);
    boolean existsByEmail(String email);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select teacher from Teacher teacher where teacher.id = :id")
    Optional<Teacher> findByIdForUpdate(@Param("id") Long id);
}
