package com.dylanclarke.FleetManagementAPI.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.dylanclarke.FleetManagementAPI.model.User;

@Service
public class CurrentUserService {

    public String getUsername() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        return user.getUsername();
    }
}
