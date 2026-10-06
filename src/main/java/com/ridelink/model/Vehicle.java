package com.ridelink.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "vehicles")
public class Vehicle {

    @Id
    private String id;

    @Indexed
    private String driverId;

    private VehicleType vehicleType;
    private String make;
    private String model;

    @Indexed(unique = true)
    private String licensePlate;

    private String color;
    private Integer seatingCapacity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Vehicle() {
    }
}
