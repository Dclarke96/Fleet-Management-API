package com.dylanclarke.FleetManagementAPI.service;

import com.dylanclarke.FleetManagementAPI.model.MaintenanceRecord;
import com.dylanclarke.FleetManagementAPI.model.Vehicle;
import com.dylanclarke.FleetManagementAPI.repository.MaintenanceRepository;
import com.dylanclarke.FleetManagementAPI.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@SuppressWarnings("null")
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository, VehicleRepository vehicleRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public List<MaintenanceRecord> getAllMaintenance() {
        return maintenanceRepository.findAll();
    }

    public List<MaintenanceRecord> getMaintenanceForVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        return maintenanceRepository.findByVehicleOrderByServiceDateAsc(vehicle);
    }

    public MaintenanceRecord getMaintenanceById(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance record not found"));
    }

    public MaintenanceRecord addMaintenance(MaintenanceRecord record, Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        validateRecord(record, vehicle);
        record.setVehicle(vehicle);
        return maintenanceRepository.save(record);
    }

    public MaintenanceRecord updateMaintenance(Long id, MaintenanceRecord updatedRecord) {
        MaintenanceRecord existing = maintenanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance record not found"));

        validateRecord(updatedRecord, existing.getVehicle());

        existing.setDescription(updatedRecord.getDescription());
        existing.setServiceDate(updatedRecord.getServiceDate());
        existing.setAlertsEnabled(updatedRecord.isAlertsEnabled());

        return maintenanceRepository.save(existing);
    }

    public void deleteMaintenance(Long id) {
        maintenanceRepository.deleteById(id);
    }

    // -----------------------------------------------------------
    // Validation logic similar to client
    // -----------------------------------------------------------
    private void validateRecord(MaintenanceRecord record, Vehicle vehicle) {
        if (record.getDescription() == null || record.getDescription().trim().length() < 3) {
            throw new IllegalArgumentException("Description must be at least 3 characters");
        }

        LocalDate serviceDate = record.getServiceDate();
        if (serviceDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Service date cannot be in the past");
        }

        if (serviceDate.isBefore(vehicle.getStartDate())) {
            throw new IllegalArgumentException("Service date cannot be before vehicle start date");
        }

        if (vehicle.getEndDate() != null && serviceDate.isAfter(vehicle.getEndDate())) {
            throw new IllegalArgumentException("Service date cannot be after vehicle end date");
        }
    }
}