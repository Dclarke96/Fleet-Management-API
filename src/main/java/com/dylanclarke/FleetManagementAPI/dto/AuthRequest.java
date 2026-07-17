package com.dylanclarke.FleetManagementAPI.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class AuthRequest {

    @Schema(
            description = "Username used for authentication",
            example = "admin"
    )
    @NotBlank(message = "Username cannot be blank")
    private String username;


    @Schema(
            description = "Password associated with the account",
            example = "Password123!"
    )
    @NotBlank(message = "Password cannot be blank")
    private String password;


    // getters/setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}