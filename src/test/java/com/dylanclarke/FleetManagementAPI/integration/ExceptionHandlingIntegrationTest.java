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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ExceptionHandlingIntegrationTest {

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

    // =========================================================
    // VEHICLE HELPERS
    // =========================================================

    private Long createVehicle(
            String token
    ) throws Exception {

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusYears(1);

        String json = """
        {
        "title":"Fleet Truck",
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
    // MAINTENANCE HELPERS
    // =========================================================

    private Long createMaintenance(
            String token,
            Long vehicleId
    ) throws Exception {

        LocalDate serviceDate = LocalDate.now().plusDays(1);

        String json = """
        {
        "vehicleId": %d,
        "description":"Oil Change",
        "date":"%s",
        "cost":125.50
        }
        """.formatted(
                vehicleId,
                serviceDate
        );


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

    private void deleteMaintenance(
            String token,
            Long maintenanceId
    ) throws Exception {

        mockMvc.perform(delete("/api/maintenance/" + maintenanceId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return standardized error response for missing resource")
    void shouldReturn404WhenResourceDoesNotExist() throws Exception {

        register("exceptionUser", "Test Company");

        String token = login("exceptionUser");

        Long vehicleId = createVehicle(token);

        Long maintenanceId = createMaintenance(token, vehicleId);

        deleteMaintenance(token, maintenanceId);

        mockMvc.perform(get("/api/maintenance/" + maintenanceId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.traceId").exists());
    }

    @Test
    @DisplayName("Should return standardized error response when authentication is missing")
    void shouldReturn401WhenAuthenticationIsMissing() throws Exception {

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid authentication credentials"))
                .andExpect(jsonPath("$.path").exists())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.traceId").exists());
    }
}