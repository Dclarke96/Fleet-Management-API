package com.dylanclarke.FleetManagementAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dylanclarke.FleetManagementAPI.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
