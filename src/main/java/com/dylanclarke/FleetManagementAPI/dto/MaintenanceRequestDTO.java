package com.dylanclarke.FleetManagementAPI.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class MaintenanceRequestDTO {

    @NotNull(message = "Vehicle ID is required")
    @JsonProperty("vehicleId")
    private Long vehicleId;

    @NotBlank(message = "Description is required")
    @Size(min = 3, message = "Description must be at least 3 characters")
    private String description;

    @NotNull(message = "Service date is required")
    @FutureOrPresent(message = "Service date cannot be in the past")
    private LocalDate date;

    @NotNull(message = "Cost is required")
    @PositiveOrZero(message = "Cost must be zero or positive")
    private Double cost;

    // Getters and Setters
    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
}