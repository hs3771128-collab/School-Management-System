package com.school.mamagement.controller;

import com.school.mamagement.dto.request.BootstrapRequest;
import com.school.mamagement.dto.request.ChangePasswordRequest;
import com.school.mamagement.dto.request.CreateAuthAccountRequest;
import com.school.mamagement.dto.request.LoginRequest;
import com.school.mamagement.dto.response.CurrentUserResponse;
import com.school.mamagement.dto.response.LoginResponse;
import com.school.mamagement.exception.ErrorResponseDto;
import com.school.mamagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication, account bootstrap and credential management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a username and password and returns a signed JWT access token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/bootstrap")
    @Operation(
            summary = "Bootstrap the first super admin account",
            description = "Creates the first SUPER_ADMIN authentication account when authentication bootstrap is enabled and no authentication account exists."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Authentication account created"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Super admin profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Bootstrap is disabled, already completed, or conflicts with existing account data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<CurrentUserResponse> bootstrap(
            @Valid @RequestBody BootstrapRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.bootstrap(request));
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get current authenticated user",
            description = "Returns the identity, role and account status represented by the authenticated JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user returned"),
            @ApiResponse(responseCode = "401", description = "Authentication is required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<CurrentUserResponse> me() {
        return ResponseEntity.ok(authService.me());
    }

    @PostMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Change current user's password",
            description = "Changes the authenticated user's password after validating the current password."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Authentication is required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Password change violates a business rule",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {

        authService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/accounts")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN')")
    @Operation(
            summary = "Create authentication account",
            description = "Creates a database-backed authentication account for exactly one existing profile. SUPER_ADMIN can provision any supported role; ADMIN can provision TEACHER, STUDENT or PARENT_GUARDIAN accounts."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication account created"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Authentication is required",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Insufficient privileges",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Referenced profile not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Username or profile already has an authentication account",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public CurrentUserResponse createAccount(
            @Valid @RequestBody CreateAuthAccountRequest request) {

        return authService.createAccount(request);
    }
}
