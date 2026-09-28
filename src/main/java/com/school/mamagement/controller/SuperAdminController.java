package com.school.mamagement.controller;

import com.school.mamagement.dto.request.SuperAdminCreateRequest;
import com.school.mamagement.dto.request.SuperAdminUpdateRequest;
import com.school.mamagement.dto.response.SuperAdminResponse;
import com.school.mamagement.service.SuperAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/super-admins")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Super Admin Management", description = "Manage system-level super administrator records")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @PostMapping
    @Operation(summary = "Create super admin")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Super admin created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Code, username or email already exists")
    })
    public ResponseEntity<SuperAdminResponse> createSuperAdmin(
            @Valid @RequestBody SuperAdminCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(superAdminService.createSuperAdmin(request));
    }

    @GetMapping
    @Operation(summary = "Get all super admins")
    @ApiResponse(responseCode = "200", description = "Super admins retrieved successfully")
    public ResponseEntity<List<SuperAdminResponse>> getAllSuperAdmins() {
        return ResponseEntity.ok(superAdminService.getAllSuperAdmins());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get super admin by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Super admin found"),
            @ApiResponse(responseCode = "404", description = "Super admin not found")
    })
    public ResponseEntity<SuperAdminResponse> getSuperAdminById(
            @Parameter(description = "Super admin ID", example = "1")
            @PathVariable @Positive Long id) {
        return ResponseEntity.ok(superAdminService.getSuperAdminById(id));
    }

    @GetMapping("/code/{superAdminCode}")
    @Operation(summary = "Get super admin by code")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Super admin found"),
            @ApiResponse(responseCode = "404", description = "Super admin not found")
    })
    public ResponseEntity<SuperAdminResponse> getSuperAdminByCode(
            @Parameter(description = "Unique super admin code", example = "SADM-001")
            @PathVariable String superAdminCode) {
        return ResponseEntity.ok(
                superAdminService.getSuperAdminByCode(superAdminCode));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update super admin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Super admin updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Super admin not found"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    public ResponseEntity<SuperAdminResponse> updateSuperAdmin(
            @Parameter(description = "Super admin ID", example = "1")
            @PathVariable @Positive Long id,
            @Valid @RequestBody SuperAdminUpdateRequest request) {
        return ResponseEntity.ok(
                superAdminService.updateSuperAdmin(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete super admin")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Super admin deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Super admin not found"),
            @ApiResponse(responseCode = "409", description = "The last super admin cannot be deleted")
    })
    public ResponseEntity<Void> deleteSuperAdmin(
            @Parameter(description = "Super admin ID", example = "1")
            @PathVariable @Positive Long id) {
        superAdminService.deleteSuperAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
