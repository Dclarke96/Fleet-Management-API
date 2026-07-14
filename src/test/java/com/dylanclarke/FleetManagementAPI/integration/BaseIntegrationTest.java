package com.dylanclarke.FleetManagementAPI.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Base class for all integration tests.
 *
 * Provides:
 * - Shared Spring Boot test configuration
 * - Common authentication helpers
 * - Common vehicle helpers
 * - Common maintenance helpers
 *
 * Individual integration tests should extend this class and focus only
 * on the behavior being tested.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    // =========================================================
    // AUTH HELPERS
    // =========================================================

    protected void register(
            String username,
            String companyName
    ) throws Exception {

        String json = """
        {
          "username":"%s",
          "password":"password",
          "companyName":"%s"
        }
        """.formatted(
                username,
                companyName
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    protected String login(
            String username
    ) throws Exception {

        String json = """
        {
          "username":"%s",
          "password":"password"
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

    protected Long createVehicle(
            String token
    ) throws Exception {

        return createVehicle(
                token,
                "Fleet Truck",
                LocalDate.now(),
                LocalDate.now().plusYears(1)
        );
    }


    protected Long createVehicle(
            String token,
            String title
    ) throws Exception {

        return createVehicle(
                token,
                title,
                LocalDate.now(),
                LocalDate.now().plusYears(1)
        );
    }


    protected Long createVehicle(
            String token,
            String title,
            LocalDate startDate,
            LocalDate endDate
    ) throws Exception {

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


    protected void getVehicle(
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

    protected Long createMaintenance(
            String token,
            Long vehicleId
    ) throws Exception {

        LocalDate serviceDate = LocalDate.now().plusDays(1);

        String json = """
        {
          "vehicleId":%d,
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

    protected void getMaintenance(
            String token,
            Long maintenanceId
    ) throws Exception {

        mockMvc.perform(get("/api/maintenance/" + maintenanceId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    protected void updateMaintenance(
            String token,
            Long maintenanceId,
            Long vehicleId
    ) throws Exception {

        LocalDate updatedDate = LocalDate.now().plusDays(2);

        String json = """
        {
          "vehicleId":%d,
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

    protected void deleteMaintenance(
            String token,
            Long maintenanceId
    ) throws Exception {

        mockMvc.perform(delete("/api/maintenance/" + maintenanceId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}