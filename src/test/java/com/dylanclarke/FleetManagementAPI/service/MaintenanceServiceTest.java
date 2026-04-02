package com.dylanclarke.FleetManagementAPI.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        vehicle.setStartDate(LocalDate.now());
        vehicle.setEndDate(LocalDate.now().plusDays(10));

        MaintenanceRecord record = new MaintenanceRecord();
        record.setDescription("Oil change");
        record.setServiceDate(LocalDate.now().plusDays(1));
        record.setAlertsEnabled(true);

        MaintenanceRecord saved = new MaintenanceRecord();
        saved.setId(1L);
        saved.setDescription("Oil change");
        saved.setServiceDate(record.getServiceDate());
        saved.setAlertsEnabled(true);
        saved.setVehicle(vehicle);

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(maintenanceRepository.save(any(MaintenanceRecord.class))).thenReturn(saved);

        MaintenanceRecord result = service.addMaintenance(record, 1L);

        assertEquals(1L, result.getId());
        assertEquals("Oil change", result.getDescription());
    }

    @Test
    void shouldGetAllMaintenance() {
        MaintenanceRecord r1 = new MaintenanceRecord();
        r1.setId(1L);

        MaintenanceRecord r2 = new MaintenanceRecord();
        r2.setId(2L);

        when(maintenanceRepository.findAll()).thenReturn(List.of(r1, r2));

        List<MaintenanceRecord> results = service.getAllMaintenance();

        assertEquals(2, results.size());
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