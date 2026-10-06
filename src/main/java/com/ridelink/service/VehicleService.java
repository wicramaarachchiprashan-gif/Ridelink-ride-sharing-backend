package com.ridelink.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ridelink.exception.ConflictException;
import com.ridelink.exception.ResourceNotFoundException;
import com.ridelink.dto.request.VehicleRegisterRequest;
import com.ridelink.dto.request.VehicleUpdateRequest;
import com.ridelink.dto.response.VehicleResponse;
import com.ridelink.model.Driver;
import com.ridelink.model.DriverAvailability;
import com.ridelink.model.Vehicle;
import com.ridelink.repository.DriverRepository;
import com.ridelink.repository.VehicleRepository;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public VehicleService(
            VehicleRepository vehicleRepository,
            DriverRepository driverRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    /**
     * Registers a new vehicle and links it to the driver.
     */
    public VehicleResponse registerVehicle(VehicleRegisterRequest request) {
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + request.getDriverId()));

        if (vehicleRepository.existsByDriverId(request.getDriverId())) {
            throw new ConflictException(
                    "Driver already has a registered vehicle (Driver ID: " + request.getDriverId() + ")");
        }

        String plate = request.getLicensePlate().trim().toUpperCase();
        if (vehicleRepository.existsByLicensePlate(plate)) {
            throw new ConflictException("Vehicle with license plate already exists: " + plate);
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setDriverId(request.getDriverId());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setMake(request.getMake().trim());
        vehicle.setModel(request.getModel().trim());
        vehicle.setLicensePlate(plate);
        vehicle.setColor(request.getColor().trim());
        vehicle.setSeatingCapacity(request.getSeatingCapacity());

        LocalDateTime now = LocalDateTime.now();
        vehicle.setCreatedAt(now);
        vehicle.setUpdatedAt(now);

        Vehicle saved = vehicleRepository.save(vehicle);

        // Link vehicle ID to the driver
        driver.setVehicleId(saved.getId());
        driver.setUpdatedAt(now);
        driverRepository.save(driver);

        return new VehicleResponse(saved);
    }

    /**
     * Retrieves a vehicle by ID.
     */
    public VehicleResponse getVehicleById(String id) {
        Vehicle vehicle = findVehicleById(id);
        return new VehicleResponse(vehicle);
    }

    /**
     * Retrieves a vehicle by Driver ID.
     */
    public VehicleResponse getVehicleByDriverId(String driverId) {
        Vehicle vehicle = vehicleRepository.findByDriverId(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found for driver ID: " + driverId));
        return new VehicleResponse(vehicle);
    }

    /**
     * Updates vehicle details.
     */
    public VehicleResponse updateVehicle(String id, VehicleUpdateRequest request) {
        Vehicle vehicle = findVehicleById(id);

        if (request.getVehicleType() != null) {
            vehicle.setVehicleType(request.getVehicleType());
        }
        if (request.getMake() != null && !request.getMake().isBlank()) {
            vehicle.setMake(request.getMake().trim());
        }
        if (request.getModel() != null && !request.getModel().isBlank()) {
            vehicle.setModel(request.getModel().trim());
        }
        if (request.getColor() != null && !request.getColor().isBlank()) {
            vehicle.setColor(request.getColor().trim());
        }
        if (request.getSeatingCapacity() != null) {
            vehicle.setSeatingCapacity(request.getSeatingCapacity());
        }

        vehicle.setUpdatedAt(LocalDateTime.now());
        Vehicle updated = vehicleRepository.save(vehicle);
        return new VehicleResponse(updated);
    }

    /**
     * Deletes a vehicle and unlinks it from the driver.
     */
    public void deleteVehicle(String id) {
        Vehicle vehicle = findVehicleById(id);

        // Unlink from driver
        driverRepository.findById(vehicle.getDriverId()).ifPresent(driver -> {
            driver.setVehicleId(null);
            // If driver was AVAILABLE, set them to OFFLINE since they no longer have a
            // vehicle
            if (driver.getAvailability() == DriverAvailability.AVAILABLE) {
                driver.setAvailability(DriverAvailability.OFFLINE);
            }
            driver.setUpdatedAt(LocalDateTime.now());
            driverRepository.save(driver);
        });

        vehicleRepository.delete(vehicle);
    }

    /**
     * Retrieves all vehicles.
     */
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(VehicleResponse::new)
                .collect(Collectors.toList());
    }

    private Vehicle findVehicleById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));
    }
}
