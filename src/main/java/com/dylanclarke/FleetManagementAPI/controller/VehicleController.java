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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO;
import com.dylanclarke.FleetManagementAPI.service.VehicleService;

import jakarta.validation.Valid;

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
    public Page<VehicleResponseDTO> getAllVehicles(Pageable pageable) {
        return service.getAllVehicles(pageable);
    }

    // ----------------------------------------
    // GET BY ID
    // ----------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponseDTO> getVehicleById(@PathVariable Long id) {
        VehicleResponseDTO dto = service.getVehicleById(id);
        return ResponseEntity.ok(dto);
    }

    // ----------------------------------------
    // SEARCH
    // ----------------------------------------
    @GetMapping("/search")
    public Page<VehicleResponseDTO> searchVehicles(@RequestParam("q") String query, Pageable pageable) {
        return service.searchVehicles(query, pageable);
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