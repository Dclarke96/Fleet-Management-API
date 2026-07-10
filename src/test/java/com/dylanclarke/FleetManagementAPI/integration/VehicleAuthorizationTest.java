package com.dylanclarke.FleetManagementAPI.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



class VehicleAuthorizationTest extends BaseIntegrationTest {


    // =========================================================
    // AUTHORIZATION TESTS
    // =========================================================


    @Test
    @DisplayName("Should return only vehicles belonging to authenticated user's company")
    void shouldReturnOnlyUsersCompanyVehicles() throws Exception {


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


        createVehicle(
                aliceToken,
                "Alice Truck"
        );


        createVehicle(
                bobToken,
                "Bob Van"
        );


        mockMvc.perform(get("/api/vehicles")
                .header(
                        "Authorization",
                        "Bearer " + aliceToken
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].title")
                        .value("Alice Truck"))
                .andExpect(jsonPath("$.data.content[0].make")
                        .value("Ford"));
    }


    @Test
    @DisplayName("Should not return vehicle belonging to another company")
    void shouldNotReturnVehicleFromAnotherCompany() throws Exception {


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


        createVehicle(
                aliceToken,
                "Alice Truck"
        );


        Long bobVehicleId =
                createVehicle(
                        bobToken,
                        "Bob Van"
                );


        mockMvc.perform(get("/api/vehicles/" + bobVehicleId)
                .header(
                        "Authorization",
                        "Bearer " + aliceToken
                ))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Should not update vehicle belonging to another company")
    void shouldNotUpdateVehicleFromAnotherCompany() throws Exception {


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


        Long bobVehicleId =
                createVehicle(
                        bobToken,
                        "Bob Van"
                );


        mockMvc.perform(put("/api/vehicles/" + bobVehicleId)
                .header(
                        "Authorization",
                        "Bearer " + aliceToken
                )
                .contentType("application/json")
                .content("""
                {
                  "title":"Unauthorized Update",
                  "vin":"BADVIN",
                  "licensePlate":"BAD123",
                  "make":"Tesla",
                  "model":"Cybertruck",
                  "vehicleYear":2026,
                  "location":"Hack Attempt",
                  "maintenanceAlertsEnabled":true,
                  "startDate":"2026-01-01",
                  "endDate":"2026-12-31"
                }
                """))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Should not delete vehicle belonging to another company")
    void shouldNotDeleteVehicleFromAnotherCompany() throws Exception {


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


        Long bobVehicleId =
                createVehicle(
                        bobToken,
                        "Bob Van"
                );


        mockMvc.perform(delete("/api/vehicles/" + bobVehicleId)
                .header(
                        "Authorization",
                        "Bearer " + aliceToken
                ))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Should create vehicle belonging to authenticated user's company")
    void shouldCreateVehicleForAuthenticatedUsersCompany() throws Exception {


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


        createVehicle(
                aliceToken,
                "Alice Truck"
        );


        mockMvc.perform(get("/api/vehicles")
                .header(
                        "Authorization",
                        "Bearer " + aliceToken
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].title")
                        .value("Alice Truck"));


        mockMvc.perform(get("/api/vehicles")
                .header(
                        "Authorization",
                        "Bearer " + bobToken
                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }


    @Test
    @DisplayName("Should reject unauthenticated vehicle access")
    void shouldRejectUnauthenticatedVehicleAccess() throws Exception {


        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isUnauthorized());
    }
}