package com.SmartHealthRemoteSystem.SHSR.Analytics.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Utility bean for programmatic ADMIN-only access checks within the Analytics module.
 * Complements the @PreAuthorize annotation on AnalyticsController.
 */
@Component("adminAccessValidator")
public class AdminAccessValidator {

    public boolean isAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    public boolean canAccessAnalytics(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return false;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())
                            || "ROLE_RADIOLOGIST".equals(a.getAuthority()));
    }
}