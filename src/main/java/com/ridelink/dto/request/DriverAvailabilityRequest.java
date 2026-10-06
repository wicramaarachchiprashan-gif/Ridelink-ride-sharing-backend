package com.ridelink.dto.request;

import com.ridelink.model.DriverAvailability;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DriverAvailabilityRequest {

    @NotNull(message = "Availability status is required (AVAILABLE, BUSY, OFFLINE)")
    private DriverAvailability availability;
}
