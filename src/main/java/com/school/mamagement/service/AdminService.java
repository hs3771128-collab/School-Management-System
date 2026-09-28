package com.school.mamagement.service;

import com.school.mamagement.dto.request.AdminCreateRequest;
import com.school.mamagement.dto.request.AdminUpdateRequest;
import com.school.mamagement.dto.response.AdminResponse;
import com.school.mamagement.entity.Admin;
import com.school.mamagement.enums.AccountStatus;
import com.school.mamagement.exception.BusinessRuleException;
import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.exception.ResourceNotFoundException;
import com.school.mamagement.repository.AdminRepository;
import com.school.mamagement.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountSyncService userAccountSyncService;
    private final ProfileAccountProvisioningService profileAccountProvisioningService;
    private final SystemCodeGenerator systemCodeGenerator;

    public AdminResponse createAdmin(AdminCreateRequest request) {
        String adminCode = systemCodeGenerator.adminCode();
        String username = normalizeUsername(request.getUsername());
        String email = normalizeOptional(request.getEmail());

        validateUniqueUsername(username);
        validateUniqueEmail(email);

        Admin admin = new Admin();
        admin.setAdminCode(adminCode);
        admin.setUsername(username);
        applyPersonFields(admin, request.getFirstName(), request.getLastName(),
                request.getDateOfBirth(), request.getGender(), email,
                request.getPhone(), request.getAddress());
        admin.setStatus(request.getStatus() == null ? AccountStatus.ACTIVE : request.getStatus());

        Admin savedAdmin = adminRepository.save(admin);

        profileAccountProvisioningService.provision(
                savedAdmin.getUsername(),
                request.getPassword(),
                com.school.mamagement.enums.UserRole.ADMIN,
                savedAdmin.getStatus(),
                ProfileAccountProvisioningService.ProfileType.ADMIN,
                savedAdmin.getId());

        return mapToResponse(savedAdmin);
    }

    @Transactional(readOnly = true)
    public List<AdminResponse> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminResponse getAdminById(Long id) {
        return mapToResponse(findAdminById(id));
    }

    @Transactional(readOnly = true)
    public AdminResponse getAdminByCode(String adminCode) {
        String normalizedCode = normalizeRequired(adminCode);
        Admin admin = adminRepository.findByAdminCode(normalizedCode)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Admin not found with code: " + normalizedCode));
        return mapToResponse(admin);
    }

    public AdminResponse updateAdmin(Long id, AdminUpdateRequest request) {
        Admin admin = findAdminById(id);
        String email = normalizeOptional(request.getEmail());

        if (email != null
                && !email.equalsIgnoreCase(admin.getEmail())
                && adminRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists: " + email);
        }

        applyPersonFields(admin, request.getFirstName(), request.getLastName(),
                request.getDateOfBirth(), request.getGender(), email,
                request.getPhone(), request.getAddress());

        if (request.getStatus() != null) {
            admin.setStatus(request.getStatus());
            userAccountSyncService.syncAdminStatus(admin.getId(), request.getStatus());
        }

        return mapToResponse(adminRepository.save(admin));
    }

    public void deleteAdmin(Long id) {
        Admin admin = findAdminById(id);
        if (userAccountRepository.existsByAdminId(id)) {
            throw new BusinessRuleException(
                    "Admin cannot be deleted while an authentication account is linked. Deactivate the profile instead.");
        }
        adminRepository.delete(admin);
    }

    private Admin findAdminById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Admin not found with id: " + id));
    }

    private void validateUniqueUsername(String username) {
        if (adminRepository.existsByUsername(username)
                || userAccountRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException(
                    "Username already exists: " + username);
        }
    }

    private void validateUniqueEmail(String email) {
        if (email != null && adminRepository.existsByEmail(email)) {
            throw new DuplicateResourceException(
                    "Email already exists: " + email);
        }
    }

    private void applyPersonFields(
            Admin admin,
            String firstName,
            String lastName,
            java.time.LocalDate dateOfBirth,
            com.school.mamagement.enums.Gender gender,
            String email,
            String phone,
            String address) {

        admin.setFirstName(normalizeRequired(firstName));
        admin.setLastName(normalizeRequired(lastName));
        admin.setDateOfBirth(dateOfBirth);
        admin.setGender(gender);
        admin.setEmail(email);
        admin.setPhone(normalizeOptional(phone));
        admin.setAddress(normalizeOptional(address));
    }

    private AdminResponse mapToResponse(Admin admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .adminCode(admin.getAdminCode())
                .username(admin.getUsername())
                .firstName(admin.getFirstName())
                .lastName(admin.getLastName())
                .dateOfBirth(admin.getDateOfBirth())
                .gender(admin.getGender())
                .email(admin.getEmail())
                .phone(admin.getPhone())
                .address(admin.getAddress())
                .status(admin.getStatus())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .build();
    }

    private String normalizeUsername(String value) {
        return normalizeRequired(value).toLowerCase(java.util.Locale.ROOT);
    }

    private String normalizeRequired(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
