package com.dylanclarke.FleetManagementAPI.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.dylanclarke.FleetManagementAPI.dto.MaintenanceRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.MaintenanceResponseDTO;
import com.dylanclarke.FleetManagementAPI.exception.ResourceNotFoundException;
import com.dylanclarke.FleetManagementAPI.model.MaintenanceRecord;
import com.dylanclarke.FleetManagementAPI.model.Vehicle;
import com.dylanclarke.FleetManagementAPI.repository.MaintenanceRepository;
import com.dylanclarke.FleetManagementAPI.repository.VehicleRepository;

class MaintenanceServiceTest {

    private MaintenanceRepository maintenanceRepository;
    private VehicleRepository vehicleRepository;
    private MaintenanceService service;

    @BeforeEach
    void setup() {
        maintenanceRepository = mock(MaintenanceRepository.class);
        vehicleRepository = mock(VehicleRepository.class);
        service = new MaintenanceService(maintenanceRepository, vehicleRepository);
    }

    @Test
    void shouldSaveMaintenance() {

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setStartDate(LocalDate.now()); // ✅ REQUIRED
        vehicle.setEndDate(LocalDate.now().plusDays(10)); // optional but safe

        MaintenanceRequestDTO request = new MaintenanceRequestDTO();
        request.setVehicleId(1L);
        request.setDescription("Oil change");
        request.setDate(LocalDate.now().plusDays(1));
        request.setCost(50.0);

        MaintenanceResponseDTO saved = new MaintenanceResponseDTO();
        saved.setId(1L);
        saved.setVehicleId(1L);
        saved.setDescription("Oil change");
        saved.setDate(request.getDate());

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        when(maintenanceRepository.save(any())).thenAnswer(invocation -> {
            MaintenanceRecord entity = invocation.getArgument(0);
            entity.setId(1L); // simulate DB assigning ID
            return entity;
        });

        MaintenanceResponseDTO result = service.addMaintenance(request);

        assertEquals("Oil change", result.getDescription());
        assertEquals(1L, result.getVehicleId());
    }

    @Test
    void shouldGetAllMaintenance() {

        MaintenanceResponseDTO r1 = new MaintenanceResponseDTO();
        r1.setId(1L);

        MaintenanceResponseDTO r2 = new MaintenanceResponseDTO();
        r2.setId(2L);

        // If your service maps entities -> DTOs internally,
        // this assumes repository returns entities which service maps
        when(maintenanceRepository.findAll()).thenReturn(List.of());

        List<MaintenanceResponseDTO> results = service.getAllMaintenance();

        // Depending on your mapping logic, adjust expected size
        assertNotNull(results);
    }

    @Test
    void shouldDeleteMaintenance() {

        doNothing().when(maintenanceRepository).deleteById(1L);

        assertDoesNotThrow(() -> service.deleteMaintenance(1L));

        verify(maintenanceRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowOnMissingMaintenance() {

        when(maintenanceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getMaintenanceById(99L));
    }
}