package com.school.mamagement.controller;


import com.school.mamagement.dto.request.AdminCreateRequest;
import com.school.mamagement.dto.request.AdminUpdateRequest;
import com.school.mamagement.dto.response.AdminResponse;
import com.school.mamagement.exception.ErrorResponseDto;
import com.school.mamagement.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admins")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('SUPER_ADMIN')")
@Tag(name = "Admin Management", description = "Manage school administrator records")
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    @Operation(summary = "Create admin")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Admin created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Request conflicts with existing data", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<AdminResponse> createAdmin(
            @Valid @RequestBody AdminCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminService.createAdmin(request));
    }

    @GetMapping
    @Operation(summary = "Get all admins")
    @ApiResponse(responseCode = "200", description = "Admins retrieved successfully")
    public ResponseEntity<List<AdminResponse>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get admin by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin found"),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<AdminResponse> getAdminById(
            @Parameter(description = "Admin ID", example = "1")
            @PathVariable @Positive Long id) {
        return ResponseEntity.ok(adminService.getAdminById(id));
    }

    @GetMapping("/code/{adminCode}")
    @Operation(summary = "Get admin by admin code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin found"),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<AdminResponse> getAdminByCode(
            @Parameter(description = "Unique admin code", example = "ADM-001")
            @PathVariable String adminCode) {
        return ResponseEntity.ok(adminService.getAdminByCode(adminCode));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update admin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Admin updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Request conflicts with existing data", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<AdminResponse> updateAdmin(
            @Parameter(description = "Admin ID", example = "1")
            @PathVariable @Positive Long id,
            @Valid @RequestBody AdminUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateAdmin(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete admin")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Admin deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Resource not found", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<Void> deleteAdmin(
            @Parameter(description = "Admin ID", example = "1")
            @PathVariable @Positive Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
