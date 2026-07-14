package com.dylanclarke.FleetManagementAPI.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public CurrentUser get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        return new CurrentUser(
            user.getId(),
            user.getCompanyId(),
            user.getUsername(),
            user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
        );
    }

    public Long getCompanyId() {
        return get().getCompanyId();
    }

    public Long getUserId() {
        return get().getUserId();
    }
}