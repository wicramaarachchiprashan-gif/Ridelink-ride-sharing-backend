package com.ridelink.drivervehicleservice.dto;

import java.time.LocalDateTime;

import com.ridelink.drivervehicleservice.model.Driver;
import com.ridelink.drivervehicleservice.model.Location;

import lombok.Data;

@Data
public class DriverResponse {

    private String id;
    private String accountId;
    private String firstName;
    private String lastName;
    private String phone;
    private String licenseNumber;
    private String availability;
    private String serviceArea;
    private Location currentLocation;
    private String vehicleId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DriverResponse(Driver driver) {
        this.id = driver.getId();
        this.accountId = driver.getAccountId();
        this.firstName = driver.getFirstName();
        this.lastName = driver.getLastName();
        this.phone = driver.getPhone();
        this.licenseNumber = driver.getLicenseNumber();
        this.availability = driver.getAvailability() != null ? driver.getAvailability().name() : null;
        this.serviceArea = driver.getServiceArea();
        this.currentLocation = driver.getCurrentLocation();
        this.vehicleId = driver.getVehicleId();
        this.createdAt = driver.getCreatedAt();
        this.updatedAt = driver.getUpdatedAt();
    }
}
