package com.dylanclarke.FleetManagementAPI.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MaintenanceRequestDTO {

    @NotNull
    @JsonProperty("vehicleId")
    private Long vehicleId;

    @NotBlank
    private String description;

    @NotNull
    private LocalDate date;

    @NotNull
    @Min(0)
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