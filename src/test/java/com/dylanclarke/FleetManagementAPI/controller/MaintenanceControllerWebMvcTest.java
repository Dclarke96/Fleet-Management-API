package com.dylanclarke.FleetManagementAPI.controller;

import com.dylanclarke.FleetManagementAPI.config.SecurityConfig;
import com.dylanclarke.FleetManagementAPI.dto.MaintenanceRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.MaintenanceResponseDTO;
import com.dylanclarke.FleetManagementAPI.exception.GlobalExceptionHandler;
import com.dylanclarke.FleetManagementAPI.exception.ValidationException;
import com.dylanclarke.FleetManagementAPI.exception.ResourceNotFoundException;
import com.dylanclarke.FleetManagementAPI.service.MaintenanceService;

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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(MaintenanceController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class MaintenanceControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaintenanceService maintenanceService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateMaintenance() throws Exception {

        MaintenanceResponseDTO returned = new MaintenanceResponseDTO();
        returned.setId(1L);
        returned.setDescription("Oil change");
        returned.setDate(LocalDate.parse("2026-03-01"));
        returned.setVehicleId(1L);

        when(maintenanceService.addMaintenance(any(MaintenanceRequestDTO.class)))
                .thenReturn(returned);

        String json = """
            {
              "vehicleId": 1,
              "description": "Oil change",
              "date": "2026-03-01",
              "cost": 50.0
            }
        """;

        mockMvc.perform(post("/api/maintenance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Oil change"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllMaintenance() throws Exception {

        MaintenanceResponseDTO m1 = new MaintenanceResponseDTO();
        m1.setId(1L);

        MaintenanceResponseDTO m2 = new MaintenanceResponseDTO();
        m2.setId(2L);

        when(maintenanceService.getAllMaintenance()).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/maintenance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundForMissingMaintenance() throws Exception {

        when(maintenanceService.getMaintenanceById(99L))
                .thenThrow(new ResourceNotFoundException("Maintenance", "id", 99L));

        mockMvc.perform(get("/api/maintenance/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateMaintenance() throws Exception {

        MaintenanceResponseDTO updated = new MaintenanceResponseDTO();
        updated.setId(3L);
        updated.setDescription("Brake pads replaced");
        updated.setDate(LocalDate.parse("2026-03-10"));

        when(maintenanceService.updateMaintenance(eq(3L), any(MaintenanceRequestDTO.class)))
                .thenReturn(updated);

        String json = """
            {
              "vehicleId": 1,
              "description": "Brake pads replaced",
              "date": "2026-03-10",
              "cost": 120.0
            }
        """;

        mockMvc.perform(put("/api/maintenance/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.description").value("Brake pads replaced"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteMaintenance() throws Exception {

        doNothing().when(maintenanceService).deleteMaintenance(4L);

        mockMvc.perform(delete("/api/maintenance/4"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRejectInvalidMaintenanceCreate() throws Exception {

        when(maintenanceService.addMaintenance(any(MaintenanceRequestDTO.class)))
                .thenThrow(new ValidationException("Invalid data", "description", null));

        String badJson = """
            {
              "vehicleId": 1,
              "description": "",
              "date": "2026-03-01",
              "cost": 50.0
            }
        """;

        mockMvc.perform(post("/api/maintenance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}