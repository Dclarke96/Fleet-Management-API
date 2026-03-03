package com.dylanclarke.FleetManagementAPI.controller;

import com.dylanclarke.FleetManagementAPI.repository.VehicleRepository;
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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository repository;

    @Test
    @WithMockUser
    void testFullCRUD() throws Exception {

        // -------------------------------
        // CREATE
        // -------------------------------
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

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vehicleJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());

        long id = repository.findAll().get(0).getId();

        // -------------------------------
        // READ
        // -------------------------------
        mockMvc.perform(get("/api/vehicles/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Toyota"));

        // -------------------------------
        // UPDATE
        // -------------------------------
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

        // -------------------------------
        // DELETE
        // -------------------------------
        mockMvc.perform(delete("/api/vehicles/" + id))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/vehicles/" + id))
                .andExpect(status().isNotFound());
    }
}