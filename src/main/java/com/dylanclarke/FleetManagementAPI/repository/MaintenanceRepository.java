package com.dylanclarke.FleetManagementAPI.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dylanclarke.FleetManagementAPI.model.MaintenanceRecord;
import com.dylanclarke.FleetManagementAPI.model.Vehicle;

import java.util.Optional;

public interface MaintenanceRepository extends JpaRepository<MaintenanceRecord, Long> {

    Page<MaintenanceRecord> findByVehicle(Vehicle vehicle, Pageable pageable);

    Optional<MaintenanceRecord> findByIdAndVehicle(Long id, Vehicle vehicle);

    // NEW: tenant-safe query (THIS IS THE IMPORTANT ONE)
    Page<MaintenanceRecord> findByVehicle_Company_Id(Long companyId, Pageable pageable);

    Optional<MaintenanceRecord> findByIdAndVehicle_Company_Id(Long id, Long companyId);

    Page<MaintenanceRecord> findByVehicle_IdAndVehicle_Company_Id(Long vehicleId, Long companyId, Pageable pageable);

}