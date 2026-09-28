package com.school.mamagement.repository;

import com.school.mamagement.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByStudentIdAndAttendanceDate(Long studentId, LocalDate date);

    Optional<Attendance> findByStudentIdAndAttendanceDate(Long studentId, LocalDate date);

    List<Attendance> findByStudentIdAndAttendanceDateBetween(Long studentId, LocalDate from, LocalDate to);
}
