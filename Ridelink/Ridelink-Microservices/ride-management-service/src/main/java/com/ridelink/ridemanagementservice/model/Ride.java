package com.ridelink.ridemanagementservice.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "rides")
public class Ride {

    @Id
    private String id;
    
    private String riderId;
    
    private String driverId;
    
    private String vehicleId;
    
    private Location pickupLocation;
    
    private Location dropoffLocation;
    
    private double distance;
    
    private RideStatus status = RideStatus.REQUESTED;
    
    private LocalDateTime requestedAt;
    
    private LocalDateTime acceptedAt;
    
    private LocalDateTime completedAt;
    
    private LocalDateTime cancelledAt;
}
