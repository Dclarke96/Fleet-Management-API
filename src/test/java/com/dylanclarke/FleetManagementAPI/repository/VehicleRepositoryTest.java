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

    // ---------------- SAVE ----------------

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

    // ---------------- FIND BY ID ----------------

    @Test
    void shouldFindVehicleById() {

        Vehicle vehicle = new Vehicle(
                "Find Test",
                "Toyota",
                "Corolla",
                2021,
                "Lot",
                false,
                LocalDate.now(),
                LocalDate.now().plusDays(5)
        );

        Vehicle saved = repository.save(vehicle);

        Vehicle found = repository.findById(saved.getId()).orElse(null);

        assertNotNull(found);
        assertEquals("Toyota", found.getMake());
        assertEquals("Corolla", found.getModel());
    }

    // ---------------- SEARCH ----------------

    @Test
    void shouldSearchVehicles() {

        Vehicle v1 = new Vehicle(
                "Truck Alpha",
                "Ford",
                "F-150",
                2020,
                "Yard",
                false,
                LocalDate.now(),
                LocalDate.now().plusDays(10)
        );

        Vehicle v2 = new Vehicle(
                "Sedan Beta",
                "Toyota",
                "Camry",
                2021,
                "Garage",
                false,
                LocalDate.now(),
                LocalDate.now().plusDays(10)
        );

        repository.save(v1);
        repository.save(v2);

        var results = repository.searchVehicles(
                "Ford",
                org.springframework.data.domain.Pageable.unpaged()
        );

        assertEquals(1, results.getContent().size());
        assertEquals("Ford", results.getContent().get(0).getMake());
    }
}