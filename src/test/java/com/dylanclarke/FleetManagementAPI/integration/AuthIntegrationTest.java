package com.dylanclarke.FleetManagementAPI.integration;

import com.dylanclarke.FleetManagementAPI.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;


    // =========================================================
    // REGISTER TESTS
    // =========================================================

    @Test
    @DisplayName("Should register a new user")
    void shouldRegisterUserSuccessfully() throws Exception {

        register(
                "user1",
                "TestCo"
        );

        Assertions.assertTrue(
                userRepository.findAll()
                        .stream()
                        .anyMatch(user ->
                                user.getUsername()
                                        .equals("user1"))
        );
    }


    @Test
    @DisplayName("Should reject duplicate username")
    void shouldRejectDuplicateUsername() throws Exception {

        register(
                "duplicate",
                "TestCo"
        );


        String json = """
        {
          "username": "duplicate",
          "password": "password",
          "companyName": "TestCo"
        }
        """;


        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }


    // =========================================================
    // LOGIN TESTS
    // =========================================================

    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() throws Exception {

        register(
                "loginuser",
                "TestCo"
        );


        String token = login("loginuser");


        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());
    }


    @Test
    @DisplayName("Should reject invalid password")
    void shouldRejectInvalidPassword() throws Exception {

        register(
                "badpass",
                "TestCo"
        );


        String json = """
        {
          "username": "badpass",
          "password": "wrongpassword"
        }
        """;


        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("Should reject unknown user")
    void shouldRejectUnknownUser() throws Exception {

        String json = """
        {
          "username": "doesnotexist",
          "password": "password"
        }
        """;


        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isUnauthorized());
    }


    // =========================================================
    // AUTHENTICATION TESTS
    // =========================================================

    @Test
    @DisplayName("Should allow authenticated request")
    void shouldAllowAuthenticatedRequest() throws Exception {

        register(
                "authuser",
                "TestCo"
        );


        String token = login("authuser");


        mockMvc.perform(get("/api/vehicles")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isOk());
    }


    @Test
    @DisplayName("Should reject invalid JWT")
    void shouldRejectInvalidJwt() throws Exception {

        mockMvc.perform(get("/api/vehicles")
                        .header(
                                "Authorization",
                                "Bearer invalidtoken"
                        ))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("Should reject expired JWT")
    void shouldRejectExpiredJwt() throws Exception {

        String fakeExpiredToken =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.expired.signature";


        mockMvc.perform(get("/api/vehicles")
                        .header(
                                "Authorization",
                                "Bearer " + fakeExpiredToken
                        ))
                .andExpect(status().isUnauthorized());
    }
}