package com.school.mamagement.service;

import com.school.mamagement.entity.UserAccount;
import com.school.mamagement.enums.AccountStatus;
import com.school.mamagement.enums.StudentStatus;
import com.school.mamagement.enums.TeacherStatus;
import com.school.mamagement.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAccountSyncService {
    private final UserAccountRepository repository;

    public void syncAdminStatus(Long profileId, AccountStatus status) {
        repository.findByAdminId(profileId).ifPresent(a -> a.setStatus(status));
    }

    public void syncSuperAdminStatus(Long profileId, AccountStatus status) {
        repository.findBySuperAdminId(profileId).ifPresent(a -> a.setStatus(status));
    }

    public void syncTeacherStatus(Long profileId, TeacherStatus status) {
        AccountStatus mapped = switch (status) {
            case ACTIVE -> AccountStatus.ACTIVE;
            case INACTIVE, TERMINATED -> AccountStatus.INACTIVE;
            case ON_LEAVE -> AccountStatus.SUSPENDED;
        };
        repository.findByTeacherId(profileId).ifPresent(a -> a.setStatus(mapped));
    }

    public void syncStudentStatus(Long profileId, StudentStatus status) {
        AccountStatus mapped = switch (status) {
            case ACTIVE -> AccountStatus.ACTIVE;
            case SUSPENDED -> AccountStatus.SUSPENDED;
            case INACTIVE, GRADUATED, TRANSFERRED -> AccountStatus.INACTIVE;
        };
        repository.findByStudentId(profileId).ifPresent(a -> a.setStatus(mapped));
    }

    public Optional<UserAccount> findAdminAccount(Long profileId) {
        return repository.findByAdminId(profileId);
    }

    public Optional<UserAccount> findSuperAdminAccount(Long profileId) {
        return repository.findBySuperAdminId(profileId);
    }

    public Optional<UserAccount> findTeacherAccount(Long profileId) {
        return repository.findByTeacherId(profileId);
    }

    public Optional<UserAccount> findStudentAccount(Long profileId) {
        return repository.findByStudentId(profileId);
    }
}
