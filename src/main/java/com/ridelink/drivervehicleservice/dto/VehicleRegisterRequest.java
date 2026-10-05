package com.ridelink.drivervehicleservice.dto;

import com.ridelink.drivervehicleservice.model.VehicleType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleRegisterRequest {

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @NotNull(message = "Vehicle type is required (CAR, VAN, BIKE, THREE_WHEELER)")
    private VehicleType vehicleType;

    @NotBlank(message = "Vehicle make is required (e.g. Toyota)")
    private String make;

    @NotBlank(message = "Vehicle model is required (e.g. Prius)")
    private String model;

    @NotBlank(message = "License plate number is required (e.g. CAB-1234)")
    private String licensePlate;

    @NotBlank(message = "Vehicle color is required")
    private String color;

    @NotNull(message = "Seating capacity is required")
    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Max(value = 50, message = "Seating capacity cannot exceed 50")
    private Integer seatingCapacity;
}
