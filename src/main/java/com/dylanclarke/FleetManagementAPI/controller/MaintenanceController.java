package com.dylanclarke.FleetManagementAPI.controller;

import com.dylanclarke.FleetManagementAPI.model.MaintenanceRecord;
import com.dylanclarke.FleetManagementAPI.service.MaintenanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public List<MaintenanceRecord> getAllMaintenance() {
        return maintenanceService.getAllMaintenance();
    }

    @GetMapping("/vehicle/{vehicleId}")
    public List<MaintenanceRecord> getForVehicle(@PathVariable Long vehicleId) {
        return maintenanceService.getMaintenanceForVehicle(vehicleId);
    }

    @GetMapping("/{id}")
    public MaintenanceRecord getById(@PathVariable Long id) {
        return maintenanceService.getMaintenanceById(id);
    }

    @PostMapping("/vehicle/{vehicleId}")
    public ResponseEntity<MaintenanceRecord> addMaintenance(
            @PathVariable Long vehicleId,
            @RequestBody MaintenanceRecord record) {
        MaintenanceRecord saved = maintenanceService.addMaintenance(record, vehicleId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceRecord> updateMaintenance(
            @PathVariable Long id,
            @RequestBody MaintenanceRecord record) {
        MaintenanceRecord updated = maintenanceService.updateMaintenance(id, record);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);
        return ResponseEntity.noContent().build();
    }
}