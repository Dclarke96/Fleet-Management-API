package com.dylanclarke.FleetManagementAPI.integration;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DataIntegrityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================================================
    // AUTH HELPERS
    // =========================================================

    private void register(String username, String companyName) throws Exception {

        String json = """
        {
          "username": "%s",
          "password": "password",
          "companyName": "%s"
        }
        """.formatted(username, companyName);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    private String login(String username) throws Exception {

        String json = """
        {
          "username": "%s",
          "password": "password"
        }
        """.formatted(username);

        String response = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);

        return node.get("data").asText();
    }

    private Long createVehicle(String token, String title) throws Exception {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);

        String json = """
        {
        "title":"%s",
        "vin":"VIN123",
        "licensePlate":"ABC123",
        "make":"Ford",
        "model":"F150",
        "vehicleYear":2024,
        "location":"Yard",
        "maintenanceAlertsEnabled":true,
        "startDate":"%s",
        "endDate":"%s"
        }
        """.formatted(
                title,
                startDate,
                endDate
        );

        String response = mockMvc.perform(post("/api/vehicles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode node = objectMapper.readTree(response);

        return node.get("data")
                .get("id")
                .asLong();
    }

    // =========================================================
    // DATA INTEGRITY TESTS
    // =========================================================

    @Test
    @DisplayName("Should reject maintenance for non-existent vehicle")
    void shouldRejectMaintenanceForNonExistentVehicle() throws Exception {

        // Arrange
        register("alice", "Company A");

        String token = login("alice");

        LocalDate serviceDate = LocalDate.now().plusDays(1);

        // Act + Assert
        mockMvc.perform(post("/api/maintenance")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                  "vehicleId": 999999,
                  "description": "Oil Change",
                  "date": "%s",
                  "cost": 100.00
                }
                """.formatted(serviceDate)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    @DisplayName("Should reject maintenance with negative cost")
    void shouldRejectMaintenanceWithNegativeCost() throws Exception {

        // Arrange
        register("alice", "Company A");

        String token = login("alice");

        Long vehicleId = createVehicle(
                token,
                "Company Truck");

        LocalDate serviceDate = LocalDate.now().plusDays(1);

        // Act + Assert
        mockMvc.perform(post("/api/maintenance")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                "vehicleId": %d,
                "description": "Oil Change",
                "date": "%s",
                "cost": -50.00
                }
                """.formatted(vehicleId, serviceDate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Invalid Request"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors[0].field").value("cost"));
    }
}
