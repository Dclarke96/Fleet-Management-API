package com.dylanclarke.FleetManagementAPI.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String make;
    private String model;

    // ✅ FIX: Rename column to avoid SQL reserved keyword conflict
    @Column(name = "vehicle_year", nullable = false)
    private int vehicleYear;

    private String location;

    @Column(name = "maintenance_alerts_enabled")
    private boolean maintenanceAlertsEnabled;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Constructors
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

    // Getters & Setters
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