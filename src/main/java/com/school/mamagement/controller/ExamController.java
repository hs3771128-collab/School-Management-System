package com.school.mamagement.controller;

import com.school.mamagement.service.ExamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','TEACHER')")
public class ExamController {
    private final ExamService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public com.school.mamagement.dto.response.exam.ExamResponse create(@Valid @RequestBody com.school.mamagement.dto.request.exam.ExamCreateRequest r) {
        return service.createExam(r);
    }

    @GetMapping("/academic-year/{yearId}")
    public List<com.school.mamagement.dto.response.exam.ExamResponse> exams(@PathVariable Long yearId) {
        return service.exams(yearId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/subjects")
    public com.school.mamagement.dto.response.exam.ExamSubjectResponse addSubject(@Valid @RequestBody com.school.mamagement.dto.request.exam.ExamSubjectCreateRequest r) {
        return service.addSubject(r);
    }

    @GetMapping("/{examId}/subjects")
    public List<com.school.mamagement.dto.response.exam.ExamSubjectResponse> subjects(@PathVariable Long examId) {
        return service.subjects(examId);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/marks")
    public com.school.mamagement.dto.response.exam.MarkResponse mark(@Valid @RequestBody com.school.mamagement.dto.request.exam.MarkCreateRequest r) {
        return service.recordMark(r);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{examId}/students/{studentId}/result")
    public com.school.mamagement.dto.response.exam.ResultResponse generate(@PathVariable Long examId, @PathVariable Long studentId) {
        return service.generateResult(examId, studentId);
    }

    @GetMapping("/{examId}/students/{studentId}/result")
    public com.school.mamagement.dto.response.exam.ResultResponse result(@PathVariable Long examId, @PathVariable Long studentId) {
        return service.getResult(examId, studentId);
    }
}
