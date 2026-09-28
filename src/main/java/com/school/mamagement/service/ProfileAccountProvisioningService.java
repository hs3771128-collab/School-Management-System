package com.school.mamagement.service;

import com.school.mamagement.entity.UserAccount;
import com.school.mamagement.enums.AccountStatus;
import com.school.mamagement.enums.UserRole;
import com.school.mamagement.exception.DuplicateResourceException;
import com.school.mamagement.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;


@Service
@RequiredArgsConstructor
public class ProfileAccountProvisioningService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserAccount provision(
            String username,
            String rawPassword,
            UserRole role,
            AccountStatus status,
            ProfileType profileType,
            Long profileId) {

        String normalizedUsername = normalizeUsername(username);

        if (userAccountRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new DuplicateResourceException(
                    "Username already exists: " + normalizedUsername);
        }

        if (profileId == null || profileType == null || role == null) {
            throw new IllegalArgumentException("Profile, role and profile id are required");
        }

        if (profileType.role() != role) {
            throw new IllegalArgumentException("Profile type does not match authentication role");
        }

        if (hasExistingProfileAccount(profileType, profileId)) {
            throw new DuplicateResourceException(
                    "This profile already has an authentication account");
        }

        UserAccount account = new UserAccount();
        account.setUsername(normalizedUsername);
        account.setPasswordHash(passwordEncoder.encode(rawPassword));
        account.setRole(role);
        account.setStatus(status == null ? AccountStatus.ACTIVE : status);

        switch (profileType) {
            case ADMIN -> account.setAdminId(profileId);
            case SUPER_ADMIN -> account.setSuperAdminId(profileId);
            case TEACHER -> account.setTeacherId(profileId);
            case STUDENT -> account.setStudentId(profileId);
            case PARENT_GUARDIAN -> account.setParentGuardianId(profileId);
        }

        return userAccountRepository.save(account);
    }

    private boolean hasExistingProfileAccount(ProfileType profileType, Long profileId) {
        return switch (profileType) {
            case ADMIN -> userAccountRepository.existsByAdminId(profileId);
            case SUPER_ADMIN -> userAccountRepository.existsBySuperAdminId(profileId);
            case TEACHER -> userAccountRepository.existsByTeacherId(profileId);
            case STUDENT -> userAccountRepository.existsByStudentId(profileId);
            case PARENT_GUARDIAN -> userAccountRepository.existsByParentGuardianId(profileId);
        };
    }

    private String normalizeUsername(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }

    public enum ProfileType {
        ADMIN(UserRole.ADMIN),
        SUPER_ADMIN(UserRole.SUPER_ADMIN),
        TEACHER(UserRole.TEACHER),
        STUDENT(UserRole.STUDENT),
        PARENT_GUARDIAN(UserRole.PARENT_GUARDIAN);

        private final UserRole role;

        ProfileType(UserRole role) {
            this.role = role;
        }

        public UserRole role() {
            return role;
        }
    }
}
