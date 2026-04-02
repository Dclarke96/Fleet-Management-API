package com.dylanclarke.FleetManagementAPI.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dylanclarke.FleetManagementAPI.model.MaintenanceRecord;
import com.dylanclarke.FleetManagementAPI.service.MaintenanceService;

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


    @PostMapping
    public ResponseEntity<MaintenanceRecord> addMaintenance(
            @RequestParam("vehicleId") Long vehicleId,
            @RequestBody MaintenanceRecord record) {

        MaintenanceRecord created = maintenanceService.addMaintenance(record, vehicleId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
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