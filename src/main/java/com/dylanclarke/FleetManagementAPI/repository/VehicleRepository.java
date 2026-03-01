package com.dylanclarke.FleetManagementAPI.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dylanclarke.FleetManagementAPI.model.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Search vehicles by partial matches on title, make, model, or location
    @Query("SELECT v FROM Vehicle v " +
           "WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(v.make) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(v.model) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(v.location) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "ORDER BY v.vehicleYear ASC, v.make ASC, v.model ASC")
    List<Vehicle> searchVehicles(@Param("query") String query);
}