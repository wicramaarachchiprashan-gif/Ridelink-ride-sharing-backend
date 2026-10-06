package com.ridelink.dto.response;

import java.time.LocalDateTime;

import com.ridelink.model.Vehicle;

import lombok.Data;

@Data
public class VehicleResponse {

    private String id;
    private String driverId;
    private String vehicleType;
    private String make;
    private String model;
    private String licensePlate;
    private String color;
    private Integer seatingCapacity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public VehicleResponse(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.driverId = vehicle.getDriverId();
        this.vehicleType = vehicle.getVehicleType() != null ? vehicle.getVehicleType().name() : null;
        this.make = vehicle.getMake();
        this.model = vehicle.getModel();
        this.licensePlate = vehicle.getLicensePlate();
        this.color = vehicle.getColor();
        this.seatingCapacity = vehicle.getSeatingCapacity();
        this.createdAt = vehicle.getCreatedAt();
        this.updatedAt = vehicle.getUpdatedAt();
    }
}
