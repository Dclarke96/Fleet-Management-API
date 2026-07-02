package com.dylanclarke.FleetManagementAPI.security;

import java.util.List;

public class CurrentUser {

    private Long userId;
    private Long companyId;
    private String email;
    private List<String> roles;

    public CurrentUser(Long userId, Long companyId, String email, List<String> roles) {
        this.userId = userId;
        this.companyId = companyId;
        this.email = email;
        this.roles = roles;
    }

    public Long getUserId() { return userId; }
    public Long getCompanyId() { return companyId; }
    public String getEmail() { return email; }
    public List<String> getRoles() { return roles; }
}