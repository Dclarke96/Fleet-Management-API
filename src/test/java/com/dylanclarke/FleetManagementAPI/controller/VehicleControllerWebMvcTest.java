package com.dylanclarke.FleetManagementAPI.controller;

import com.dylanclarke.FleetManagementAPI.config.SecurityConfig;
import com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO;
import com.dylanclarke.FleetManagementAPI.exception.ValidationException;
import com.dylanclarke.FleetManagementAPI.exception.ResourceNotFoundException;
import com.dylanclarke.FleetManagementAPI.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    // ---------------- CREATE ----------------

    @Test
    @WithMockUser
    void shouldCreateVehicle() throws Exception {

        VehicleResponseDTO returnedVehicle = new VehicleResponseDTO();
        returnedVehicle.setId(1L);
        returnedVehicle.setTitle("Test Vehicle");
        returnedVehicle.setMake("Toyota");
        returnedVehicle.setModel("Corolla");
        returnedVehicle.setVehicleYear(2021);
        returnedVehicle.setLocation("Garage");
        returnedVehicle.setMaintenanceAlertsEnabled(false);
        returnedVehicle.setStartDate(LocalDate.parse("2026-05-01"));
        returnedVehicle.setEndDate(LocalDate.parse("2026-12-31"));

        when(vehicleService.addVehicle(any(VehicleRequestDTO.class)))
                .thenReturn(returnedVehicle);

        String vehicleJson = """
            {
              "title": "Test Vehicle",
              "make": "Toyota",
              "model": "Corolla",
              "vehicleYear": 2021,
              "location": "Garage",
              "maintenanceAlertsEnabled": false,
              "startDate": "2026-05-01",
              "endDate": "2026-12-31"
            }
        """;

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vehicleJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title").value("Test Vehicle"));
    }

    // ---------------- GET ALL (PAGINATION FIX) ----------------

    @Test
    @WithMockUser
    void shouldGetAllVehicles() throws Exception {

        VehicleResponseDTO v1 = new VehicleResponseDTO();
        v1.setId(1L);

        VehicleResponseDTO v2 = new VehicleResponseDTO();
        v2.setId(2L);

        Page<VehicleResponseDTO> page = new PageImpl<>(List.of(v1, v2));

        when(vehicleService.getAllVehicles(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/vehicles")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(2)))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    // ---------------- GET BY ID ----------------

    @Test
    @WithMockUser
    void shouldReturnNotFoundForMissingVehicle() throws Exception {

        when(vehicleService.getVehicleById(99L))
                .thenThrow(new ResourceNotFoundException("Vehicle", "id", 99L));

        mockMvc.perform(get("/api/vehicles/99"))
                .andExpect(status().isNotFound());
    }

    // ---------------- UPDATE ----------------

    @Test
    @WithMockUser
    void shouldUpdateVehicle() throws Exception {

        VehicleResponseDTO updated = new VehicleResponseDTO();
        updated.setId(3L);
        updated.setTitle("Updated Vehicle");

        when(vehicleService.updateVehicle(eq(3L), any(VehicleRequestDTO.class)))
                .thenReturn(updated);

        String json = """
            {
              "title": "Updated Vehicle",
              "make": "Toyota",
              "model": "Corolla",
              "vehicleYear": 2022,
              "location": "Garage",
              "maintenanceAlertsEnabled": true,
              "startDate": "2026-05-01",
              "endDate": "2026-12-31"
            }
        """;

        mockMvc.perform(put("/api/vehicles/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(3));
    }

    // ---------------- DELETE ----------------

    @Test
    @WithMockUser
    void shouldDeleteVehicle() throws Exception {

        doNothing().when(vehicleService).deleteVehicle(4L);

        mockMvc.perform(delete("/api/vehicles/4"))
                .andExpect(status().isNoContent());
    }

    // ---------------- VALIDATION ----------------

    @Test
    @WithMockUser
    void shouldRejectInvalidDatesOnCreate() throws Exception {

        String badJson = """
            {
              "title":"Bad",
              "make":"M",
              "model":"D",
              "year":2020,
              "location":"L",
              "maintenanceAlertsEnabled":false,
              "startDate":"2026-01-02",
              "endDate":"2026-01-01"
            }
        """;

        when(vehicleService.addVehicle(any(VehicleRequestDTO.class)))
                .thenThrow(new ValidationException("End date cannot be before start date", "endDate", null));

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }

    // ---------------- SEARCH ----------------

    @Test
    @WithMockUser
    void shouldSearchVehicles() throws Exception {

        VehicleResponseDTO v = new VehicleResponseDTO();
        v.setId(10L);

        Page<VehicleResponseDTO> page = new PageImpl<>(List.of(v));

        when(vehicleService.searchVehicles(eq("test"), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/vehicles/search")
                        .param("q","test")
                        .param("page","0")
                        .param("size","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].id").value(10));
    }
}