package com.school.mamagement.controller;

import com.school.mamagement.dto.request.attendance.AttendanceCreateRequest;
import com.school.mamagement.dto.response.attendance.AttendanceResponse;
import com.school.mamagement.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','TEACHER')")
public class AttendanceController {
    private final AttendanceService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AttendanceResponse create(@Valid @RequestBody AttendanceCreateRequest r) {
        return service.create(r);
    }

    @GetMapping("/student/{studentId}")
    public List<AttendanceResponse> byStudent(@PathVariable Long studentId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return service.byStudent(studentId, from, to);
    }

    @PutMapping("/{id}")
    public AttendanceResponse update(@PathVariable Long id, @Valid @RequestBody AttendanceCreateRequest r) {
        return service.update(id, r);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
