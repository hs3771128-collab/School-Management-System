package com.school.mamagement.controller;

import com.school.mamagement.dto.request.academic.TeacherAssignmentCreateRequest;
import com.school.mamagement.dto.response.academic.TeacherAssignmentResponse;
import com.school.mamagement.service.TeacherAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teacher-assignments")
@RequiredArgsConstructor
@Tag(
        name = "Teacher Assignment Management",
        description = "Assign teachers to subjects and sections for academic years"
)
public class TeacherAssignmentController {

    private final TeacherAssignmentService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(
            summary = "Assign teacher to a subject and section"
    )
    public ResponseEntity<TeacherAssignmentResponse> create(
            @Valid @RequestBody TeacherAssignmentCreateRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(
            summary = "Get all teacher assignments"
    )
    public ResponseEntity<List<TeacherAssignmentResponse>> getAll() {

        return ResponseEntity.ok(
                service.getAll()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(
            summary = "Get teacher assignment by ID"
    )
    public ResponseEntity<TeacherAssignmentResponse> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                service.getById(id)
        );
    }

    @GetMapping("/teacher/{teacherId}/academic-year/{academicYearId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','TEACHER')")
    @Operation(
            summary = "Get teacher assignments for an academic year"
    )
    public ResponseEntity<List<TeacherAssignmentResponse>>
    getByTeacherAndAcademicYear(
            @PathVariable Long teacherId,
            @PathVariable Long academicYearId
    ) {

        return ResponseEntity.ok(
                service.getByTeacherAndAcademicYear(
                        teacherId,
                        academicYearId
                )
        );
    }

    @GetMapping("/section/{sectionId}/academic-year/{academicYearId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','TEACHER')")
    @Operation(
            summary = "Get teacher assignments for a section"
    )
    public ResponseEntity<List<TeacherAssignmentResponse>>
    getBySectionAndAcademicYear(
            @PathVariable Long sectionId,
            @PathVariable Long academicYearId
    ) {

        return ResponseEntity.ok(
                service.getBySectionAndAcademicYear(
                        sectionId,
                        academicYearId
                )
        );
    }

    @GetMapping("/academic-year/{academicYearId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','TEACHER')")
    @Operation(
            summary = "Get all teacher assignments for an academic year"
    )
    public ResponseEntity<List<TeacherAssignmentResponse>>
    getByAcademicYear(
            @PathVariable Long academicYearId
    ) {

        return ResponseEntity.ok(
                service.getByAcademicYear(
                        academicYearId
                )
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(
            summary = "Update teacher assignment"
    )
    public ResponseEntity<TeacherAssignmentResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TeacherAssignmentCreateRequest request
    ) {

        return ResponseEntity.ok(
                service.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(
            summary = "Delete teacher assignment"
    )
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}