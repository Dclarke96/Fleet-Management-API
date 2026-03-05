package com.dylanclarke.FleetManagementAPI.repository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.dylanclarke.FleetManagementAPI.model.Vehicle;

@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository repository;

    @Test
    void shouldSaveVehicle() {

        Vehicle vehicle = new Vehicle(
                "Repo Test",
                "Honda",
                "Civic",
                2022,
                "Garage",
                false,
                LocalDate.now(),
                LocalDate.now().plusDays(10)
        );

        Vehicle saved = repository.save(vehicle);

        assertNotNull(saved.getId());
        assertEquals("Honda", saved.getMake());
    }
}