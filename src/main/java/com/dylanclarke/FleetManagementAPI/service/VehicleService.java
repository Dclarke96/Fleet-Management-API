package com.dylanclarke.FleetManagementAPI.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO;
import com.dylanclarke.FleetManagementAPI.model.Vehicle;
import com.dylanclarke.FleetManagementAPI.repository.VehicleRepository;

@Service
public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    // ----------------------------------------
    // GET ALL
    // ----------------------------------------
    public List<VehicleResponseDTO> getAllVehicles() {
        return repository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ----------------------------------------
    // GET BY ID
    // ----------------------------------------
    public Optional<VehicleResponseDTO> getVehicleById(Long id) {
        return repository.findById(id)
                .map(this::toDto);
    }

    // ----------------------------------------
    // SEARCH
    // ----------------------------------------
    public List<VehicleResponseDTO> searchVehicles(String query) {
        return repository.searchVehicles(query)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ----------------------------------------
    // CREATE
    // ----------------------------------------
    public VehicleResponseDTO addVehicle(VehicleRequestDTO dto) {

        Vehicle entity = toEntity(dto);

        validateVehicle(entity);

        Vehicle saved = repository.save(entity);

        return toDto(saved);
    }

    // ----------------------------------------
    // UPDATE
    // ----------------------------------------
    public VehicleResponseDTO updateVehicle(Long id, VehicleRequestDTO dto) {

        Vehicle existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        updateEntityFromDto(existing, dto);

        validateVehicle(existing);

        Vehicle saved = repository.save(existing);

        return toDto(saved);
    }

    // ----------------------------------------
    // DELETE
    // ----------------------------------------
    public void deleteVehicle(Long id) {

        Vehicle vehicle = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        repository.delete(vehicle);
    }

    // ----------------------------------------------------
    // DTO MAPPING
    // ----------------------------------------------------

    private VehicleResponseDTO toDto(Vehicle v) {

        VehicleResponseDTO dto = new VehicleResponseDTO();

        dto.setId(v.getId());
        dto.setTitle(v.getTitle());
        dto.setVin(v.getVin());
        dto.setLicensePlate(v.getLicensePlate());
        dto.setMake(v.getMake());
        dto.setModel(v.getModel());
        dto.setVehicleYear(v.getVehicleYear());

        dto.setLocation(v.getLocation());
        dto.setMaintenanceAlertsEnabled(v.isMaintenanceAlertsEnabled());
        dto.setStartDate(v.getStartDate());
        dto.setEndDate(v.getEndDate());

        return dto;
    }

    private Vehicle toEntity(VehicleRequestDTO dto) {

        Vehicle v = new Vehicle();

        v.setTitle(dto.getTitle());
        v.setVin(dto.getVin());
        v.setLicensePlate(dto.getLicensePlate());
        v.setMake(dto.getMake());
        v.setModel(dto.getModel());

        if (dto.getYear() != null) {
            v.setVehicleYear(dto.getYear());
        }

        v.setLocation(dto.getLocation());
        v.setMaintenanceAlertsEnabled(dto.getMaintenanceAlertsEnabled() != null ? dto.getMaintenanceAlertsEnabled() : false);
        v.setStartDate(dto.getStartDate());
        v.setEndDate(dto.getEndDate());

        return v;
    }

    private void updateEntityFromDto(Vehicle vehicle, VehicleRequestDTO dto) {

        vehicle.setTitle(dto.getTitle());
        vehicle.setVin(dto.getVin());
        vehicle.setLicensePlate(dto.getLicensePlate());
        vehicle.setMake(dto.getMake());
        vehicle.setModel(dto.getModel());

        if (dto.getYear() != null) {
            vehicle.setVehicleYear(dto.getYear());
        }

        vehicle.setLocation(dto.getLocation());
        vehicle.setMaintenanceAlertsEnabled(dto.getMaintenanceAlertsEnabled());
        vehicle.setStartDate(dto.getStartDate());
        vehicle.setEndDate(dto.getEndDate());
    }

    // ----------------------------------------------------
    // VALIDATION
    // ----------------------------------------------------

    private void validateVehicle(Vehicle vehicle) {

        if (vehicle.getMake() == null || vehicle.getMake().isEmpty() ||
            vehicle.getModel() == null || vehicle.getModel().isEmpty()) {
            throw new IllegalArgumentException("Make and model are required");
        }

        int currentYear = LocalDate.now().getYear();

        if (vehicle.getVehicleYear() < 1900 || vehicle.getVehicleYear() > currentYear) {
            throw new IllegalArgumentException(
                    "Year must be between 1900 and " + currentYear);
        }

        LocalDate start = vehicle.getStartDate();
        LocalDate end = vehicle.getEndDate();

        if (start == null) {
            throw new IllegalArgumentException("Start date is required");
        }

        if (end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }
}