package com.dylanclarke.FleetManagementAPI.integration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dylanclarke.FleetManagementAPI.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =========================================================
    // 🔧 HELPER METHODS (NO ASSERTIONS INSIDE)
    // =========================================================

    private ResultActions registerRaw(String username) throws Exception {
        String json = """
        {
          "username": "%s",
          "password": "password",
          "companyName": "TestCo"
        }
        """.formatted(username);

        return mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private ResultActions loginRaw(String username) throws Exception {
        String json = """
        {
          "username": "%s",
          "password": "password"
        }
        """.formatted(username);

        return mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));
    }

    private String loginAndGetToken(String username) throws Exception {
        String response = loginRaw(username)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        return node.get("data").asText();
    }

    // =========================================================
    // REGISTER TESTS
    // =========================================================

    @Test
    @DisplayName("Should register a new user")
    void shouldRegisterUserSuccessfully() throws Exception {

        registerRaw("user1")
                .andDo(print())
                .andExpect(status().isCreated());

        Assertions.assertTrue(
                userRepository.findAll()
                        .stream()
                        .anyMatch(u -> u.getUsername().equals("user1"))
        );
    }

    @Test
    @DisplayName("Should reject duplicate username")
    void shouldRejectDuplicateUsername() throws Exception {

        registerRaw("duplicate")
                .andExpect(status().isCreated());

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

        registerRaw("loginuser")
                .andExpect(status().isCreated());

        String token = loginAndGetToken("loginuser");

        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Should reject invalid password")
    void shouldRejectInvalidPassword() throws Exception {

        registerRaw("badpass")
                .andExpect(status().isCreated());

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
    // AUTH TESTS
    // =========================================================

    @Test
    @DisplayName("Should allow authenticated request")
    void shouldAllowAuthenticatedRequest() throws Exception {

        registerRaw("authuser")
                .andExpect(status().isCreated());

        String token = loginAndGetToken("authuser");

        mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should reject invalid JWT")
    void shouldRejectInvalidJwt() throws Exception {

        mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer invalidtoken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should reject expired JWT")
    void shouldRejectExpiredJwt() throws Exception {

        String fakeExpiredToken =
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.expired.signature";

        mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer " + fakeExpiredToken))
                .andExpect(status().isUnauthorized());
    }
}