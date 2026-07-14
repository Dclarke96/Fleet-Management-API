package com.dylanclarke.FleetManagementAPI.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import org.springframework.transaction.annotation.Transactional;


@Transactional
class MaintenanceAuthorizationTest extends BaseIntegrationTest {


    // =========================================================
    // AUTHORIZATION TESTS
    // =========================================================


    @Test
    @DisplayName("Should return only maintenance belonging to authenticated user's company")
    void shouldReturnOnlyUsersCompanyMaintenance() throws Exception {


        register(
                "alice",
                "Company A"
        );

        register(
                "bob",
                "Company B"
        );


        String aliceToken = login("alice");
        String bobToken = login("bob");


        Long aliceVehicle =
                createVehicle(
                        aliceToken,
                        "Alice Truck"
                );


        Long bobVehicle =
                createVehicle(
                        bobToken,
                        "Bob Van"
                );


        createMaintenance(
                aliceToken,
                aliceVehicle
        );


        createMaintenance(
                bobToken,
                bobVehicle
        );


        mockMvc.perform(get("/api/maintenance")
                .header(
                        "Authorization",
                        "Bearer " + aliceToken
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }


    @Test
    @DisplayName("Should not return maintenance belonging to another company")
    void shouldNotReturnMaintenanceFromAnotherCompany() throws Exception {


        register(
                "alice",
                "Company A"
        );

        register(
                "bob",
                "Company B"
        );


        String aliceToken = login("alice");
        String bobToken = login("bob");


        Long bobVehicle =
                createVehicle(
                        bobToken,
                        "Bob Van"
                );


        Long bobMaintenance =
                createMaintenance(
                        bobToken,
                        bobVehicle
                );


        mockMvc.perform(get("/api/maintenance/" + bobMaintenance)
                .header(
                        "Authorization",
                        "Bearer " + aliceToken
                ))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Should not create maintenance for another company's vehicle")
    void shouldNotCreateMaintenanceForAnotherCompaniesVehicle() throws Exception {


        register(
                "alice",
                "Company A"
        );

        register(
                "bob",
                "Company B"
        );


        String aliceToken = login("alice");
        String bobToken = login("bob");


        Long bobVehicle =
                createVehicle(
                        bobToken,
                        "Bob Van"
                );


        mockMvc.perform(post("/api/maintenance")
                .header(
                        "Authorization",
                        "Bearer " + aliceToken
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "vehicleId": %d,
                    "description": "Unauthorized Maintenance",
                    "date": "%s",
                    "cost": 99.99
                }
                """.formatted(
                        bobVehicle,
                        LocalDate.now().plusDays(1)
                )))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Should not update maintenance belonging to another company")
    void shouldNotUpdateMaintenanceFromAnotherCompany() throws Exception {


        register(
                "alice",
                "Company A"
        );

        register(
                "bob",
                "Company B"
        );


        String aliceToken = login("alice");
        String bobToken = login("bob");


        Long bobVehicle =
                createVehicle(
                        bobToken,
                        "Bob Van"
                );


        Long bobMaintenance =
                createMaintenance(
                        bobToken,
                        bobVehicle
                );


        mockMvc.perform(put("/api/maintenance/" + bobMaintenance)
                .header(
                        "Authorization",
                        "Bearer " + aliceToken
                )
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "vehicleId": %d,
                    "description": "Unauthorized Update",
                    "date": "%s",
                    "cost": 500.00
                }
                """.formatted(
                        bobVehicle,
                        LocalDate.now().plusDays(1)
                )))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Should not delete maintenance belonging to another company")
    void shouldNotDeleteMaintenanceFromAnotherCompany() throws Exception {


        register(
                "alice",
                "Company A"
        );

        register(
                "bob",
                "Company B"
        );


        String aliceToken = login("alice");
        String bobToken = login("bob");


        Long bobVehicle =
                createVehicle(
                        bobToken,
                        "Bob Van"
                );


        Long bobMaintenance =
                createMaintenance(
                        bobToken,
                        bobVehicle
                );


        mockMvc.perform(delete("/api/maintenance/" + bobMaintenance)
                .header(
                        "Authorization",
                        "Bearer " + aliceToken
                ))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Should reject unauthenticated maintenance access")
    void shouldRejectUnauthenticatedMaintenanceAccess() throws Exception {


        mockMvc.perform(get("/api/maintenance"))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("Should not return maintenance for another company's vehicle")
    void shouldNotReturnMaintenanceForAnotherCompanyVehicle() throws Exception {


        register(
                "alice",
                "Company A"
        );

        register(
                "bob",
                "Company B"
        );


        String aliceToken = login("alice");
        String bobToken = login("bob");


        Long bobVehicle =
                createVehicle(
                        bobToken,
                        "Bob Van"
                );


        createMaintenance(
                bobToken,
                bobVehicle
        );


        mockMvc.perform(get("/api/maintenance/vehicle/" + bobVehicle)
                .header(
                        "Authorization",
                        "Bearer " + aliceToken
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }
}