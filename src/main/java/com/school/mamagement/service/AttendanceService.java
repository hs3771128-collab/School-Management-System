package com.school.mamagement.service;

import com.school.mamagement.dto.request.attendance.AttendanceCreateRequest;
import com.school.mamagement.dto.response.attendance.AttendanceResponse;
import com.school.mamagement.entity.Attendance;
import com.school.mamagement.entity.Student;
import com.school.mamagement.exception.*;
import com.school.mamagement.repository.AttendanceRepository;
import com.school.mamagement.repository.StudentRepository;
import com.school.mamagement.repository.EnrollmentRepository;
import com.school.mamagement.enums.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceService {
    private final AttendanceRepository repository;
    private final StudentRepository students;
    private final EnrollmentRepository enrollments;

    public AttendanceResponse create(AttendanceCreateRequest r) {
        if (r.getAttendanceDate().isAfter(LocalDate.now()))
            throw new BusinessRuleException("Attendance date cannot be in the future");
        Student s = student(r.getStudentId());
        requireActiveEnrollment(s.getId());
        if (repository.existsByStudentIdAndAttendanceDate(s.getId(), r.getAttendanceDate()))
            throw new DuplicateResourceException("Attendance already recorded for this student and date");
        Attendance e = new Attendance();
        e.setStudent(s);
        e.setAttendanceDate(r.getAttendanceDate());
        e.setStatus(r.getStatus());
        e.setRemarks(clean(r.getRemarks()));
        return map(repository.save(e));
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> byStudent(Long id, LocalDate from, LocalDate to) {
        if (to.isBefore(from)) throw new BusinessRuleException("to date must not be before from date");
        student(id);
        requireActiveEnrollment(id);
        return repository.findByStudentIdAndAttendanceDateBetween(id, from, to).stream().map(this::map).toList();
    }

    public AttendanceResponse update(Long id, AttendanceCreateRequest r) {
        Attendance e = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Attendance not found: " + id));
        if (!e.getStudent().getId().equals(r.getStudentId()))
            throw new BusinessRuleException("Student cannot be changed on an attendance record");
        if (r.getAttendanceDate().isAfter(LocalDate.now()))
            throw new BusinessRuleException("Attendance date cannot be in the future");
        requireActiveEnrollment(e.getStudent().getId());
        if (!e.getAttendanceDate().equals(r.getAttendanceDate()) && repository.existsByStudentIdAndAttendanceDate(e.getStudent().getId(), r.getAttendanceDate()))
            throw new DuplicateResourceException("Attendance already recorded for this student and date");
        e.setAttendanceDate(r.getAttendanceDate());
        e.setStatus(r.getStatus());
        e.setRemarks(clean(r.getRemarks()));
        return map(repository.save(e));
    }

    public void delete(Long id) {
        repository.delete(repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Attendance not found: " + id)));
    }

    private Student student(Long id) {
        return students.findById(id).orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }

    private void requireActiveEnrollment(Long studentId) {
        if (enrollments.findByStudentId(studentId).stream().noneMatch(e -> e.getStatus() == EnrollmentStatus.ACTIVE))
            throw new BusinessRuleException("Student must have an ACTIVE enrollment before attendance can be recorded");
    }

    private String clean(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private AttendanceResponse map(Attendance e) {
        return AttendanceResponse.builder().id(e.getId()).studentId(e.getStudent().getId()).studentCode(e.getStudent().getStudentCode()).attendanceDate(e.getAttendanceDate()).status(e.getStatus()).remarks(e.getRemarks()).createdAt(e.getCreatedAt()).updatedAt(e.getUpdatedAt()).build();
    }
}
