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

import com.dylanclarke.FleetManagementAPI.api.ApiResponse;
import com.dylanclarke.FleetManagementAPI.api.PageResponse;
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
    public ResponseEntity<ApiResponse<PageResponse<VehicleResponseDTO>>> getAllVehicles(Pageable pageable) {
        Page<VehicleResponseDTO> page = service.getAllVehicles(pageable);
        PageResponse<VehicleResponseDTO> pageResponse = new PageResponse<>(page);
        return ResponseEntity.ok(
                new ApiResponse<>(true, pageResponse, "Vehicles retrieved successfully")
        );
    }

    // ----------------------------------------
    // GET BY ID
    // ----------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> getVehicleById(@PathVariable Long id) {
        VehicleResponseDTO dto = service.getVehicleById(id);
        return ResponseEntity.ok(
                new ApiResponse<>(true, dto, "Vehicle retrieved successfully")
        );
    }

    // ----------------------------------------
    // SEARCH
    // ----------------------------------------
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<VehicleResponseDTO>>> searchVehicles(@RequestParam("q") String query, Pageable pageable) {
        Page<VehicleResponseDTO> page = service.searchVehicles(query, pageable);
        PageResponse<VehicleResponseDTO> pageResponse = new PageResponse<>(page);
        return ResponseEntity.ok(
                new ApiResponse<>(true, pageResponse, "Vehicles search results retrieved successfully")
        );
    }

    // ----------------------------------------
    // CREATE
    // ----------------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> createVehicle(
            @Valid @RequestBody VehicleRequestDTO request) {
        VehicleResponseDTO created = service.addVehicle(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, created, "Vehicle created successfully"));
    }

    // ----------------------------------------
    // UPDATE
    // ----------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponseDTO>> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequestDTO request) {
        VehicleResponseDTO updated = service.updateVehicle(id, request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, updated, "Vehicle updated successfully")
        );
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