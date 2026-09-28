package com.school.mamagement.service;

import com.school.mamagement.dto.request.SuperAdminCreateRequest;
import com.school.mamagement.dto.request.SuperAdminUpdateRequest;
import com.school.mamagement.dto.response.SuperAdminResponse;
import com.school.mamagement.entity.SuperAdmin;
import com.school.mamagement.enums.AccountStatus;
import com.school.mamagement.enums.Gender;
import com.school.mamagement.exception.BusinessRuleException;
import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.exception.ResourceNotFoundException;
import com.school.mamagement.repository.SuperAdminRepository;
import com.school.mamagement.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class SuperAdminService {

    private final SuperAdminRepository superAdminRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountSyncService userAccountSyncService;
    private final ProfileAccountProvisioningService profileAccountProvisioningService;
    private final SystemCodeGenerator systemCodeGenerator;



    // CREATE SUPER ADMIN


    public SuperAdminResponse createSuperAdmin(
            SuperAdminCreateRequest request
    ) {

        String code =
                systemCodeGenerator.superAdminCode();

        String username =
                normalizeUsername(request.getUsername());

        String email =
                normalizeOptional(request.getEmail());

        /*
         * Username must be unique.
         *
         * Usernames are handled case-insensitively.
         */
        if (superAdminRepository.existsByUsernameIgnoreCase(username)
                || userAccountRepository.existsByUsernameIgnoreCase(username)) {

            throw new DuplicateResourceException(
                    "Username already exists: " + username
            );
        }

        /*
         * Email is optional, but if supplied it must be unique.
         */
        if (email != null
                && superAdminRepository.existsByEmail(email)) {

            throw new DuplicateResourceException(
                    "Email already exists: " + email
            );
        }

        SuperAdmin superAdmin = new SuperAdmin();

        superAdmin.setSuperAdminCode(code);

        superAdmin.setUsername(username);

        applyPersonFields(
                superAdmin,
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getGender(),
                email,
                request.getPhone(),
                request.getAddress()
        );

        superAdmin.setStatus(
                request.getStatus() == null
                        ? AccountStatus.ACTIVE
                        : request.getStatus()
        );

        SuperAdmin savedSuperAdmin =
                superAdminRepository.save(superAdmin);

        profileAccountProvisioningService.provision(
                savedSuperAdmin.getUsername(),
                request.getPassword(),
                com.school.mamagement.enums.UserRole.SUPER_ADMIN,
                savedSuperAdmin.getStatus(),
                ProfileAccountProvisioningService.ProfileType.SUPER_ADMIN,
                savedSuperAdmin.getId());

        return mapToResponse(savedSuperAdmin);
    }



    // GET ALL SUPER ADMINS


    @Transactional(readOnly = true)
    public List<SuperAdminResponse> getAllSuperAdmins() {

        return superAdminRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    // GET SUPER ADMIN BY ID


    @Transactional(readOnly = true)
    public SuperAdminResponse getSuperAdminById(
            Long id
    ) {

        SuperAdmin superAdmin =
                findSuperAdminById(id);

        return mapToResponse(superAdmin);
    }



    // GET SUPER ADMIN BY CODE


    @Transactional(readOnly = true)
    public SuperAdminResponse getSuperAdminByCode(
            String code
    ) {

        String normalizedCode =
                normalizeRequired(code);

        SuperAdmin superAdmin =
                superAdminRepository
                        .findBySuperAdminCode(normalizedCode)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Super admin not found with code: "
                                                + normalizedCode
                                )
                        );

        return mapToResponse(superAdmin);
    }



    // UPDATE SUPER ADMIN


    public SuperAdminResponse updateSuperAdmin(
            Long id,
            SuperAdminUpdateRequest request
    ) {

        SuperAdmin superAdmin =
                findSuperAdminById(id);

        String email=
                normalizeOptional(request.getEmail());

        /*
         * Email uniqueness check.
         *
         * Ignore the current Super Admin's own email.
         */
        if (email != null
                && !email.equalsIgnoreCase(superAdmin.getEmail())
                && superAdminRepository.existsByEmail(email)) {

            throw new DuplicateResourceException(
                    "Email already exists: " + email
            );
        }

        applyPersonFields(
                superAdmin,
                request.getFirstName(),
                request.getLastName(),
                request.getDateOfBirth(),
                request.getGender(),
                email,
                request.getPhone(),
                request.getAddress()
        );

        /*
         * Status is optional during update.
         *
         * If it is null, keep the existing status.
         */
        if (request.getStatus() != null) {
            superAdmin.setStatus(request.getStatus());
            userAccountSyncService.syncSuperAdminStatus(superAdmin.getId(), request.getStatus());
        }

        SuperAdmin updatedSuperAdmin =
                superAdminRepository.save(superAdmin);

        return mapToResponse(updatedSuperAdmin);
    }



    // DELETE SUPER ADMIN


    public void deleteSuperAdmin(
            Long id
    ) {

        SuperAdmin superAdmin =
                findSuperAdminById(id);

        /*
         * Protect the last Super Admin profile.
         */
        if (superAdminRepository.count() <= 1) {

            throw new BusinessRuleException(
                    "The last super admin cannot be deleted. "
                            + "Create another super admin or deactivate this account."
            );
        }

        if (userAccountRepository.existsBySuperAdminId(id)) {
            throw new BusinessRuleException(
                    "Super admin cannot be deleted while an authentication account is linked. Deactivate the profile instead.");
        }

        superAdminRepository.delete(superAdmin);
    }



    // FIND SUPER ADMIN


    private SuperAdmin findSuperAdminById(
            Long id
    ) {

        return superAdminRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Super admin not found with id: " + id
                        )
                );
    }



    // APPLY PERSON FIELDS


    private void applyPersonFields(
            SuperAdmin superAdmin,
            String firstName,
            String lastName,
            LocalDate dateOfBirth,
            Gender gender,
            String email,
            String phone,
            String address
    ) {

        superAdmin.setFirstName(
                normalizeRequired(firstName)
        );

        superAdmin.setLastName(
                normalizeRequired(lastName)
        );

        superAdmin.setDateOfBirth(
                dateOfBirth
        );

        superAdmin.setGender(
                gender
        );

        superAdmin.setEmail(
                email
        );

        superAdmin.setPhone(
                normalizeOptional(phone)
        );

        superAdmin.setAddress(
                normalizeOptional(address)
        );
    }



    // RESPONSE MAPPING


    private SuperAdminResponse mapToResponse(
            SuperAdmin superAdmin
    ) {

        return SuperAdminResponse.builder()
                .id(superAdmin.getId())
                .superAdminCode(
                        superAdmin.getSuperAdminCode()
                )
                .username(
                        superAdmin.getUsername()
                )
                .firstName(
                        superAdmin.getFirstName()
                )
                .lastName(
                        superAdmin.getLastName()
                )
                .dateOfBirth(
                        superAdmin.getDateOfBirth()
                )
                .gender(
                        superAdmin.getGender()
                )
                .email(
                        superAdmin.getEmail()
                )
                .phone(
                        superAdmin.getPhone()
                )
                .address(
                        superAdmin.getAddress()
                )
                .status(
                        superAdmin.getStatus()
                )
                .createdAt(
                        superAdmin.getCreatedAt()
                )
                .updatedAt(
                        superAdmin.getUpdatedAt()
                )
                .build();
    }



    // USERNAME NORMALIZATION


    private String normalizeUsername(
            String value
    ) {

        return normalizeRequired(value)
                .toLowerCase(Locale.ROOT);
    }



    // REQUIRED STRING NORMALIZATION


    private String normalizeRequired(
            String value
    ) {

        return value == null
                ? null
                : value.trim();
    }



    // OPTIONAL STRING NORMALIZATION


    private String normalizeOptional(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String trimmed =
                value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }
}