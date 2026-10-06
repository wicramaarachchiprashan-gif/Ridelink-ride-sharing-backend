package com.ridelink.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RideRequestDto {

    @NotBlank(message = "Rider ID is mandatory")
    private String riderId;

    @NotNull(message = "Pickup latitude is mandatory")
    private Double pickupLatitude;

    @NotNull(message = "Pickup longitude is mandatory")
    private Double pickupLongitude;

    @NotBlank(message = "Pickup address is mandatory")
    private String pickupAddress;

    @NotNull(message = "Dropoff latitude is mandatory")
    private Double dropoffLatitude;

    @NotNull(message = "Dropoff longitude is mandatory")
    private Double dropoffLongitude;

    @NotBlank(message = "Dropoff address is mandatory")
    private String dropoffAddress;
}
