package com.school.mamagement.service;

import com.school.mamagement.enums.AttendanceStatus;
import com.school.mamagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {
    private final StudentRepository students;
    private final TeacherRepository teachers;
    private final ParentGuardianRepository parents;
    private final EnrollmentRepository enrollments;
    private final AttendanceRepository attendance;
    private final ExamRepository exams;
    private final FeeInvoiceRepository invoices;
    private final PaymentRepository payments;

    public Map<String, Object> dashboard() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("students", students.count());
        r.put("teachers", teachers.count());
        r.put("parentGuardians", parents.count());
        r.put("enrollments", enrollments.count());
        r.put("attendanceRecords", attendance.count());
        r.put("exams", exams.count());
        r.put("feeInvoices", invoices.count());
        r.put("payments", payments.count());
        return r;
    }

    public Map<String, Object> studentAttendance(Long studentId) {
        if (!students.existsById(studentId))
            throw new com.school.mamagement.exception.ResourceNotFoundException("Student not found: " + studentId);
        var all = attendance.findByStudentIdAndAttendanceDateBetween(studentId, java.time.LocalDate.of(2000, 1, 1), java.time.LocalDate.now());
        long present = all.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long absent = all.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long late = all.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("studentId", studentId);
        r.put("totalRecords", all.size());
        r.put("present", present);
        r.put("absent", absent);
        r.put("late", late);
        r.put("attendancePercentage", all.isEmpty() ? BigDecimal.ZERO : BigDecimal.valueOf(present * 100.0 / all.size()).setScale(2, java.math.RoundingMode.HALF_UP));
        return r;
    }
}
