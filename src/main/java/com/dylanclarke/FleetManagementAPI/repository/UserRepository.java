package com.dylanclarke.FleetManagementAPI.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dylanclarke.FleetManagementAPI.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
