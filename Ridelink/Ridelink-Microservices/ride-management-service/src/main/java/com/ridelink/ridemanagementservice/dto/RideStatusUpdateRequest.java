package com.ridelink.ridemanagementservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RideStatusUpdateRequest {

    @NotBlank(message = "Status is mandatory")
    private String status;
}
