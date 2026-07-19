package com.dylanclarke.FleetManagementAPI.integration;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.dylanclarke.FleetManagementAPI.exception.ValidationException;

class DataIntegrityIntegrationTest extends BaseIntegrationTest {

    // =========================================================
    // DATA INTEGRITY TESTS
    // =========================================================

    @Test
    @DisplayName("Should reject maintenance for non-existent vehicle")
    void shouldRejectMaintenanceForNonExistentVehicle() throws Exception {

        register(
                "alice",
                "Company A"
        );

        String token = login("alice");

        LocalDate serviceDate = LocalDate.now().plusDays(1);


        mockMvc.perform(post("/api/maintenance")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "vehicleId":999999,
                          "description":"Oil Change",
                          "date":"%s",
                          "cost":100.00
                        }
                        """.formatted(serviceDate)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error")
                        .value("Resource Not Found"));
    }


    @Test
    @DisplayName("Should reject maintenance with negative cost")
    void shouldRejectMaintenanceWithNegativeCost() throws Exception {

        register(
                "alice",
                "Company A"
        );

        String token = login("alice");

        Long vehicleId = createVehicle(
                token,
                "Company Truck"
        );

        LocalDate serviceDate = LocalDate.now().plusDays(1);


        mockMvc.perform(post("/api/maintenance")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "vehicleId":%d,
                          "description":"Oil Change",
                          "date":"%s",
                          "cost":-50.00
                        }
                        """.formatted(
                                vehicleId,
                                serviceDate
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error")
                        .value("Invalid Request"))
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value("cost"));
    }


    @Test
    @DisplayName("Should reject maintenance without description")
    void shouldRejectMaintenanceWithoutDescription() throws Exception {

        register(
                "alice",
                "Company A"
        );

        String token = login("alice");

        Long vehicleId = createVehicle(
                token,
                "Company Truck"
        );

        LocalDate serviceDate = LocalDate.now().plusDays(1);


        mockMvc.perform(post("/api/maintenance")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "vehicleId":%d,
                          "description":"",
                          "date":"%s",
                          "cost":100.00
                        }
                        """.formatted(
                                vehicleId,
                                serviceDate
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value("description"));
    }


    @Test
    @DisplayName("Should reject maintenance without vehicle ID")
    void shouldRejectMaintenanceWithoutVehicleId() throws Exception {

        register(
                "alice",
                "Company A"
        );

        String token = login("alice");

        LocalDate serviceDate = LocalDate.now().plusDays(1);


        mockMvc.perform(post("/api/maintenance")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "description":"Oil Change",
                          "date":"%s",
                          "cost":100.00
                        }
                        """.formatted(serviceDate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[0].field")
                        .value("vehicleId"));
    }


    @Test
    @DisplayName("Should reject maintenance after vehicle end date")
    void shouldRejectMaintenanceAfterVehicleEndDate() throws Exception {

        register(
                "alice",
                "Company A"
        );

        String token = login("alice");

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(5);

        Long vehicleId = createVehicle(
                token,
                "Company Truck",
                startDate,
                endDate
        );


        mockMvc.perform(post("/api/maintenance")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "vehicleId":%d,
                          "description":"Oil Change",
                          "date":"%s",
                          "cost":100.00
                        }
                        """.formatted(
                                vehicleId,
                                endDate.plusDays(1)
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Maintenance date cannot occur after vehicle end date"));
    }


    @Test
    @DisplayName("Should reject maintenance before vehicle start date")
    void shouldRejectMaintenanceBeforeVehicleStartDate() throws Exception {

        register(
                "alice",
                "Company A"
        );

        String token = login("alice");

        LocalDate startDate = LocalDate.now().plusDays(10);
        LocalDate endDate = startDate.plusYears(1);

        Long vehicleId = createVehicle(
                token,
                "Company Truck",
                startDate,
                endDate
        );


        mockMvc.perform(post("/api/maintenance")
                        .header(
                                "Authorization",
                                "Bearer " + token
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "vehicleId":%d,
                          "description":"Oil Change",
                          "date":"%s",
                          "cost":100.00
                        }
                        """.formatted(
                                vehicleId,
                                startDate.minusDays(1)
                        )))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Maintenance date cannot occur before vehicle start date"));
    }
}