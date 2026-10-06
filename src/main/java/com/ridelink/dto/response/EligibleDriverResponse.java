package com.ridelink.dto.response;

import com.ridelink.model.Driver;
import com.ridelink.model.Location;
import com.ridelink.model.Vehicle;

import lombok.Data;

@Data
public class EligibleDriverResponse {

    private String driverId;
    private String accountId;
    private String driverName;
    private String phone;
    private String serviceArea;
    private Location currentLocation;
    private String vehicleId;
    private String vehicleType;
    private String make;
    private String model;
    private String licensePlate;
    private Integer seatingCapacity;

    public EligibleDriverResponse(Driver driver, Vehicle vehicle) {
        this.driverId = driver.getId();
        this.accountId = driver.getAccountId();
        this.driverName = driver.getFirstName() + " " + driver.getLastName();
        this.phone = driver.getPhone();
        this.serviceArea = driver.getServiceArea();
        this.currentLocation = driver.getCurrentLocation();

        if (vehicle != null) {
            this.vehicleId = vehicle.getId();
            this.vehicleType = vehicle.getVehicleType() != null ? vehicle.getVehicleType().name() : null;
            this.make = vehicle.getMake();
            this.model = vehicle.getModel();
            this.licensePlate = vehicle.getLicensePlate();
            this.seatingCapacity = vehicle.getSeatingCapacity();
        }
    }
}
