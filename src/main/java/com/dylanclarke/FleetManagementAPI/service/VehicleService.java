package com.dylanclarke.FleetManagementAPI.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.dylanclarke.FleetManagementAPI.dto.VehicleRequestDTO;
import com.dylanclarke.FleetManagementAPI.dto.VehicleResponseDTO;
import com.dylanclarke.FleetManagementAPI.exception.ResourceNotFoundException;
import com.dylanclarke.FleetManagementAPI.exception.ValidationException;
import com.dylanclarke.FleetManagementAPI.model.Company;
import com.dylanclarke.FleetManagementAPI.model.User;
import com.dylanclarke.FleetManagementAPI.model.Vehicle;
import com.dylanclarke.FleetManagementAPI.repository.UserRepository;
import com.dylanclarke.FleetManagementAPI.repository.VehicleRepository;
import com.dylanclarke.FleetManagementAPI.security.CurrentUserService;

@Service
@SuppressWarnings("null")
public class VehicleService {

    private final VehicleRepository repository;
    private final CurrentUserService currentUserService;

    public VehicleService(
            VehicleRepository repository,
            CurrentUserService currentUserService
    ) {
        this.repository = repository;
        this.currentUserService = currentUserService;
    }

    // ----------------------------------------
    // GET ALL (PAGINATED)
    // ----------------------------------------
    public Page<VehicleResponseDTO> getAllVehicles(Pageable pageable) {

        Long companyId = currentUserService.getCompanyId();

        return repository.findByCompanyId(companyId, pageable)
                .map(this::toDto);
    }

    // ----------------------------------------
    // GET BY ID
    // ----------------------------------------
    public VehicleResponseDTO getVehicleById(Long id) {

        Long companyId = currentUserService.getCompanyId();

        Vehicle vehicle = repository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

        return toDto(vehicle);
    }

    // ----------------------------------------
    // SEARCH (PAGINATED)
    // ----------------------------------------
    public Page<VehicleResponseDTO> searchVehicles(String query, Pageable pageable) {

        Long companyId = currentUserService.getCompanyId();

        return repository.searchVehiclesByCompany(companyId, query, pageable)
                .map(this::toDto);
    }

    // ----------------------------------------
    // CREATE
    // ----------------------------------------
    public VehicleResponseDTO addVehicle(VehicleRequestDTO dto) {

        Long companyId = currentUserService.getCompanyId();

        Vehicle entity = toEntity(dto);

        // IMPORTANT: enforce tenant ownership via relationship
        Company company = new Company();
        company.setId(companyId);

        entity.setCompany(company);

        validateVehicle(entity);

        Vehicle saved = repository.save(entity);

        return toDto(saved);
    }

    // ----------------------------------------
    // UPDATE
    // ----------------------------------------
    public VehicleResponseDTO updateVehicle(Long id, VehicleRequestDTO dto) {

        Long companyId = currentUserService.getCompanyId();

        Vehicle existing = repository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

        updateEntityFromDto(existing, dto);

        validateVehicle(existing);

        Vehicle saved = repository.save(existing);

        return toDto(saved);
    }

    // ----------------------------------------
    // DELETE
    // ----------------------------------------
    public void deleteVehicle(Long id) {

        Long companyId = currentUserService.getCompanyId();

        Vehicle vehicle = repository.findByIdAndCompanyId(id, companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

        repository.delete(vehicle);
    }

    // ----------------------------------------
    // DTO MAPPING
    // ----------------------------------------
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

        v.setMaintenanceAlertsEnabled(
                dto.getMaintenanceAlertsEnabled() != null
                        ? dto.getMaintenanceAlertsEnabled()
                        : false
        );

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

    // ----------------------------------------
    // VALIDATION
    // ----------------------------------------
    private void validateVehicle(Vehicle vehicle) {

        if (vehicle.getMake() == null || vehicle.getMake().isEmpty() ||
            vehicle.getModel() == null || vehicle.getModel().isEmpty()) {

            throw new ValidationException("Make and model are required");
        }

        int currentYear = LocalDate.now().getYear();

        if (vehicle.getVehicleYear() < 1900 ||
            vehicle.getVehicleYear() > currentYear) {

            throw new ValidationException(
                    "Year must be between 1900 and " + currentYear,
                    "vehicleYear",
                    vehicle.getVehicleYear()
            );
        }

        LocalDate start = vehicle.getStartDate();
        LocalDate end = vehicle.getEndDate();

        if (start == null) {
            throw new ValidationException(
                    "Start date is required",
                    "startDate",
                    null
            );
        }

        if (end != null && end.isBefore(start)) {

            throw new ValidationException(
                    "End date cannot be before start date",
                    "endDate",
                    end
            );
        }
    }
}