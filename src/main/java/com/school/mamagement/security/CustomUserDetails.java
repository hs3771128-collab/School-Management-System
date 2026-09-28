package com.school.mamagement.security;

import com.school.mamagement.entity.UserAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final UserAccount account;

    public UserAccount getAccount() { return account; }
    public Long getAccountId() { return account.getId(); }
    public String getRole() { return account.getRole().name(); }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()));
    }
    @Override public String getPassword() { return account.getPasswordHash(); }
    @Override public String getUsername() { return account.getUsername(); }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return account.getLockedUntil() == null || account.getLockedUntil().isBefore(java.time.LocalDateTime.now()); }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return account.getStatus() == com.school.mamagement.enums.AccountStatus.ACTIVE; }
}
