package com.ridelink.drivervehicleservice.dto;

import com.ridelink.drivervehicleservice.model.DriverAvailability;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DriverAvailabilityRequest {

    @NotNull(message = "Availability status is required (AVAILABLE, BUSY, OFFLINE)")
    private DriverAvailability availability;
}
