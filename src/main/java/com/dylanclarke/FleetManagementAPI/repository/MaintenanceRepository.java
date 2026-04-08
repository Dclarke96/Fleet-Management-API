package com.dylanclarke.FleetManagementAPI.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dylanclarke.FleetManagementAPI.model.MaintenanceRecord;
import com.dylanclarke.FleetManagementAPI.model.Vehicle;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceRecord, Long> {

    // Paginated + sortable query
    Page<MaintenanceRecord> findByVehicle(Vehicle vehicle, Pageable pageable);
}