package com.school.mamagement.controller;

import com.school.mamagement.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','TEACHER')")
public class ReportController {
    private final ReportService service;

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return service.dashboard();
    }

    @GetMapping("/students/{studentId}/attendance")
    public Map<String, Object> studentAttendance(@PathVariable Long studentId) {
        return service.studentAttendance(studentId);
    }
}
