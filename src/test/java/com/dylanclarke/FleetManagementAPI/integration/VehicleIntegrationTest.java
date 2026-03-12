package com.dylanclarke.FleetManagementAPI.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;

@SuppressWarnings("null")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class VehicleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    @SuppressWarnings("null")
    void testFullCRUD() throws Exception {

        // CREATE
        String vehicleJson = """
            {
              "title": "Test Vehicle",
              "make": "Toyota",
              "model": "Corolla",
              "vehicleYear": 2021,
              "location": "Garage",
              "maintenanceAlertsEnabled": false,
              "startDate": "2026-03-01",
              "endDate": "2026-12-31"
            }
        """;

        String createResponse = mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vehicleJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        Number idNumber = JsonPath.read(createResponse, "$.id");
        long id = idNumber.longValue();

        // READ
        mockMvc.perform(get("/api/vehicles/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Toyota"));

        // UPDATE
        String updateJson = """
            {
              "title": "Updated Vehicle",
              "make": "Toyota",
              "model": "Corolla",
              "vehicleYear": 2022,
              "location": "Garage",
              "maintenanceAlertsEnabled": true,
              "startDate": "2026-03-01",
              "endDate": "2026-12-31"
            }
        """;

        mockMvc.perform(put("/api/vehicles/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleYear").value(2022))
                .andExpect(jsonPath("$.maintenanceAlertsEnabled").value(true));

        // DELETE
        mockMvc.perform(delete("/api/vehicles/" + id))
                .andExpect(status().isNoContent());

        // VERIFY DELETION
        mockMvc.perform(get("/api/vehicles/" + id))
                .andExpect(status().isNotFound());
    }

    // -------------------------------
    // Validation failure tests
    // -------------------------------
    @Test
    @WithMockUser
    void shouldFailValidationWhenTitleBlank() throws Exception {

        String invalidJson = """
            {
              "title": "",
              "make": "Toyota",
              "model": "Corolla",
              "vehicleYear": 2021,
              "location": "Garage",
              "maintenanceAlertsEnabled": false,
              "startDate": "2026-03-01",
              "endDate": "2026-12-31"
            }
        """;

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldFailValidationWhenYearInvalid() throws Exception {

        String invalidJson = """
            {
              "title": "Bad Year Vehicle",
              "make": "Ford",
              "model": "F-150",
              "vehicleYear": 1800,
              "location": "Garage",
              "maintenanceAlertsEnabled": false,
              "startDate": "2026-03-01",
              "endDate": "2026-12-31"
            }
        """;

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldFailValidationWhenEndDateBeforeStartDate() throws Exception {

        String invalidJson = """
            {
              "title": "Bad Dates",
              "make": "Honda",
              "model": "Civic",
              "vehicleYear": 2022,
              "location": "Garage",
              "maintenanceAlertsEnabled": false,
              "startDate": "2026-12-31",
              "endDate": "2026-03-01"
            }
        """;

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}