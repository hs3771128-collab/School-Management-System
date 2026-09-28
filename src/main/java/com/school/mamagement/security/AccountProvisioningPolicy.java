package com.school.mamagement.security;

import com.school.mamagement.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AccountProvisioningPolicy {

    public boolean canCreate(Authentication authentication, UserRole requestedRole) {
        if (authentication == null || !authentication.isAuthenticated() || requestedRole == null) {
            return false;
        }

        if (hasRole(authentication, UserRole.SUPER_ADMIN)) {
            return true;
        }

        return hasRole(authentication, UserRole.ADMIN)
                && switch (requestedRole) {
            case TEACHER, STUDENT, PARENT_GUARDIAN -> true;
            case ADMIN, SUPER_ADMIN -> false;
        };
    }

    private boolean hasRole(Authentication authentication, UserRole role) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> ("ROLE_" + role.name()).equals(a.getAuthority()));
    }
}
