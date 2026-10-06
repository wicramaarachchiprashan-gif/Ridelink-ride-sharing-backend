package com.ridelink.ridemanagementservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RideAssignmentRequest {

    @NotBlank(message = "Driver ID is mandatory")
    private String driverId;

    @NotBlank(message = "Vehicle ID is mandatory")
    private String vehicleId;
}
