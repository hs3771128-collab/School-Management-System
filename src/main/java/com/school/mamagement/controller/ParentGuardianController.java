package com.school.mamagement.controller;

import io.swagger.v3.oas.annotations.media.Schema;

import io.swagger.v3.oas.annotations.media.Content;

import com.school.mamagement.exception.ErrorResponseDto;

import com.school.mamagement.dto.request.ParentGuardianCreateRequest;
import com.school.mamagement.dto.request.ParentGuardianUpdateRequest;
import com.school.mamagement.dto.response.ParentGuardianResponse;
import com.school.mamagement.service.ParentGuardianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parent-guardians")
@RequiredArgsConstructor
@Tag(
        name = "Parent & Guardian Management",
        description = "Manage parent/guardian relationships assigned to students"
)
public class ParentGuardianController {

    private final ParentGuardianService parentGuardianService;

    @PostMapping
    @Operation(
            summary = "Create parent or guardian",
            description = "Creates a parent/guardian record for a student"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Parent/Guardian created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Request conflicts with existing data", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ParentGuardianResponse> createParentGuardian(
            @Valid @RequestBody ParentGuardianCreateRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parentGuardianService.createParentGuardian(request));
    }

    @GetMapping
    @Operation(summary = "Get all parent/guardian records")
    @ApiResponse(responseCode = "200", description = "Records retrieved successfully")
    public ResponseEntity<List<ParentGuardianResponse>> getAllParentGuardians() {
        return ResponseEntity.ok(parentGuardianService.getAllParentGuardians());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parent/guardian by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record found"),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ParentGuardianResponse> getParentGuardianById(
            @Parameter(description = "Parent/Guardian ID", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(parentGuardianService.getParentGuardianById(id));
    }

    @GetMapping("/student/{studentId}")
    @Operation(
            summary = "Get parent/guardian records by student ID",
            description = "Returns all parent/guardian records assigned to the student"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record found"),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<List<ParentGuardianResponse>> getParentGuardiansByStudentId(
            @Parameter(description = "Student ID", example = "1")
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                parentGuardianService.getParentGuardiansByStudentId(studentId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update parent/guardian")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Record updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<ParentGuardianResponse> updateParentGuardian(
            @Parameter(description = "Parent/Guardian ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ParentGuardianUpdateRequest request) {

        return ResponseEntity.ok(
                parentGuardianService.updateParentGuardian(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete parent/guardian")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    public ResponseEntity<Void> deleteParentGuardian(
            @Parameter(description = "Parent/Guardian ID", example = "1")
            @PathVariable Long id) {

        parentGuardianService.deleteParentGuardian(id);
        return ResponseEntity.noContent().build();
    }
}
