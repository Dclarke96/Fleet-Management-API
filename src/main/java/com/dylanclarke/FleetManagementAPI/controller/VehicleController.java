package com.dylanclarke.FleetManagementAPI.controller;

import com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO;
import com.dylanclarke.FleetManagementAPI.service.VehicleService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    // ----------------------------------------
    // GET ALL
    // ----------------------------------------
    @GetMapping
    public List<VehicleResponseDTO> getAllVehicles() {
        return service.getAllVehicles();
    }

    // ----------------------------------------
    // GET BY ID
    // ----------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicle(@PathVariable Long id) {
        return service.getVehicleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------------------------------
    // SEARCH
    // ----------------------------------------
    @GetMapping("/search")
    public List<VehicleResponseDTO> searchVehicles(@RequestParam("q") String query) {
        return service.searchVehicles(query);
    }

    // ----------------------------------------
    // CREATE
    // ----------------------------------------
    @PostMapping
    public ResponseEntity<VehicleResponseDTO> createVehicle(
            @Valid @RequestBody VehicleRequestDTO request) {
        VehicleResponseDTO created = service.addVehicle(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    // ----------------------------------------
    // UPDATE
    // ----------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequestDTO request) {
        VehicleResponseDTO updated = service.updateVehicle(id, request);
        return ResponseEntity.ok(updated);
    }

    // ----------------------------------------
    // DELETE
    // ----------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        service.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}