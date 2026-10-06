package com.ridelink.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ridelink.dto.request.DriverAvailabilityRequest;
import com.ridelink.dto.request.DriverLocationRequest;
import com.ridelink.dto.request.DriverRegisterRequest;
import com.ridelink.dto.request.DriverUpdateRequest;
import com.ridelink.dto.response.DriverResponse;
import com.ridelink.dto.response.EligibleDriverResponse;
import com.ridelink.model.VehicleType;
import com.ridelink.service.DriverService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    /**
     * Registers a new driver operational profile.
     * Validates account ID with Member 1's Account Service.
     */
    @PostMapping

    public ResponseEntity<DriverResponse> registerDriver(
            @Valid @RequestBody DriverRegisterRequest request) {
        DriverResponse response = driverService.registerDriver(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a driver profile by internal Driver ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriverById(@PathVariable String id) {
        return ResponseEntity.ok(driverService.getDriverById(id));
    }

    /**
     * Retrieves a driver profile by Account Service account ID.
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<DriverResponse> getDriverByAccountId(@PathVariable String accountId) {
        return ResponseEntity.ok(driverService.getDriverByAccountId(accountId));
    }

    /**
     * Updates driver's personal and operational profile.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> updateDriverProfile(
            @PathVariable String id,
            @Valid @RequestBody DriverUpdateRequest request) {
        return ResponseEntity.ok(driverService.updateDriverProfile(id, request));
    }

    /**
     * Updates driver's availability status (AVAILABLE, BUSY, OFFLINE).
     * Cannot set to AVAILABLE without a registered vehicle.
     */
    @PatchMapping("/{id}/availability")
    public ResponseEntity<DriverResponse> updateAvailability(
            @PathVariable String id,
            @Valid @RequestBody DriverAvailabilityRequest request) {
        return ResponseEntity.ok(driverService.updateAvailability(id, request));
    }

    /**
     * Updates driver's simulated current GPS location.
     */
    @PatchMapping("/{id}/location")
    public ResponseEntity<DriverResponse> updateLocation(
            @PathVariable String id,
            @Valid @RequestBody DriverLocationRequest request) {
        return ResponseEntity.ok(driverService.updateLocation(id, request));
    }

    /**
     * Retrieves all registered drivers.
     */
    @GetMapping
    public ResponseEntity<List<DriverResponse>> getAllDrivers() {
        return ResponseEntity.ok(driverService.getAllDrivers());
    }
    @DeleteMapping("/{driverId}")
    public ResponseEntity<Void> deleteDriver(@PathVariable String driverId) {
        driverService.deleteDriver(driverId);
        return ResponseEntity.noContent().build();
    }
    /**
     * Retrieves eligible available drivers (filterable by serviceArea and vehicleType).
     * Primary endpoint for Member 3 (Ride Management Service) to assign drivers to ride requests.
     */
    @GetMapping("/available")
    public ResponseEntity<List<EligibleDriverResponse>> getEligibleAvailableDrivers(
            @RequestParam(required = false) String serviceArea,
            @RequestParam(required = false) VehicleType vehicleType) {
        return ResponseEntity.ok(driverService.getEligibleAvailableDrivers(serviceArea, vehicleType));
    }
}
