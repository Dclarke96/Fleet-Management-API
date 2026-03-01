package com.dylanclarke.FleetManagementAPI.service;

import com.dylanclarke.FleetManagementAPI.model.Vehicle;
import com.dylanclarke.FleetManagementAPI.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    // Get all vehicles
    public List<Vehicle> getAllVehicles() {
        return repository.findAll();
    }

    // Get vehicle by ID
    public Optional<Vehicle> getVehicleById(Long id) {
        return repository.findById(id);
    }

    // Search vehicles
    public List<Vehicle> searchVehicles(String query) {
        return repository.searchVehicles(query);
    }

    // Add vehicle with validation
    public Vehicle addVehicle(Vehicle vehicle) throws IllegalArgumentException {
        validateVehicle(vehicle);
        return repository.save(vehicle);
    }

    // Update vehicle
    public Vehicle updateVehicle(Long id, Vehicle vehicle) throws IllegalArgumentException {
        Optional<Vehicle> existing = repository.findById(id);
        if (existing.isEmpty()) throw new IllegalArgumentException("Vehicle not found");

        validateVehicle(vehicle);
        vehicle.setId(existing.get().getId());
        return repository.save(vehicle);
    }

    // Delete vehicle
    public void deleteVehicle(Long id) throws IllegalArgumentException {
        Optional<Vehicle> vehicle = repository.findById(id);
        if (vehicle.isEmpty()) throw new IllegalArgumentException("Vehicle not found");

        // Optional: Check if there are associated MaintenanceRecords
        repository.deleteById(id);
    }

    // ----------------------------------------------------
    // Validation (mirrors Android repository logic)
    // ----------------------------------------------------
    private void validateVehicle(Vehicle vehicle) {
        if (vehicle.getMake() == null || vehicle.getMake().isEmpty() ||
            vehicle.getModel() == null || vehicle.getModel().isEmpty() ||
            vehicle.getLocation() == null || vehicle.getLocation().isEmpty()) {
            throw new IllegalArgumentException("Make, model, and location are required");
        }

        int currentYear = LocalDate.now().getYear();
        if (vehicle.getVehicleYear() < 1900 || vehicle.getVehicleYear() > currentYear) {
            throw new IllegalArgumentException("Year must be between 1900 and " + currentYear);
        }

        LocalDate start = vehicle.getStartDate();
        LocalDate end = vehicle.getEndDate();
        if (start == null) throw new IllegalArgumentException("Start date is required");
        if (end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
    }
}