package com.dylanclarke.FleetManagementAPI.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dylanclarke.FleetManagementAPI.model.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Paginated + sortable search query
    @Query("SELECT v FROM Vehicle v " +
           "WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(v.make) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(v.model) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(v.location) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Vehicle> searchVehicles(@Param("query") String query, Pageable pageable);

    // (Optional) Keep this if you still need non-paginated simple search
    List<Vehicle> findByMakeContainingIgnoreCaseOrModelContainingIgnoreCaseOrTitleContainingIgnoreCase(
            String make, String model, String title);
}