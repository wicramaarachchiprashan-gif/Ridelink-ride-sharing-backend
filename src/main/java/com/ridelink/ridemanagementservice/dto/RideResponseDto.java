package com.ridelink.ridemanagementservice.dto;

import java.time.LocalDateTime;

import com.ridelink.ridemanagementservice.model.Location;
import com.ridelink.ridemanagementservice.model.Ride;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RideResponseDto {

    private String id;
    private String riderId;
    private String driverId;
    private String vehicleId;
    private Location pickupLocation;
    private Location dropoffLocation;
    private double distance;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    public RideResponseDto(Ride ride) {
        this.id = ride.getId();
        this.riderId = ride.getRiderId();
        this.driverId = ride.getDriverId();
        this.vehicleId = ride.getVehicleId();
        this.pickupLocation = ride.getPickupLocation();
        this.dropoffLocation = ride.getDropoffLocation();
        this.distance = ride.getDistance();
        this.status = ride.getStatus().name();
        this.requestedAt = ride.getRequestedAt();
        this.acceptedAt = ride.getAcceptedAt();
        this.completedAt = ride.getCompletedAt();
        this.cancelledAt = ride.getCancelledAt();
    }
}
