package com.dylanclarke.FleetManagementAPI.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dylanclarke.FleetManagementAPI.model.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // ----------------------------------------
    // GET ALL BY COMPANY
    // ----------------------------------------
    Page<Vehicle> findByCompanyId(Long companyId, Pageable pageable);

    // ----------------------------------------
    // GET BY ID + COMPANY
    // ----------------------------------------
    Optional<Vehicle> findByIdAndCompanyId(Long id, Long companyId);

    // ----------------------------------------
    // SEARCH BY COMPANY
    // ----------------------------------------
    @Query("""
        SELECT v FROM Vehicle v
        WHERE v.company.id = :companyId
        AND (
            LOWER(v.title) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(v.make) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(v.model) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(v.location) LIKE LOWER(CONCAT('%', :query, '%'))
        )
    """)
    Page<Vehicle> searchVehiclesByCompany(
            @Param("companyId") Long companyId,
            @Param("query") String query,
            Pageable pageable
    );

    // ----------------------------------------
    // OPTIONAL SIMPLE SEARCH
    // ----------------------------------------
    List<Vehicle> findByMakeContainingIgnoreCaseOrModelContainingIgnoreCaseOrTitleContainingIgnoreCase(
            String make,
            String model,
            String title
    );
}