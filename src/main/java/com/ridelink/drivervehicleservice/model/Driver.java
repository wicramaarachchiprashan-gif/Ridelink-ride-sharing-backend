package com.ridelink.drivervehicleservice.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "drivers")
public class Driver {

    @Id
    private String id;

    @Indexed(unique = true)
    private String accountId;

    private String firstName;
    private String lastName;
    private String phone;

    @Indexed(unique = true)
    private String licenseNumber;

    private DriverAvailability availability = DriverAvailability.OFFLINE;

    @Indexed
    private String serviceArea;

    private Location currentLocation;

    private String vehicleId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Driver() {
    }
}
