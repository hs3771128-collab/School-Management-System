package com.school.mamagement.service;

import com.school.mamagement.dto.request.BootstrapRequest;
import com.school.mamagement.dto.request.ChangePasswordRequest;
import com.school.mamagement.dto.request.CreateAuthAccountRequest;
import com.school.mamagement.dto.request.LoginRequest;
import com.school.mamagement.dto.response.CurrentUserResponse;
import com.school.mamagement.dto.response.LoginResponse;
import com.school.mamagement.entity.Admin;
import com.school.mamagement.entity.ParentGuardian;
import com.school.mamagement.entity.Student;
import com.school.mamagement.entity.SuperAdmin;
import com.school.mamagement.entity.Teacher;
import com.school.mamagement.entity.UserAccount;
import com.school.mamagement.enums.AccountStatus;
import com.school.mamagement.enums.UserRole;
import com.school.mamagement.enums.StudentStatus;
import com.school.mamagement.enums.TeacherStatus;
import com.school.mamagement.exception.AuthenticationFailedException;
import com.school.mamagement.exception.BusinessRuleException;
import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.exception.ResourceNotFoundException;
import com.school.mamagement.repository.AdminRepository;
import com.school.mamagement.repository.ParentGuardianRepository;
import com.school.mamagement.repository.StudentRepository;
import com.school.mamagement.repository.SuperAdminRepository;
import com.school.mamagement.repository.TeacherRepository;
import com.school.mamagement.repository.UserAccountRepository;
import com.school.mamagement.security.AccountProvisioningPolicy;
import com.school.mamagement.security.CustomUserDetails;
import com.school.mamagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserAccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final AdminRepository adminRepository;

    private final SuperAdminRepository superAdminRepository;

    private final TeacherRepository teacherRepository;

    private final StudentRepository studentRepository;

    private final ParentGuardianRepository parentGuardianRepository;

    private final AccountProvisioningPolicy accountProvisioningPolicy;

    private final SystemCodeGenerator systemCodeGenerator;

    @Value("${app.security.bootstrap.enabled:false}")
    private boolean bootstrapEnabled;


    // ============================================================
    // LOGIN
    // ============================================================

    public LoginResponse login(LoginRequest request) {

        String username = normalize(request.getUsername());

        try {

            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    request.getPassword()
                            )
                    );

            CustomUserDetails details =
                    (CustomUserDetails) authentication.getPrincipal();

            UserAccount account = details.getAccount();

            account.setLastLoginAt(LocalDateTime.now());
            account.setFailedLoginAttempts(0);
            account.setLockedUntil(null);

            String token = jwtService.generateToken(details);

            LocalDateTime expiresAt =
                    LocalDateTime.now()
                            .plusSeconds(jwtService.getExpirationSeconds());

            return LoginResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresInSeconds(
                            jwtService.getExpirationSeconds()
                    )
                    .username(account.getUsername())
                    .role(account.getRole())
                    .expiresAt(expiresAt)
                    .build();

        } catch (AuthenticationException ex) {

            accountRepository
                    .findByUsernameIgnoreCase(username)
                    .ifPresent(this::registerFailedLogin);

            throw new AuthenticationFailedException(
                    "Invalid username or password"
            );
        }
    }


    // ============================================================
    // CURRENT USER
    // ============================================================

    public CurrentUserResponse me() {

        CustomUserDetails details = currentUser();

        UserAccount account = details.getAccount();

        return map(account);
    }


    // ============================================================
    // CHANGE PASSWORD
    // ============================================================

    public void changePassword(
            ChangePasswordRequest request
    ) {

        CustomUserDetails details = currentUser();

        UserAccount account = details.getAccount();

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                account.getPasswordHash()
        )) {

            throw new BusinessRuleException(
                    "Current password is incorrect"
            );
        }

        if (passwordEncoder.matches(
                request.getNewPassword(),
                account.getPasswordHash()
        )) {

            throw new BusinessRuleException(
                    "New password must be different from the current password"
            );
        }

        account.setPasswordHash(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );
    }


    // ============================================================
    // CREATE AUTHENTICATION ACCOUNT
    // ============================================================

    public CurrentUserResponse createAccount(
            CreateAuthAccountRequest request
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        /*
         * SecurityConfig / @PreAuthorize provides the first
         * authorization layer.
         *
         * AccountProvisioningPolicy provides the second,
         * fine-grained authorization layer.
         */
        if (!accountProvisioningPolicy.canCreate(
                authentication,
                request.getRole()
        )) {

            throw new BusinessRuleException(
                    "You are not authorized to create an account with role: "
                            + request.getRole()
            );
        }

        String username = normalize(request.getUsername());

        /*
         * Exactly one business profile must be associated
         * with an authentication account.
         */
        validateProfileReference(
                request.getRole(),
                request
        );

        if (accountRepository.existsByUsernameIgnoreCase(username)) {

            throw new DuplicateResourceException(
                    "Username already exists: " + username
            );
        }

        UserAccount account = new UserAccount();

        account.setUsername(username);

        /*
         * NEVER store a raw password.
         */
        account.setPasswordHash(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        account.setRole(request.getRole());

        account.setStatus(AccountStatus.ACTIVE);

        account.setAdminId(request.getAdminId());
        account.setSuperAdminId(request.getSuperAdminId());
        account.setTeacherId(request.getTeacherId());
        account.setStudentId(request.getStudentId());
        account.setParentGuardianId(request.getParentGuardianId());

        UserAccount savedAccount =
                accountRepository.save(account);

        return map(savedAccount);
    }


    // ============================================================
    // INITIAL SUPER ADMIN BOOTSTRAP
    // ============================================================

    public CurrentUserResponse bootstrap(
            BootstrapRequest request
    ) {

        /*
         * Rule 1:
         * Bootstrap must be explicitly enabled.
         */
        if (!bootstrapEnabled) {

            throw new BusinessRuleException(
                    "Authentication bootstrap is disabled"
            );
        }

        /*
         * Rule 2:
         * Bootstrap is allowed only when there are no
         * authentication accounts.
         *
         * This guarantees that Bootstrap is a
         * first-installation operation.
         */
        if (accountRepository.count() != 0) {

            throw new BusinessRuleException(
                    "Bootstrap is available only when no authentication account exists"
            );
        }

        String username = normalize(request.getUsername());

        /*
         * Defensive duplicate check.
         */
        if (accountRepository.existsByUsernameIgnoreCase(username)) {

            throw new DuplicateResourceException(
                    "Username already exists: " + username
            );
        }

        /*
         * --------------------------------------------------------
         * STEP 1: Create the first SuperAdmin profile
         * --------------------------------------------------------
         *
         * The database generates the SuperAdmin ID.
         *
         * Bootstrap therefore does NOT require superAdminId.
         */

        if (superAdminRepository.existsByUsernameIgnoreCase(
                request.getUsername()
        )) {
            throw new DuplicateResourceException(
                    "Super admin username already exists: "
                            + request.getUsername()
            );
        }

        SuperAdmin superAdmin = new SuperAdmin();

        superAdmin.setSuperAdminCode(
                systemCodeGenerator.superAdminCode()
        );

        superAdmin.setUsername(username);

        superAdmin.setFirstName(
                request.getFirstName()
        );

        superAdmin.setLastName(
                request.getLastName()
        );

        superAdmin.setDateOfBirth(
                request.getDateOfBirth()
        );

        superAdmin.setGender(
                request.getGender()
        );

        superAdmin.setEmail(
                request.getEmail()
        );

        superAdmin.setPhone(
                request.getPhone()
        );

        superAdmin.setAddress(
                request.getAddress()
        );

        superAdmin.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : AccountStatus.ACTIVE
        );

        SuperAdmin savedSuperAdmin =
                superAdminRepository.save(superAdmin);


        /*
         * --------------------------------------------------------
         * STEP 2: Create authentication account
         * --------------------------------------------------------
         */

        UserAccount account = new UserAccount();

        account.setUsername(username);

        /*
         * NEVER store raw password.
         */
        account.setPasswordHash(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        /*
         * Bootstrap ALWAYS creates SUPER_ADMIN.
         */
        account.setRole(UserRole.SUPER_ADMIN);

        account.setStatus(AccountStatus.ACTIVE);

        /*
         * Link authentication account to the newly
         * created SuperAdmin profile.
         */
        account.setSuperAdminId(
                savedSuperAdmin.getId()
        );

        UserAccount savedAccount =
                accountRepository.save(account);

        /*
         * Both records are inside the same transaction.
         *
         * If account creation fails, the SuperAdmin
         * profile creation is rolled back as well.
         */

        return map(savedAccount);
    }


    // ============================================================
    // FAILED LOGIN / ACCOUNT LOCK
    // ============================================================

    private void registerFailedLogin(
            UserAccount account
    ) {

        if (account.getLockedUntil() != null
                && account.getLockedUntil()
                .isAfter(LocalDateTime.now())) {

            return;
        }

        int attempts =
                account.getFailedLoginAttempts() + 1;

        account.setFailedLoginAttempts(attempts);

        /*
         * Five failed attempts:
         * lock account for 15 minutes.
         */
        if (attempts >= 5) {

            account.setLockedUntil(
                    LocalDateTime.now()
                            .plusMinutes(15)
            );

            account.setFailedLoginAttempts(0);
        }
    }


    // ============================================================
    // PROFILE VALIDATION
    // ============================================================

    private void validateProfileReference(
            UserRole role,
            CreateAuthAccountRequest request
    ) {

        int profileCount =
                java.util.stream.Stream.of(
                                request.getAdminId(),
                                request.getSuperAdminId(),
                                request.getTeacherId(),
                                request.getStudentId(),
                                request.getParentGuardianId()
                        )
                        .mapToInt(
                                value ->
                                        value == null ? 0 : 1
                        )
                        .sum();

        /*
         * Every authentication account must belong
         * to exactly one profile.
         */
        if (profileCount != 1) {

            throw new BusinessRuleException(
                    "Exactly one profile id must be supplied for an authentication account"
            );
        }

        switch (role) {

            case ADMIN -> {

                if (request.getAdminId() == null) {

                    throw new BusinessRuleException(
                            "adminId is required for ADMIN"
                    );
                }

                Admin admin = adminRepository.findByIdForUpdate(request.getAdminId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Admin not found with id: " + request.getAdminId()));
                if (admin.getStatus() != AccountStatus.ACTIVE) {
                    throw new BusinessRuleException("Cannot create an authentication account for an inactive admin");
                }

                if (accountRepository.existsByAdminId(
                        request.getAdminId()
                )) {

                    throw new DuplicateResourceException(
                            "Admin already has an authentication account"
                    );
                }
            }

            case SUPER_ADMIN -> {

                if (request.getSuperAdminId() == null) {

                    throw new BusinessRuleException(
                            "superAdminId is required for SUPER_ADMIN"
                    );
                }

                SuperAdmin superAdmin = superAdminRepository.findByIdForUpdate(request.getSuperAdminId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Super admin not found with id: " + request.getSuperAdminId()));
                if (superAdmin.getStatus() != AccountStatus.ACTIVE) {
                    throw new BusinessRuleException("Cannot create an authentication account for an inactive super admin");
                }

                if (accountRepository.existsBySuperAdminId(
                        request.getSuperAdminId()
                )) {

                    throw new DuplicateResourceException(
                            "Super admin already has an authentication account"
                    );
                }
            }

            case TEACHER -> {

                if (request.getTeacherId() == null) {

                    throw new BusinessRuleException(
                            "teacherId is required for TEACHER"
                    );
                }

                Teacher teacher = teacherRepository.findByIdForUpdate(request.getTeacherId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Teacher not found with id: " + request.getTeacherId()));
                if (teacher.getStatus() != TeacherStatus.ACTIVE) {
                    throw new BusinessRuleException("Cannot create an authentication account for an inactive teacher");
                }

                if (accountRepository.existsByTeacherId(
                        request.getTeacherId()
                )) {

                    throw new DuplicateResourceException(
                            "Teacher already has an authentication account"
                    );
                }
            }

            case STUDENT -> {

                if (request.getStudentId() == null) {

                    throw new BusinessRuleException(
                            "studentId is required for STUDENT"
                    );
                }

                Student student = studentRepository.findByIdForUpdate(request.getStudentId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Student not found with id: " + request.getStudentId()));
                if (student.getStatus() != StudentStatus.ACTIVE) {
                    throw new BusinessRuleException("Cannot create an authentication account for an inactive student");
                }

                if (accountRepository.existsByStudentId(
                        request.getStudentId()
                )) {

                    throw new DuplicateResourceException(
                            "Student already has an authentication account"
                    );
                }
            }

            case PARENT_GUARDIAN -> {

                if (request.getParentGuardianId() == null) {

                    throw new BusinessRuleException(
                            "parentGuardianId is required for PARENT_GUARDIAN"
                    );
                }

                parentGuardianRepository.findByIdForUpdate(request.getParentGuardianId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Parent/Guardian not found with id: " + request.getParentGuardianId()));

                if (accountRepository.existsByParentGuardianId(
                        request.getParentGuardianId()
                )) {

                    throw new DuplicateResourceException(
                            "Parent/Guardian already has an authentication account"
                    );
                }
            }
        }
    }


    // ============================================================
    // CURRENT AUTHENTICATED USER
    // ============================================================

    private CustomUserDetails currentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal()
                instanceof CustomUserDetails details)) {

            throw new BusinessRuleException(
                    "Authentication is required"
            );
        }

        return details;
    }


    // ============================================================
    // RESPONSE MAPPING
    // ============================================================

    private CurrentUserResponse map(
            UserAccount account
    ) {

        return CurrentUserResponse.builder()
                .id(account.getId())
                .username(account.getUsername())
                .role(account.getRole())
                .status(account.getStatus())
                .build();
    }


    // ============================================================
    // USERNAME NORMALIZATION
    // ============================================================

    private String normalize(
            String value
    ) {

        return value
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}