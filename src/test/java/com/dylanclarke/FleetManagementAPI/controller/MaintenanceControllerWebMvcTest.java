package com.dylanclarke.FleetManagementAPI.controller;

import com.dylanclarke.FleetManagementAPI.config.SecurityConfig;
import com.dylanclarke.FleetManagementAPI.exception.ValidationException;
import com.dylanclarke.FleetManagementAPI.service.MaintenanceService;
import com.dylanclarke.FleetManagementAPI.dto.MaintenanceRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.MaintenanceResponseDTO;
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

@WebMvcTest(MaintenanceController.class)
@Import(SecurityConfig.class)
class MaintenanceControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaintenanceService maintenanceService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser
    void shouldCreateMaintenance() throws Exception {
        MaintenanceResponseDTO returned = new MaintenanceResponseDTO();
        returned.setId(1L);
        returned.setVehicleId(1L);
        returned.setDescription("Oil change");
        returned.setDate(LocalDate.parse("2026-03-01"));
        returned.setCost(49.99);

        when(maintenanceService.addMaintenance(any(MaintenanceRequestDTO.class)))
                .thenReturn(returned);

        String json = """
            {
              "vehicleId":1,
              "description":"Oil change",
              "date":"2026-03-01",
              "cost":49.99
            }
        """;

        mockMvc.perform(post("/api/maintenance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.vehicleId").value(1))
                .andExpect(jsonPath("$.description").value("Oil change"));
    }

    @Test
    @WithMockUser
    void shouldGetAllMaintenance() throws Exception {
        MaintenanceResponseDTO m1 = new MaintenanceResponseDTO();
        m1.setId(1L);
        m1.setVehicleId(1L);
        m1.setDescription("Oil change");
        MaintenanceResponseDTO m2 = new MaintenanceResponseDTO();
        m2.setId(2L);
        m2.setVehicleId(2L);
        m2.setDescription("Tire rotation");

        when(maintenanceService.getAllMaintenance()).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/maintenance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser
    void shouldReturnNotFoundForMissingMaintenance() throws Exception {
        when(maintenanceService.getMaintenanceById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/maintenance/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void shouldUpdateMaintenance() throws Exception {
        MaintenanceResponseDTO updated = new MaintenanceResponseDTO();
        updated.setId(3L);
        updated.setVehicleId(1L);
        updated.setDescription("Brake pads replaced");
        updated.setDate(LocalDate.parse("2026-03-10"));
        updated.setCost(150.0);

        when(maintenanceService.updateMaintenance(eq(3L), any(MaintenanceRequestDTO.class)))
                .thenReturn(updated);

        String json = objectMapper.writeValueAsString(updated);

        mockMvc.perform(put("/api/maintenance/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.description").value("Brake pads replaced"));
    }

    @Test
    @WithMockUser
    void shouldDeleteMaintenance() throws Exception {
        doNothing().when(maintenanceService).deleteMaintenance(4L);

        mockMvc.perform(delete("/api/maintenance/4"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void shouldRejectInvalidMaintenanceCreate() throws Exception {
        String badJson = """
            {"vehicleId":1,"description":"","date":"2026-03-01","cost":-10}
        """;

        when(maintenanceService.addMaintenance(any(MaintenanceRequestDTO.class)))
                .thenThrow(new ValidationException("Description required or cost invalid", "description", null));

        mockMvc.perform(post("/api/maintenance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }
}