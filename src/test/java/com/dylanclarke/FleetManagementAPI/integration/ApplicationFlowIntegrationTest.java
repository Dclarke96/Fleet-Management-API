package com.dylanclarke.FleetManagementAPI.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ApplicationFlowIntegrationTest extends BaseIntegrationTest {


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


        // =====================================================
        // Verify vehicle retrieval
        // =====================================================

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


        // =====================================================
        // Verify maintenance retrieval
        // =====================================================

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