package com.dylanclarke.FleetManagementAPI.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VehicleRequestDTO {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private String vin;

    private String licensePlate;

    @NotBlank(message = "Make cannot be blank")
    private String make;

    @NotBlank(message = "Model cannot be blank")
    private String model;

    @NotNull(message = "Year cannot be null")
    @JsonProperty("vehicleYear")
    @JsonAlias("vehicleYear")
    private Integer year;

    @NotBlank(message = "Location cannot be blank")
    private String location;

    private Boolean maintenanceAlertsEnabled;

    private LocalDate startDate;

    private LocalDate endDate;

    public VehicleRequestDTO() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getMaintenanceAlertsEnabled() {
        return maintenanceAlertsEnabled;
    }

    public void setMaintenanceAlertsEnabled(Boolean maintenanceAlertsEnabled) {
        this.maintenanceAlertsEnabled = maintenanceAlertsEnabled;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}