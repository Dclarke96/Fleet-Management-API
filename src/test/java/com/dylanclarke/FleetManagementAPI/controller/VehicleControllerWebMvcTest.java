package com.dylanclarke.FleetManagementAPI.controller;

import com.dylanclarke.FleetManagementAPI.config.SecurityConfig;
import com.dylanclarke.FleetManagementAPI.exception.ValidationException;
import com.dylanclarke.FleetManagementAPI.model.Vehicle;
import com.dylanclarke.FleetManagementAPI.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VehicleController.class)
@Import(SecurityConfig.class)
@SuppressWarnings({"null","unused"})
class VehicleControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleService vehicleService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser
    @SuppressWarnings("null")
    void shouldCreateVehicle() throws Exception {

        // Mocked response DTO returned by the service
        com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO returnedVehicle = new com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO();
        returnedVehicle.setId(1L);
        returnedVehicle.setTitle("Test Vehicle");
        returnedVehicle.setMake("Toyota");
        returnedVehicle.setModel("Corolla");
        returnedVehicle.setVehicleYear(2021);
        returnedVehicle.setLocation("Garage");
        returnedVehicle.setMaintenanceAlertsEnabled(false);
        returnedVehicle.setStartDate(java.time.LocalDate.parse("2026-03-01"));
        returnedVehicle.setEndDate(java.time.LocalDate.parse("2026-12-31"));

        when(vehicleService.addVehicle(any(com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO.class)))
                .thenReturn(returnedVehicle);

        // JSON payload for POST (no id field)
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())           // check ID exists
                .andExpect(jsonPath("$.title").value("Test Vehicle"))
                .andExpect(jsonPath("$.make").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla"))
                .andExpect(jsonPath("$.vehicleYear").value(2021))
                .andExpect(jsonPath("$.location").value("Garage"))
                .andExpect(jsonPath("$.maintenanceAlertsEnabled").value(false))
                .andExpect(jsonPath("$.startDate").value("2026-03-01"))
                .andExpect(jsonPath("$.endDate").value("2026-12-31"));
    }

    @Test
    @WithMockUser
    void shouldGetAllVehicles() throws Exception {
        com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO v1 = new com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO();
        v1.setId(1L);
        v1.setTitle("A");
        com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO v2 = new com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO();
        v2.setId(2L);
        v2.setTitle("D");
        when(vehicleService.getAllVehicles()).thenReturn(List.of(v1, v2));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundForMissingVehicle() throws Exception {
        when(vehicleService.getVehicleById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/vehicles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldUpdateVehicle() throws Exception {
        com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO updated = new com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO();
        updated.setId(3L);
        updated.setTitle("U");
        updated.setMake("Toyota");
        updated.setModel("Corolla");
        updated.setVehicleYear(2022);
        updated.setLocation("Garage");
        updated.setMaintenanceAlertsEnabled(true);
        updated.setStartDate(java.time.LocalDate.parse("2026-03-01"));
        updated.setEndDate(java.time.LocalDate.parse("2026-12-31"));
        
        when(vehicleService.updateVehicle(eq(3L), any(com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO.class)))
                .thenReturn(updated);

        String json = objectMapper.writeValueAsString(updated);

        mockMvc.perform(put("/api/vehicles/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    @WithMockUser
    void shouldDeleteVehicle() throws Exception {
        doNothing().when(vehicleService).deleteVehicle(4L);

        mockMvc.perform(delete("/api/vehicles/4"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void shouldRejectInvalidDatesOnCreate() throws Exception {
        String badJson = """
            {"title":"Bad","make":"M","model":"D","vehicleYear":2020,"location":"L","maintenanceAlertsEnabled":false,"startDate":"2026-01-02","endDate":"2026-01-01"}
        """;

        when(vehicleService.addVehicle(any(com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO.class)))
                .thenThrow(new ValidationException("End date cannot be before start date", "endDate", null));

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldSearchVehicles() throws Exception {
        com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO v = new com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO();
        v.setId(10L);
        when(vehicleService.searchVehicles("test")).thenReturn(List.of(v));

        mockMvc.perform(get("/api/vehicles/search").param("q","test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(10));
    }
}