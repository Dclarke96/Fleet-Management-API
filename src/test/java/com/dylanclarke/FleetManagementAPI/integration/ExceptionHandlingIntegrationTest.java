package com.dylanclarke.FleetManagementAPI.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ExceptionHandlingIntegrationTest extends BaseIntegrationTest {


    @Test
    @DisplayName("Should return standardized error response for missing resource")
    void shouldReturn404WhenResourceDoesNotExist() throws Exception {

        register(
                "exceptionUser",
                "Test Company"
        );

        String token = login("exceptionUser");


        Long vehicleId = createVehicle(token);


        Long maintenanceId = createMaintenance(
                token,
                vehicleId
        );


        deleteMaintenance(
                token,
                maintenanceId
        );


        mockMvc.perform(get("/api/maintenance/" + maintenanceId)
                        .header(
                                "Authorization",
                                "Bearer " + token
                        ))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status")
                        .value(404))
                .andExpect(jsonPath("$.error")
                        .value("Resource Not Found"))
                .andExpect(jsonPath("$.message")
                        .exists())
                .andExpect(jsonPath("$.path")
                        .exists())
                .andExpect(jsonPath("$.timestamp")
                        .exists())
                .andExpect(jsonPath("$.traceId")
                        .exists());
    }


    @Test
    @DisplayName("Should return standardized error response when authentication is missing")
    void shouldReturn401WhenAuthenticationIsMissing() throws Exception {

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status")
                        .value(401))
                .andExpect(jsonPath("$.error")
                        .value("Unauthorized"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid authentication credentials"))
                .andExpect(jsonPath("$.path")
                        .exists())
                .andExpect(jsonPath("$.timestamp")
                        .exists())
                .andExpect(jsonPath("$.traceId")
                        .exists());
    }
}