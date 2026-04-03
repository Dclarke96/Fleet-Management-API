package com.dylanclarke.FleetManagementAPI.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dylanclarke.FleetManagementAPI.dto.MaintenanceRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.MaintenanceResponseDTO;
import com.dylanclarke.FleetManagementAPI.service.MaintenanceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public List<MaintenanceResponseDTO> getAllMaintenance() {
        return maintenanceService.getAllMaintenance();
    }

    @GetMapping("/{id}")
    public MaintenanceResponseDTO getById(@PathVariable Long id) {
        return maintenanceService.getMaintenanceById(id);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<MaintenanceResponseDTO> getByVehicle(@PathVariable Long vehicleId) {
        return maintenanceService.getMaintenanceForVehicle(vehicleId);
    }

    @PostMapping
    public ResponseEntity<MaintenanceResponseDTO> createMaintenance(
            @Valid @RequestBody MaintenanceRequestDTO requestDTO) {

        MaintenanceResponseDTO response = maintenanceService.addMaintenance(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceResponseDTO> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceRequestDTO requestDTO) {

        MaintenanceResponseDTO response = maintenanceService.updateMaintenance(id, requestDTO);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
        return ResponseEntity.noContent().build();
    }
}