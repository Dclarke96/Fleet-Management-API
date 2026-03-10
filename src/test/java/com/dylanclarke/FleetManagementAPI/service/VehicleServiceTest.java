package com.dylanclarke.FleetManagementAPI.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.dylanclarke.FleetManagementAPI.model.Vehicle;
import com.dylanclarke.FleetManagementAPI.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"null","unused"})
class VehicleServiceTest {

    @Mock
    private VehicleRepository repository;

    @InjectMocks
    private VehicleService service;

    @Test
    @SuppressWarnings("null")
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

        com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO req = new com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO();
        req.setTitle("Truck 1");
        req.setMake("Ford");
        req.setModel("F-150");
        req.setYear(2020);

        com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO saved = service.addVehicle(req);

        assertEquals("Truck 1", saved.getTitle());
        verify(repository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void shouldReturnAllVehicles() {
        Vehicle a = new Vehicle("T","M","D",2021,"L",false,LocalDate.now(),LocalDate.now());
        when(repository.findAll()).thenReturn(List.of(a));
        List<com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO> result = service.getAllVehicles();
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetByIdWhenExists() {
        Vehicle a = new Vehicle("T","M","D",2021,"L",false,LocalDate.now(),LocalDate.now());
        when(repository.findById(5L)).thenReturn(Optional.of(a));
        Optional<com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO> got = service.getVehicleById(5L);
        assertEquals(true, got.isPresent());
    }

    @Test
    void updateNonexistentThrows() {
        when(repository.findById(10L)).thenReturn(Optional.empty());
        com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO r = new com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO();
        r.setTitle("T");
        IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class, () -> service.updateVehicle(10L, r));
        //noinspection unused
        String ignore1 = thrown1.getMessage();
    }

    @Test
    void deleteNonexistentThrows() {
        when(repository.findById(11L)).thenReturn(Optional.empty());
        IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class, () -> service.deleteVehicle(11L));
        //noinspection unused
        String ignore2 = thrown2.getMessage();
    }

    @Test
    void addVehicleValidationFails() {
        com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO bad = new com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO();
        // leave required fields blank
        IllegalArgumentException thrown3 = assertThrows(IllegalArgumentException.class, () -> service.addVehicle(bad));
        //noinspection unused
        String ignore3 = thrown3.getMessage();
    }

    @Test
    void searchVehiclesDelegates() {
        String q = "foo";
        Vehicle v = new Vehicle("T","M","D",2021,"L",false,LocalDate.now(),LocalDate.now());
        when(repository.searchVehicles(q)).thenReturn(List.of(v));
        List<com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO> result = service.searchVehicles(q);
        assertEquals(1, result.size());
    }
}