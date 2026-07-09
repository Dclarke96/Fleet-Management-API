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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ApplicationFlowIntegrationTest {

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


    private void getVehicle(
            String token,
            Long vehicleId
    ) throws Exception {

        mockMvc.perform(get("/api/vehicles/" + vehicleId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
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


    private void getMaintenance(
            String token,
            Long maintenanceId
    ) throws Exception {

        mockMvc.perform(get("/api/maintenance/" + maintenanceId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }


    private void updateMaintenance(
            String token,
            Long maintenanceId,
            Long vehicleId
    ) throws Exception {

        LocalDate updatedDate = LocalDate.now().plusDays(2);

        String json = """
        {
        "vehicleId": %d,
        "description":"Updated Oil Change",
        "date":"%s",
        "cost":150.00
        }
        """.formatted(
                vehicleId,
                updatedDate
        );


        mockMvc.perform(put("/api/maintenance/" + maintenanceId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
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
    @DisplayName("Should complete full vehicle maintenance workflow")
    void shouldCompleteFullVehicleMaintenanceWorkflow() throws Exception {

        // =====================================================
        // Register and authenticate user
        // =====================================================

        register(
                "alice",
                "Company A"
        );

        String token = login("alice");


        // =====================================================
        // Create vehicle
        // =====================================================

        Long vehicleId = createVehicle(token);


        // Verify vehicle can be retrieved

        getVehicle(
                token,
                vehicleId
        );


        // =====================================================
        // Create maintenance record
        // =====================================================

        Long maintenanceId = createMaintenance(
                token,
                vehicleId
        );


        // Verify maintenance can be retrieved

        getMaintenance(
                token,
                maintenanceId
        );


        // =====================================================
        // Update maintenance record
        // =====================================================

        updateMaintenance(
                token,
                maintenanceId,
                vehicleId
        );


        // =====================================================
        // Delete maintenance record
        // =====================================================

        deleteMaintenance(
                token,
                maintenanceId
        );


        // =====================================================
        // Verify deletion
        // =====================================================

        mockMvc.perform(get("/api/maintenance/" + maintenanceId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}