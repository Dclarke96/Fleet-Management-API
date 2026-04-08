package com.dylanclarke.FleetManagementAPI.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO;
import com.dylanclarke.FleetManagementAPI.exception.ResourceNotFoundException;
import com.dylanclarke.FleetManagementAPI.exception.ValidationException;
import com.dylanclarke.FleetManagementAPI.model.Vehicle;
import com.dylanclarke.FleetManagementAPI.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"null","unused"})
class VehicleServiceTest {

    @Mock
    private VehicleRepository repository;

    @InjectMocks
    private VehicleService service;

    // ---------------- CREATE ----------------

    @Test
    void shouldSaveVehicle() {

        Vehicle vehicle = new Vehicle(
                "Truck 1",
                "Ford",
                "F-150",
                2020,
                "Yard",
                false,
                LocalDate.now(),
                LocalDate.now().plusDays(30)
        );

        when(repository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleRequestDTO req = new VehicleRequestDTO();
        req.setTitle("Truck 1");
        req.setMake("Ford");
        req.setModel("F-150");
        req.setYear(2020);
        req.setLocation("Yard");
        req.setMaintenanceAlertsEnabled(false);
        req.setStartDate(LocalDate.now());
        req.setEndDate(LocalDate.now().plusDays(30));

        VehicleResponseDTO saved = service.addVehicle(req);

        assertEquals("Truck 1", saved.getTitle());
        verify(repository, times(1)).save(any(Vehicle.class));
    }

    // ---------------- GET ALL (FIXED FOR PAGE) ----------------

    @Test
    void shouldReturnAllVehicles() {

        Vehicle a = new Vehicle("T","M","D",2021,"L",false,LocalDate.now(),LocalDate.now());

        Page<Vehicle> page = new PageImpl<>(List.of(a));

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        Page<VehicleResponseDTO> result =
                service.getAllVehicles(Pageable.unpaged());

        assertEquals(1, result.getContent().size());
    }

    // ---------------- GET BY ID ----------------

    @Test
    void shouldGetByIdWhenExists() {

        Vehicle a = new Vehicle("T","M","D",2021,"L",false,LocalDate.now(),LocalDate.now());

        when(repository.findById(5L)).thenReturn(Optional.of(a));

        VehicleResponseDTO got = service.getVehicleById(5L);

        assertNotNull(got);
    }

    // ---------------- UPDATE ----------------

    @Test
    void updateNonexistentThrows() {

        when(repository.findById(10L)).thenReturn(Optional.empty());

        VehicleRequestDTO r = new VehicleRequestDTO();
        r.setTitle("T");

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateVehicle(10L, r));
    }

    // ---------------- DELETE ----------------

    @Test
    void deleteNonexistentThrows() {

        when(repository.findById(11L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.deleteVehicle(11L));
    }

    // ---------------- VALIDATION ----------------

    @Test
    void addVehicleValidationFails() {

        VehicleRequestDTO bad = new VehicleRequestDTO();

        assertThrows(ValidationException.class,
                () -> service.addVehicle(bad));
    }

    // ---------------- SEARCH (FIXED FOR PAGE) ----------------

    @Test
    void searchVehiclesDelegates() {

        String q = "foo";

        Vehicle v = new Vehicle("T","M","D",2021,"L",false,LocalDate.now(),LocalDate.now());

        Page<Vehicle> page = new PageImpl<>(List.of(v));

        when(repository.searchVehicles(eq(q), any(Pageable.class)))
                .thenReturn(page);

        Page<VehicleResponseDTO> result =
                service.searchVehicles(q, Pageable.unpaged());

        assertEquals(1, result.getContent().size());
    }
}