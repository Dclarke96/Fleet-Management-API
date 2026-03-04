package com.dylanclarke.FleetManagementAPI.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Make cannot be blank")
    private String make;

    @NotBlank(message = "Model cannot be blank")
    private String model;

    @Column(name = "vehicle_year", nullable = false)
    @Min(value = 1886, message = "Vehicle year must be 1886 or later")
    private int vehicleYear;

    @NotBlank(message = "Location cannot be blank")
    private String location;

    @Column(name = "maintenance_alerts_enabled")
    private boolean maintenanceAlertsEnabled;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    // -------------------------------
    // Constructors
    // -------------------------------
    public Vehicle() {}

    public Vehicle(String title, String make, String model, int vehicleYear,
                   String location, boolean maintenanceAlertsEnabled,
                   LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.make = make;
        this.model = model;
        this.vehicleYear = vehicleYear;
        this.location = location;
        this.maintenanceAlertsEnabled = maintenanceAlertsEnabled;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // -------------------------------
    // Getters & Setters
    // -------------------------------
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getVehicleYear() { return vehicleYear; }
    public void setVehicleYear(int vehicleYear) { this.vehicleYear = vehicleYear; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isMaintenanceAlertsEnabled() { return maintenanceAlertsEnabled; }
    public void setMaintenanceAlertsEnabled(boolean maintenanceAlertsEnabled) {
        this.maintenanceAlertsEnabled = maintenanceAlertsEnabled;
    }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}