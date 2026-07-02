package com.dylanclarke.FleetManagementAPI.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.dylanclarke.FleetManagementAPI.dto.MaintenanceRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.MaintenanceResponseDTO;
import com.dylanclarke.FleetManagementAPI.exception.ResourceNotFoundException;
import com.dylanclarke.FleetManagementAPI.exception.ValidationException;
import com.dylanclarke.FleetManagementAPI.model.MaintenanceRecord;
import com.dylanclarke.FleetManagementAPI.model.User;
import com.dylanclarke.FleetManagementAPI.model.Vehicle;
import com.dylanclarke.FleetManagementAPI.repository.MaintenanceRepository;
import com.dylanclarke.FleetManagementAPI.repository.UserRepository;
import com.dylanclarke.FleetManagementAPI.repository.VehicleRepository;

@Service
@SuppressWarnings("null")
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository,
                              VehicleRepository vehicleRepository,
                              UserRepository userRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    // ----------------------------------------------------
    // CURRENT COMPANY ID
    // ----------------------------------------------------
    private Long getCurrentCompanyId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getCompany().getId();
    }

    private Vehicle getVehicleForCurrentCompany(Long vehicleId, Long companyId) {
        return vehicleRepository.findByIdAndCompanyId(vehicleId, companyId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Vehicle", "id", vehicleId));
    }

    // ----------------------------------------------------
    // GET ALL (TENANT SAFE VIA VEHICLE FILTER)
    // ----------------------------------------------------
    public Page<MaintenanceResponseDTO> getAllMaintenance(Pageable pageable) {

        Long companyId = getCurrentCompanyId();

        return maintenanceRepository
                .findByVehicle_Company_Id(companyId, pageable)
                .map(this::mapToDTO);
    }

    // ----------------------------------------------------
    // GET BY ID (TENANT SAFE)
    // ----------------------------------------------------
    public MaintenanceResponseDTO getMaintenanceById(Long id) {

        Long companyId = getCurrentCompanyId();

        MaintenanceRecord record = maintenanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Maintenance record", "id", id));

        // enforce tenant safety via vehicle
        if (!record.getVehicle().getCompany().getId().equals(companyId)) {
            throw new ResourceNotFoundException("Maintenance record", "id", id);
        }

        return mapToDTO(record);
    }

    // ----------------------------------------------------
    // GET BY VEHICLE (TENANT SAFE)
    // ----------------------------------------------------
    public Page<MaintenanceResponseDTO> getMaintenanceForVehicle(Long vehicleId, Pageable pageable) {

        Long companyId = getCurrentCompanyId();

        return maintenanceRepository
                .findByVehicle_IdAndVehicle_Company_Id(vehicleId, companyId, pageable)
                .map(this::mapToDTO);
    }

    // ----------------------------------------------------
    // CREATE
    // ----------------------------------------------------
    public MaintenanceResponseDTO addMaintenance(MaintenanceRequestDTO request) {

        Long companyId = getCurrentCompanyId();

        Vehicle vehicle = getVehicleForCurrentCompany(request.getVehicleId(), companyId);

        MaintenanceRecord record = mapToEntity(request);

        validateRecord(record, vehicle);

        record.setVehicle(vehicle);

        return mapToDTO(maintenanceRepository.save(record));
    }

    // ----------------------------------------------------
    // UPDATE
    // ----------------------------------------------------
    public MaintenanceResponseDTO updateMaintenance(Long id, MaintenanceRequestDTO request) {

        Long companyId = getCurrentCompanyId();

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

        return mapToDTO(maintenanceRepository.save(existing));
    }

    // ----------------------------------------------------
    // DELETE
    // ----------------------------------------------------
    public void deleteMaintenance(Long id) {

        Long companyId = getCurrentCompanyId();

        MaintenanceRecord record = maintenanceRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Maintenance record", "id", id));

        if (!record.getVehicle().getCompany().getId().equals(companyId)) {
            throw new ResourceNotFoundException("Maintenance record", "id", id);
        }

        maintenanceRepository.delete(record);
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