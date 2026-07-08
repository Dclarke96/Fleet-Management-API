package com.dylanclarke.FleetManagementAPI.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class VehicleAuthorizationTest {

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
          "startDate":"2026-01-01",
          "endDate":"2026-12-31"
        }
        """.formatted(title);

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


    private void updateVehicle(String token, Long vehicleId) throws Exception {

        String json = """
        {
          "title":"Updated Vehicle",
          "vin":"UPDATEDVIN",
          "licensePlate":"UPDATED",
          "make":"Toyota",
          "model":"Tacoma",
          "vehicleYear":2025,
          "location":"Updated Yard",
          "maintenanceAlertsEnabled":false,
          "startDate":"2026-01-01",
          "endDate":"2026-12-31"
        }
        """;

        mockMvc.perform(put("/api/vehicles/" + vehicleId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());
    }


    // =========================================================
    // AUTHORIZATION TESTS
    // =========================================================

    @Test
    @DisplayName("Should return only vehicles belonging to authenticated user's company")
    void shouldReturnOnlyUsersCompanyVehicles() throws Exception {

        register("alice", "Company A");
        register("bob", "Company B");

        String aliceToken = login("alice");
        String bobToken = login("bob");

        createVehicle(aliceToken, "Alice Truck");
        createVehicle(bobToken, "Bob Van");


        mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("Alice Truck"))
                .andExpect(jsonPath("$.data.content[0].make").value("Ford"));
    }


    @Test
    @DisplayName("Should not return vehicle belonging to another company")
    void shouldNotReturnVehicleFromAnotherCompany() throws Exception {

        register("alice", "Company A");
        register("bob", "Company B");

        String aliceToken = login("alice");
        String bobToken = login("bob");

        createVehicle(aliceToken, "Alice Truck");

        Long bobVehicleId = createVehicle(bobToken, "Bob Van");


        mockMvc.perform(get("/api/vehicles/" + bobVehicleId)
                .header("Authorization", "Bearer " + aliceToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Should not update vehicle belonging to another company")
    void shouldNotUpdateVehicleFromAnotherCompany() throws Exception {

        // Arrange
        register("alice", "Company A");
        register("bob", "Company B");

        String aliceToken = login("alice");
        String bobToken = login("bob");

        Long bobVehicleId = createVehicle(bobToken, "Bob Van");


        // Act + Assert
        mockMvc.perform(put("/api/vehicles/" + bobVehicleId)
                .header("Authorization", "Bearer " + aliceToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                  "title":"Unauthorized Update",
                  "vin":"BADVIN",
                  "licensePlate":"BAD123",
                  "make":"Tesla",
                  "model":"Cybertruck",
                  "vehicleYear":2026,
                  "location":"Hack Attempt",
                  "maintenanceAlertsEnabled":true,
                  "startDate":"2026-01-01",
                  "endDate":"2026-12-31"
                }
                """))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should not delete vehicle belonging to another company")
    void shouldNotDeleteVehicleFromAnotherCompany() throws Exception {

        // Arrange
        register("alice", "Company A");
        register("bob", "Company B");

        String aliceToken = login("alice");
        String bobToken = login("bob");

        Long bobVehicleId = createVehicle(bobToken, "Bob Van");


        // Act + Assert
        mockMvc.perform(delete("/api/vehicles/" + bobVehicleId)
                .header("Authorization", "Bearer " + aliceToken))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should create vehicle belonging to authenticated user's company")
    void shouldCreateVehicleForAuthenticatedUsersCompany() throws Exception {

        // Arrange
        register("alice", "Company A");
        register("bob", "Company B");

        String aliceToken = login("alice");
        String bobToken = login("bob");


        // Act
        createVehicle(aliceToken, "Alice Truck");


        // Assert - Alice should see her vehicle
        mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer " + aliceToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("Alice Truck"));


        // Assert - Bob should not see Alice's vehicle
        mockMvc.perform(get("/api/vehicles")
                .header("Authorization", "Bearer " + bobToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }
}