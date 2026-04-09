package com.dylanclarke.FleetManagementAPI.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dylanclarke.FleetManagementAPI.api.ApiResponse;
import com.dylanclarke.FleetManagementAPI.api.PageResponse;
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
    public ResponseEntity<ApiResponse<PageResponse<MaintenanceResponseDTO>>> getAllMaintenance(Pageable pageable) {

        Page<MaintenanceResponseDTO> page = maintenanceService.getAllMaintenance(pageable);
        PageResponse<MaintenanceResponseDTO> pageResponse = new PageResponse<>(page);

        return ResponseEntity.ok(
                new ApiResponse<>(true, pageResponse, "Maintenance retrieved successfully")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceResponseDTO>> getById(@PathVariable Long id) {

        MaintenanceResponseDTO response = maintenanceService.getMaintenanceById(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, response, "Maintenance retrieved successfully")
        );
    }

    @GetMapping("/vehicle/{vehicleId}")
    public Page<MaintenanceResponseDTO> getByVehicle(@PathVariable Long vehicleId, Pageable pageable) {
        return maintenanceService.getMaintenanceForVehicle(vehicleId, pageable);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MaintenanceResponseDTO>> createMaintenance(
            @Valid @RequestBody MaintenanceRequestDTO requestDTO) {

        MaintenanceResponseDTO response = maintenanceService.addMaintenance(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, response, "Maintenance created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MaintenanceResponseDTO>> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody MaintenanceRequestDTO requestDTO) {

        MaintenanceResponseDTO response = maintenanceService.updateMaintenance(id, requestDTO);

        return ResponseEntity.ok(
                new ApiResponse<>(true, response, "Maintenance updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
        return ResponseEntity.noContent().build();
    }
}