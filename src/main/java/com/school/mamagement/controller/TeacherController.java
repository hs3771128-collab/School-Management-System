package com.school.mamagement.controller;

import com.school.mamagement.dto.request.TeacherCreateRequest;
import com.school.mamagement.dto.request.TeacherUpdateRequest;
import com.school.mamagement.dto.response.TeacherResponse;
import com.school.mamagement.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
@Tag(name = "Teacher Management", description = "Manage teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @PostMapping
    @Operation(summary = "Create teacher")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Teacher created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Employee code or email already exists")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<TeacherResponse> createTeacher(
            @Valid @RequestBody TeacherCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teacherService.createTeacher(request));
    }

    @GetMapping
    @Operation(summary = "Get all teachers")
    @ApiResponse(responseCode = "200", description = "Teachers retrieved successfully")
    public ResponseEntity<List<TeacherResponse>> getAllTeachers() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get teacher by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Teacher found"),
            @ApiResponse(responseCode = "404", description = "Teacher not found")
    })
    public ResponseEntity<TeacherResponse> getTeacherById(
            @Parameter(description = "Teacher ID", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @GetMapping("/employee/{employeeCode}")
    @Operation(summary = "Get teacher by employee code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Teacher found"),
            @ApiResponse(responseCode = "404", description = "Teacher not found")
    })
    public ResponseEntity<TeacherResponse> getTeacherByEmployeeCode(
            @Parameter(description = "Unique employee code", example = "EMP-001")
            @PathVariable String employeeCode) {
        return ResponseEntity.ok(teacherService.getTeacherByEmployeeCode(employeeCode));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update teacher")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Teacher updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Teacher not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<TeacherResponse> updateTeacher(
            @Parameter(description = "Teacher ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TeacherUpdateRequest request) {
        return ResponseEntity.ok(teacherService.updateTeacher(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete teacher")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Teacher deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Teacher not found")
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<Void> deleteTeacher(
            @Parameter(description = "Teacher ID", example = "1")
            @PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }
}
