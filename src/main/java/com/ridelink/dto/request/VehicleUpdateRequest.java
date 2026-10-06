package com.ridelink.dto.request;

import com.ridelink.model.VehicleType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class VehicleUpdateRequest {

    private VehicleType vehicleType;
    private String make;
    private String model;
    private String color;

    @Min(value = 1, message = "Seating capacity must be at least 1")
    @Max(value = 50, message = "Seating capacity cannot exceed 50")
    private Integer seatingCapacity;
}
