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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MaintenanceAuthorizationTest {

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

    private Long createMaintenance(
            String token,
            Long vehicleId,
            String description
    ) throws Exception {

        LocalDate serviceDate = LocalDate.now().plusDays(1);

        String json = """
        {
          "vehicleId": %d,
          "description": "%s",
          "date": "%s",
          "cost": 125.50
        }
        """.formatted(vehicleId, description, serviceDate);

        String response = mockMvc.perform(post("/api/maintenance")
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
    // AUTHORIZATION TESTS
    // =========================================================

    @Test
    @DisplayName("Should return only maintenance belonging to authenticated user's company")
    void shouldReturnOnlyUsersCompanyMaintenance() throws Exception {

        // Arrange
        register("alice", "Company A");
        register("bob", "Company B");

        String aliceToken = login("alice");
        String bobToken = login("bob");

        Long aliceVehicle = createVehicle(aliceToken, "Alice Truck");
        Long bobVehicle = createVehicle(bobToken, "Bob Van");

        createMaintenance(aliceToken, aliceVehicle, "Alice Oil Change");
        createMaintenance(bobToken, bobVehicle, "Bob Tire Rotation");

        // Act + Assert
        mockMvc.perform(get("/api/maintenance")
                .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].description")
                        .value("Alice Oil Change"));
    }

    @Test
    @DisplayName("Should not return maintenance belonging to another company")
    void shouldNotReturnMaintenanceFromAnotherCompany() throws Exception {

        // Arrange
        register("alice", "Company A");
        register("bob", "Company B");

        String aliceToken = login("alice");
        String bobToken = login("bob");

        Long bobVehicle = createVehicle(bobToken, "Bob Van");

        Long bobMaintenance =
                createMaintenance(
                        bobToken,
                        bobVehicle,
                        "Bob Tire Rotation");

        // Act + Assert
        mockMvc.perform(get("/api/maintenance/" + bobMaintenance)
                .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should not create maintenance for another company's vehicle")
    void shouldNotCreateMaintenanceForAnotherCompaniesVehicle() throws Exception {

        // Arrange
        register("alice", "Company A");
        register("bob", "Company B");

        String aliceToken = login("alice");
        String bobToken = login("bob");

        Long bobVehicle = createVehicle(bobToken, "Bob Van");

        // Act + Assert
        mockMvc.perform(post("/api/maintenance")
                .header("Authorization", "Bearer " + aliceToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                "vehicleId": %d,
                "description": "Unauthorized Maintenance",
                "date": "%s",
                "cost": 99.99
                }
                """.formatted(
                        bobVehicle,
                        java.time.LocalDate.now().plusDays(1)
                )))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should not update maintenance belonging to another company")
    void shouldNotUpdateMaintenanceFromAnotherCompany() throws Exception {

        // Arrange
        register("alice", "Company A");
        register("bob", "Company B");

        String aliceToken = login("alice");
        String bobToken = login("bob");

        Long bobVehicle = createVehicle(bobToken, "Bob Van");

        Long bobMaintenance =
                createMaintenance(
                        bobToken,
                        bobVehicle,
                        "Bob Tire Rotation");

        // Act + Assert
        mockMvc.perform(put("/api/maintenance/" + bobMaintenance)
                .header("Authorization", "Bearer " + aliceToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                "vehicleId": %d,
                "description": "Unauthorized Update",
                "date": "%s",
                "cost": 500.00
                }
                """.formatted(
                        bobVehicle,
                        java.time.LocalDate.now().plusDays(1)
                )))
                .andExpect(status().isNotFound());
    }
}
