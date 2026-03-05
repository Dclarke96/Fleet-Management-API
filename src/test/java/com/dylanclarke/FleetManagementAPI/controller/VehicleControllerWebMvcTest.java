package com.dylanclarke.FleetManagementAPI.controller;

import com.dylanclarke.FleetManagementAPI.config.SecurityConfig;
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

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        // Mocked Vehicle returned by the service
        Vehicle returnedVehicle = new Vehicle(
                "Test Vehicle",
                "Toyota",
                "Corolla",
                2021,
                "Garage",
                false,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 12, 31)
        );
        returnedVehicle.setId(1L);

        when(vehicleService.addVehicle(any(Vehicle.class))).thenReturn(returnedVehicle);

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
                .andExpect(status().isOk())
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
        Vehicle v1 = new Vehicle("A","B","C",2020,"X",false,LocalDate.now(),LocalDate.now());
        v1.setId(1L);
        Vehicle v2 = new Vehicle("D","E","F",2021,"Y",false,LocalDate.now(),LocalDate.now());
        v2.setId(2L);
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
        Vehicle updated = new Vehicle("U","M","D",2022,"Z",true,LocalDate.now(),LocalDate.now());
        updated.setId(3L);
        when(vehicleService.updateVehicle(eq(3L), any(Vehicle.class))).thenReturn(updated);

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

        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void shouldSearchVehicles() throws Exception {
        Vehicle v = new Vehicle("S","S","S",2023,"L",false,LocalDate.now(),LocalDate.now());
        v.setId(10L);
        when(vehicleService.searchVehicles("test")).thenReturn(List.of(v));

        mockMvc.perform(get("/api/vehicles/search").param("q","test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(10));
    }
}