package com.dylanclarke.FleetManagementAPI.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dylanclarke.FleetManagementAPI.dto.MaintenanceRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.MaintenanceResponseDTO;
import com.dylanclarke.FleetManagementAPI.exception.ResourceNotFoundException;
import com.dylanclarke.FleetManagementAPI.exception.ValidationException;
import com.dylanclarke.FleetManagementAPI.model.MaintenanceRecord;
import com.dylanclarke.FleetManagementAPI.model.Vehicle;
import com.dylanclarke.FleetManagementAPI.repository.MaintenanceRepository;
import com.dylanclarke.FleetManagementAPI.repository.VehicleRepository;

@Service
@SuppressWarnings("null")
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository,
                              VehicleRepository vehicleRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
    }

    // ------------------ READ ------------------

    public List<MaintenanceResponseDTO> getAllMaintenance() {
        return maintenanceRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<MaintenanceResponseDTO> getMaintenanceForVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", vehicleId));

        return maintenanceRepository.findByVehicleOrderByServiceDateAsc(vehicle)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MaintenanceResponseDTO getMaintenanceById(Long id) {
        MaintenanceRecord record = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance record", "id", id));

        return mapToDTO(record);
    }

    // ------------------ CREATE ------------------

    public MaintenanceResponseDTO addMaintenance(MaintenanceRequestDTO request) {

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        MaintenanceRecord record = mapToEntity(request);

        validateRecord(record, vehicle);

        record.setVehicle(vehicle);

        MaintenanceRecord saved = maintenanceRepository.save(record);

        return mapToDTO(saved);
    }

    // ------------------ UPDATE ------------------

    public MaintenanceResponseDTO updateMaintenance(Long id, MaintenanceRequestDTO request) {

        MaintenanceRecord existing = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance record", "id", id));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        existing.setDescription(request.getDescription());
        existing.setServiceDate(request.getDate());
        existing.setVehicle(vehicle);

        validateRecord(existing, vehicle);

        MaintenanceRecord updated = maintenanceRepository.save(existing);

        return mapToDTO(updated);
    }

    // ------------------ DELETE ------------------

    public void deleteMaintenance(Long id) {
        maintenanceRepository.deleteById(id);
    }

    // ------------------ MAPPING ------------------

    private MaintenanceResponseDTO mapToDTO(MaintenanceRecord record) {
        MaintenanceResponseDTO dto = new MaintenanceResponseDTO();

        dto.setId(record.getId());
        dto.setVehicleId(record.getVehicle().getId());
        dto.setDescription(record.getDescription());
        dto.setDate(record.getServiceDate());

        return dto;
    }

    private MaintenanceRecord mapToEntity(MaintenanceRequestDTO request) {
        MaintenanceRecord record = new MaintenanceRecord();

        record.setDescription(request.getDescription());
        record.setServiceDate(request.getDate());

        return record;
    }

    // ------------------ VALIDATION ------------------

    private void validateRecord(MaintenanceRecord record, Vehicle vehicle) {

        // ✅ Description validation
        if (record.getDescription() == null || record.getDescription().trim().length() < 3) {
            throw new ValidationException(
                    "Description must be at least 3 characters",
                    "description",
                    record.getDescription()
            );
        }

        LocalDate serviceDate = record.getServiceDate();

        // ✅ CRITICAL FIX: Null check BEFORE using isBefore()
        if (serviceDate == null) {
            throw new ValidationException(
                    "Service date cannot be null",
                    "serviceDate",
                    null
            );
        }

        // Business rule validations
        if (serviceDate.isBefore(LocalDate.now())) {
            throw new ValidationException(
                    "Service date cannot be in the past",
                    "serviceDate",
                    serviceDate
            );
        }

        if (serviceDate.isBefore(vehicle.getStartDate())) {
            throw new ValidationException(
                    "Service date cannot be before vehicle start date",
                    "serviceDate",
                    serviceDate
            );
        }

        if (vehicle.getEndDate() != null && serviceDate.isAfter(vehicle.getEndDate())) {
            throw new ValidationException(
                    "Service date cannot be after vehicle end date",
                    "serviceDate",
                    serviceDate
            );
        }
    }
}