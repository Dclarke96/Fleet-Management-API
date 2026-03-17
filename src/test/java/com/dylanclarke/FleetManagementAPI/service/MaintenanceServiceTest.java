package com.dylanclarke.FleetManagementAPI.service;

import com.dylanclarke.FleetManagementAPI.dto.MaintenanceRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.MaintenanceResponseDTO;
import com.dylanclarke.FleetManagementAPI.exception.ResourceNotFoundException;
import com.dylanclarke.FleetManagementAPI.model.MaintenanceRecord;
import com.dylanclarke.FleetManagementAPI.repository.MaintenanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MaintenanceServiceTest {

    private MaintenanceRepository repository;
    private MaintenanceService service;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(MaintenanceRepository.class);
        service = new MaintenanceService(repository);
    }

    @Test
    void shouldSaveMaintenance() {
        MaintenanceRecord record = new MaintenanceRecord();
        record.setId(1L);
        record.setVehicleId(1L);
        record.setDescription("Oil change");
        record.setDate(LocalDate.parse("2026-03-01"));
        record.setCost(49.99);

        when(repository.save(any(MaintenanceRecord.class))).thenReturn(record);

        MaintenanceRequestDTO dto = new MaintenanceRequestDTO();
        dto.setVehicleId(1L);
        dto.setDescription("Oil change");
        dto.setDate(LocalDate.parse("2026-03-01"));
        dto.setCost(49.99);

        MaintenanceResponseDTO response = service.addMaintenance(dto);

        assertEquals(1L, response.getId());
        assertEquals("Oil change", response.getDescription());
    }

    @Test
    void shouldGetAllMaintenance() {
        MaintenanceRecord r1 = new MaintenanceRecord();
        r1.setId(1L);
        r1.setVehicleId(1L);
        r1.setDescription("Oil change");
        MaintenanceRecord r2 = new MaintenanceRecord();
        r2.setId(2L);
        r2.setVehicleId(2L);
        r2.setDescription("Tire rotation");

        when(repository.findAll()).thenReturn(List.of(r1, r2));

        List<MaintenanceResponseDTO> results = service.getAllMaintenance();

        assertEquals(2, results.size());
    }

    @Test
    void shouldDeleteMaintenance() {
        MaintenanceRecord r = new MaintenanceRecord();
        r.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(r));
        doNothing().when(repository).deleteById(1L);

        assertDoesNotThrow(() -> service.deleteMaintenance(1L));
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowOnMissingMaintenance() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getMaintenanceById(99L));
    }
}