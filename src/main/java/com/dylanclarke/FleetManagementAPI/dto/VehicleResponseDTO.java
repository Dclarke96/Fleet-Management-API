package com.dylanclarke.FleetManagementAPI.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VehicleResponseDTO {

    private Long id;

    private String title;

    private String vin;

    private String licensePlate;

    private String make;

    private String model;

    @JsonProperty("vehicleYear")
    @JsonAlias("vehicleYear")
    private Integer vehicleYear;

    private String location;

    private Boolean maintenanceAlertsEnabled;

    private LocalDate startDate;

    private LocalDate endDate;

    public VehicleResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getVehicleYear() {
        return vehicleYear;
    }

    public void setVehicleYear(Integer vehicleYear) {
        this.vehicleYear = vehicleYear;
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