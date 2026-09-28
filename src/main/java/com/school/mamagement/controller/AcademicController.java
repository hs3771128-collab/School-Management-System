package com.school.mamagement.controller;

import com.school.mamagement.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/v1/academic")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
public class AcademicController {
    private final AcademicYearService years;
    private final SchoolClassService classes;
    private final SectionService sections;
    private final SubjectService subjects;
    private final EnrollmentService enrollments;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/years")
    public com.school.mamagement.dto.response.academic.AcademicYearResponse
    createYear(@Valid @RequestBody com.school.mamagement.dto.request.academic.AcademicYearCreateRequest r) {
        return years.create(r);
    }

    @GetMapping("/years")
    public List<com.school.mamagement.dto.response.academic.AcademicYearResponse> years() {
        return years.all();
    }

    @PutMapping("/years/{id}")
    public com.school.mamagement.dto.response.academic.AcademicYearResponse updateYear(@PathVariable Long id, @Valid @RequestBody com.school.mamagement.dto.request.academic.AcademicYearCreateRequest r) {
        return years.update(id, r);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/years/{id}")
    public void deleteYear(@PathVariable Long id) {
        years.delete(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/classes")
    public com.school.mamagement.dto.response.academic.SchoolClassResponse createClass(@Valid @RequestBody com.school.mamagement.dto.request.academic.SchoolClassCreateRequest r) {
        return classes.create(r);
    }

    @GetMapping("/classes")
    public List<com.school.mamagement.dto.response.academic.SchoolClassResponse> classes() {
        return classes.all();
    }

    @PutMapping("/classes/{id}")
    public com.school.mamagement.dto.response.academic.SchoolClassResponse updateClass(@PathVariable Long id, @Valid @RequestBody com.school.mamagement.dto.request.academic.SchoolClassCreateRequest r) {
        return classes.update(id, r);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/classes/{id}")
    public void deleteClass(@PathVariable Long id) {
        classes.delete(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/sections")
    public com.school.mamagement.dto.response.academic.SectionResponse createSection(@Valid @RequestBody com.school.mamagement.dto.request.academic.SectionCreateRequest r) {
        return sections.create(r);
    }

    @GetMapping("/sections")
    public List<com.school.mamagement.dto.response.academic.SectionResponse> sections() {
        return sections.all();
    }

    @PutMapping("/sections/{id}")
    public com.school.mamagement.dto.response.academic.SectionResponse updateSection(@PathVariable Long id, @Valid @RequestBody com.school.mamagement.dto.request.academic.SectionCreateRequest r) {
        return sections.update(id, r);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/sections/{id}")
    public void deleteSection(@PathVariable Long id) {
        sections.delete(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/subjects")
    public com.school.mamagement.dto.response.academic.SubjectResponse createSubject(@Valid @RequestBody com.school.mamagement.dto.request.academic.SubjectCreateRequest r) {
        return subjects.create(r);
    }

    @GetMapping("/subjects")
    public List<com.school.mamagement.dto.response.academic.SubjectResponse> subjects() {
        return subjects.all();
    }

    @PutMapping("/subjects/{id}")
    public com.school.mamagement.dto.response.academic.SubjectResponse updateSubject(@PathVariable Long id, @Valid @RequestBody com.school.mamagement.dto.request.academic.SubjectCreateRequest r) {
        return subjects.update(id, r);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/subjects/{id}")
    public void deleteSubject(@PathVariable Long id) {
        subjects.delete(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/enrollments")
    public com.school.mamagement.dto.response.academic.EnrollmentResponse createEnrollment(@Valid @RequestBody com.school.mamagement.dto.request.academic.EnrollmentCreateRequest r) {
        return enrollments.create(r);
    }

    @GetMapping("/enrollments")
    public List<com.school.mamagement.dto.response.academic.EnrollmentResponse> enrollments() {
        return enrollments.all();
    }

    @GetMapping("/enrollments/student/{studentId}")
    public List<com.school.mamagement.dto.response.academic.EnrollmentResponse> enrollmentsByStudent(@PathVariable Long studentId) {
        return enrollments.byStudent(studentId);
    }

    @PutMapping("/enrollments/{id}")
    public com.school.mamagement.dto.response.academic.EnrollmentResponse updateEnrollment(@PathVariable Long id, @Valid @RequestBody com.school.mamagement.dto.request.academic.EnrollmentCreateRequest r) {
        return enrollments.update(id, r);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/enrollments/{id}")
    public void deleteEnrollment(@PathVariable Long id) {
        enrollments.delete(id);
    }
}
