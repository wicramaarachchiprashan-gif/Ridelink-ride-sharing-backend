package com.ridelink.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ridelink.exception.BadRequestException;
import com.ridelink.exception.ConflictException;
import com.ridelink.exception.ResourceNotFoundException;
import com.ridelink.dto.request.DriverAvailabilityRequest;
import com.ridelink.dto.request.DriverLocationRequest;
import com.ridelink.dto.request.DriverRegisterRequest;
import com.ridelink.dto.request.DriverUpdateRequest;
import com.ridelink.dto.response.DriverResponse;
import com.ridelink.dto.response.EligibleDriverResponse;
import com.ridelink.model.Driver;
import com.ridelink.model.DriverAvailability;
import com.ridelink.model.Location;
import com.ridelink.model.Vehicle;
import com.ridelink.model.VehicleType;
import com.ridelink.repository.DriverRepository;
import com.ridelink.repository.VehicleRepository;

@Service
public class DriverService {

    private final DriverRepository driverRepository;
    private final VehicleRepository vehicleRepository;
    private final AccountServiceClient accountServiceClient;

    public DriverService(
            DriverRepository driverRepository,
            VehicleRepository vehicleRepository,
            AccountServiceClient accountServiceClient) {
        this.driverRepository = driverRepository;
        this.vehicleRepository = vehicleRepository;
        this.accountServiceClient = accountServiceClient;
    }

    /**
     * Registers a new driver operational profile.
     */
    public DriverResponse registerDriver(DriverRegisterRequest request) {
        if (driverRepository.existsByAccountId(request.getAccountId())) {
            throw new ConflictException("Driver profile already exists for account ID: " + request.getAccountId());
        }

        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber().trim())) {
            throw new ConflictException("Driver's license number is already registered: " + request.getLicenseNumber());
        }

        // Validate account with Member 1's Account Service
        accountServiceClient.validateDriverAccount(request.getAccountId());

        Driver driver = new Driver();
        driver.setAccountId(request.getAccountId().trim());
        driver.setFirstName(request.getFirstName().trim());
        driver.setLastName(request.getLastName().trim());
        driver.setPhone(request.getPhone().trim());
        driver.setLicenseNumber(request.getLicenseNumber().trim().toUpperCase());
        driver.setServiceArea(request.getServiceArea().trim());
        driver.setAvailability(DriverAvailability.OFFLINE);

        LocalDateTime now = LocalDateTime.now();
        driver.setCreatedAt(now);
        driver.setUpdatedAt(now);

        Driver saved = driverRepository.save(driver);
        return new DriverResponse(saved);
    }

    /**
     * Retrieves a driver by internal Driver ID.
     */
    public DriverResponse getDriverById(String id) {
        Driver driver = findDriverById(id);
        return new DriverResponse(driver);
    }

    /**
     * Retrieves a driver by external Account Service account ID.
     */
    public DriverResponse getDriverByAccountId(String accountId) {
        Driver driver = driverRepository.findByAccountId(accountId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Driver profile not found for account ID: " + accountId));
        return new DriverResponse(driver);
    }

    /**
     * Updates driver's personal/operational details.
     */
    public DriverResponse updateDriverProfile(String id, DriverUpdateRequest request) {
        Driver driver = findDriverById(id);

        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            driver.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            driver.setLastName(request.getLastName().trim());
        }
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            driver.setPhone(request.getPhone().trim());
        }
        if (request.getServiceArea() != null && !request.getServiceArea().isBlank()) {
            driver.setServiceArea(request.getServiceArea().trim());
        }

        driver.setUpdatedAt(LocalDateTime.now());
        Driver updated = driverRepository.save(driver);
        return new DriverResponse(updated);
    }

    /**
     * Updates driver's availability status (AVAILABLE, BUSY, OFFLINE).
     * Rule: Driver cannot go AVAILABLE without a registered vehicle.
     */
    public DriverResponse updateAvailability(String id, DriverAvailabilityRequest request) {
        Driver driver = findDriverById(id);

        if (request.getAvailability() == DriverAvailability.AVAILABLE) {
            if (driver.getVehicleId() == null || driver.getVehicleId().isBlank()) {
                throw new BadRequestException(
                        "Driver cannot be set to AVAILABLE without a registered vehicle. Please register a vehicle first.");
            }
        }

        driver.setAvailability(request.getAvailability());
        driver.setUpdatedAt(LocalDateTime.now());
        Driver updated = driverRepository.save(driver);
        return new DriverResponse(updated);
    }

    /**
     * Updates driver's simulated current GPS location.
     */
    public DriverResponse updateLocation(String id, DriverLocationRequest request) {
        Driver driver = findDriverById(id);

        driver.setCurrentLocation(new Location(request.getLatitude(), request.getLongitude()));
        driver.setUpdatedAt(LocalDateTime.now());
        Driver updated = driverRepository.save(driver);
        return new DriverResponse(updated);
    }

    /**
     * Retrieves all drivers.
     */
    public List<DriverResponse> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(DriverResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves eligible available drivers filtered by service area and optional
     * vehicle type.
     * Core functional requirement for Member 3 (Ride Management Service)
     * integration.
     */
    public List<EligibleDriverResponse> getEligibleAvailableDrivers(String serviceArea, VehicleType vehicleType) {
        List<Driver> availableDrivers;

        if (serviceArea != null && !serviceArea.isBlank()) {
            availableDrivers = driverRepository.findByAvailabilityAndServiceArea(
                    DriverAvailability.AVAILABLE, serviceArea.trim());
        } else {
            availableDrivers = driverRepository.findByAvailability(DriverAvailability.AVAILABLE);
        }

        List<EligibleDriverResponse> result = new ArrayList<>();

        for (Driver driver : availableDrivers) {
            if (driver.getVehicleId() == null) {
                continue;
            }

            Vehicle vehicle = vehicleRepository.findById(driver.getVehicleId()).orElse(null);
            if (vehicle == null) {
                continue;
            }

            if (vehicleType != null && vehicle.getVehicleType() != vehicleType) {
                continue;
            }

            result.add(new EligibleDriverResponse(driver, vehicle));
        }

        return result;
    }

    /**
     * Deletes a driver by internal Driver ID.
     */
    public void deleteDriver(String id) {
        Driver driver = findDriverById(id);
        driverRepository.delete(driver);
    }

    private Driver findDriverById(String id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + id));
    }
}
