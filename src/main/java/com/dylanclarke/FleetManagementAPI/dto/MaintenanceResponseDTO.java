package com.dylanclarke.FleetManagementAPI.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MaintenanceResponseDTO {

    private Long id;

    @JsonProperty("vehicleId")
    private Long vehicleId;

    private String description;
    private LocalDate date;
    private Double cost;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVehicleId() { return vehicleId; }
    public void setVehicleId(Long vehicleId) { this.vehicleId = vehicleId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
}