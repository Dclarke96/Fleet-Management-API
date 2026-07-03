package com.dylanclarke.FleetManagementAPI.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dylanclarke.FleetManagementAPI.dto.MaintenanceRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.MaintenanceResponseDTO;
import com.dylanclarke.FleetManagementAPI.exception.ResourceNotFoundException;
import com.dylanclarke.FleetManagementAPI.exception.ValidationException;
import com.dylanclarke.FleetManagementAPI.model.MaintenanceRecord;
import com.dylanclarke.FleetManagementAPI.model.Vehicle;
import com.dylanclarke.FleetManagementAPI.repository.MaintenanceRepository;
import com.dylanclarke.FleetManagementAPI.repository.VehicleRepository;
import com.dylanclarke.FleetManagementAPI.security.CurrentUserService;

@Service
@SuppressWarnings("null")
public class MaintenanceService {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceService.class);

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    private final CurrentUserService currentUserService;

    public MaintenanceService(MaintenanceRepository maintenanceRepository,
                              VehicleRepository vehicleRepository,
                              CurrentUserService currentUserService) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
        this.currentUserService = currentUserService;
    }

    // ----------------------------------------------------
    // VEHICLE LOOKUP (TENANT SAFE)
    // ----------------------------------------------------
    private Vehicle getVehicleForCurrentCompany(Long vehicleId, Long companyId) {
        return vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vehicle", "id", vehicleId));
    }

    // ----------------------------------------------------
    // GET ALL (TENANT SAFE VIA VEHICLE FILTER)
    // ----------------------------------------------------
    public Page<MaintenanceResponseDTO> getAllMaintenance(Pageable pageable) {

        Long companyId = currentUserService.getCompanyId();

        return maintenanceRepository
                .findByVehicle_Company_Id(companyId, pageable)
                .map(this::mapToDTO);
    }

    // ----------------------------------------------------
    // GET BY ID (TENANT SAFE)
    // ----------------------------------------------------
    public MaintenanceResponseDTO getMaintenanceById(Long id) {

        Long companyId = currentUserService.getCompanyId();

        MaintenanceRecord record = maintenanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Maintenance record", "id", id));

        if (!record.getVehicle().getCompany().getId().equals(companyId)) {
            throw new ResourceNotFoundException("Maintenance record", "id", id);
        }

        return mapToDTO(record);
    }

    // ----------------------------------------------------
    // GET BY VEHICLE (TENANT SAFE)
    // ----------------------------------------------------
    public Page<MaintenanceResponseDTO> getMaintenanceForVehicle(Long vehicleId, Pageable pageable) {

        Long companyId = currentUserService.getCompanyId();

        return maintenanceRepository
                .findByVehicle_IdAndVehicle_Company_Id(vehicleId, companyId, pageable)
                .map(this::mapToDTO);
    }

    // ----------------------------------------------------
    // CREATE
    // ----------------------------------------------------
    public MaintenanceResponseDTO addMaintenance(MaintenanceRequestDTO request) {

        Long companyId = currentUserService.getCompanyId();

        Vehicle vehicle = getVehicleForCurrentCompany(request.getVehicleId(), companyId);

        MaintenanceRecord record = mapToEntity(request);

        validateRecord(record, vehicle);

        record.setVehicle(vehicle);

        MaintenanceRecord saved = maintenanceRepository.save(record);

        log.info(
                "Maintenance created: maintenanceId={}, vehicleId={}, companyId={}",
                saved.getId(),
                vehicle.getId(),
                companyId
        );

        return mapToDTO(saved);
    }

    // ----------------------------------------------------
    // UPDATE
    // ----------------------------------------------------
    public MaintenanceResponseDTO updateMaintenance(Long id, MaintenanceRequestDTO request) {

        Long companyId = currentUserService.getCompanyId();

        MaintenanceRecord existing = maintenanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Maintenance record", "id", id));

        if (!existing.getVehicle().getCompany().getId().equals(companyId)) {
            throw new ResourceNotFoundException("Maintenance record", "id", id);
        }

        Vehicle vehicle = getVehicleForCurrentCompany(request.getVehicleId(), companyId);

        existing.setDescription(request.getDescription());
        existing.setServiceDate(request.getDate());
        existing.setCost(request.getCost());
        existing.setVehicle(vehicle);

        validateRecord(existing, vehicle);

        MaintenanceRecord saved = maintenanceRepository.save(existing);

        log.info(
                "Maintenance updated: maintenanceId={}, vehicleId={}, companyId={}",
                saved.getId(),
                vehicle.getId(),
                companyId
        );

        return mapToDTO(saved);
    }

    // ----------------------------------------------------
    // DELETE
    // ----------------------------------------------------
    public void deleteMaintenance(Long id) {

        Long companyId = currentUserService.getCompanyId();

        MaintenanceRecord record = maintenanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Maintenance record", "id", id));

        if (!record.getVehicle().getCompany().getId().equals(companyId)) {
            throw new ResourceNotFoundException("Maintenance record", "id", id);
        }

        maintenanceRepository.delete(record);

        log.info(
                "Maintenance deleted: maintenanceId={}, vehicleId={}, companyId={}",
                record.getId(),
                record.getVehicle().getId(),
                companyId
        );
    }
    // ----------------------------------------------------
    // MAPPING
    // ----------------------------------------------------
    private MaintenanceResponseDTO mapToDTO(MaintenanceRecord record) {

        MaintenanceResponseDTO dto = new MaintenanceResponseDTO();

        dto.setId(record.getId());
        dto.setVehicleId(record.getVehicle().getId());
        dto.setDescription(record.getDescription());
        dto.setDate(record.getServiceDate());
        dto.setCost(record.getCost());

        return dto;
    }

    private MaintenanceRecord mapToEntity(MaintenanceRequestDTO request) {

        MaintenanceRecord record = new MaintenanceRecord();

        record.setDescription(request.getDescription());
        record.setServiceDate(request.getDate());
        record.setCost(request.getCost());

        return record;
    }

    // ----------------------------------------------------
    // VALIDATION
    // ----------------------------------------------------
    private void validateRecord(MaintenanceRecord record, Vehicle vehicle) {

        if (record.getDescription() == null || record.getDescription().trim().length() < 3) {
            throw new ValidationException("Description must be at least 3 characters");
        }

        if (record.getServiceDate() == null) {
            throw new ValidationException("Service date cannot be null");
        }

        if (record.getServiceDate().isBefore(vehicle.getStartDate())) {
            throw new ValidationException("Invalid service date");
        }

        if (vehicle.getEndDate() != null &&
                record.getServiceDate().isAfter(vehicle.getEndDate())) {
            throw new ValidationException("Invalid service date");
        }
    }
}