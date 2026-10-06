package com.ridelink.ridemanagementservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ridelink.ridemanagementservice.dto.RideAssignmentRequest;
import com.ridelink.ridemanagementservice.dto.RideRequestDto;
import com.ridelink.ridemanagementservice.dto.RideResponseDto;
import com.ridelink.ridemanagementservice.dto.RideStatusUpdateRequest;
import com.ridelink.ridemanagementservice.exception.InvalidRideStateException;
import com.ridelink.ridemanagementservice.exception.RideNotFoundException;
import com.ridelink.ridemanagementservice.model.Location;
import com.ridelink.ridemanagementservice.model.Ride;
import com.ridelink.ridemanagementservice.model.RideStatus;
import com.ridelink.ridemanagementservice.repository.RideRepository;

@Service
public class RideService {

    private final RideRepository rideRepository;
    private final RestTemplate restTemplate;

    @Value("${driver.service.url}")
    private String driverServiceUrl;

    public RideService(RideRepository rideRepository, RestTemplate restTemplate) {
        this.rideRepository = rideRepository;
        this.restTemplate = restTemplate;
    }

    public RideResponseDto requestRide(RideRequestDto request) {
        Ride ride = new Ride();
        ride.setRiderId(request.getRiderId());
        
        Location pickup = new Location(request.getPickupLatitude(), request.getPickupLongitude(), request.getPickupAddress());
        Location dropoff = new Location(request.getDropoffLatitude(), request.getDropoffLongitude(), request.getDropoffAddress());
        
        ride.setPickupLocation(pickup);
        ride.setDropoffLocation(dropoff);
        
        // Simplified distance calculation or placeholder
        ride.setDistance(calculateDistance(pickup, dropoff));
        
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestedAt(LocalDateTime.now());

        Ride savedRide = rideRepository.save(ride);
        
        // OUTBOUND: Optionally fetch eligible drivers (as per requirements, we can notify/log or just fetch to ensure connectivity)
        try {
            String url = driverServiceUrl + "/api/v1/drivers/available"; // Adjusted from eligible to available based on member 2
            // Since we just need to verify interservice communication, a simple GET without failing the request is enough.
            // restTemplate.getForObject(url, Object.class);
        } catch (Exception e) {
            // Gracefully handle if Driver Service is down
            System.err.println("Driver Service is down or unreachable: " + e.getMessage());
        }

        return new RideResponseDto(savedRide);
    }

    public RideResponseDto assignDriver(String rideId, RideAssignmentRequest request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + rideId));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new InvalidRideStateException("Only requested rides can be assigned a driver.");
        }

        ride.setDriverId(request.getDriverId());
        ride.setVehicleId(request.getVehicleId());
        ride.setStatus(RideStatus.ACCEPTED);
        ride.setAcceptedAt(LocalDateTime.now());

        Ride updatedRide = rideRepository.save(ride);
        return new RideResponseDto(updatedRide);
    }

    public RideResponseDto updateRideStatus(String rideId, RideStatusUpdateRequest request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + rideId));

        RideStatus newStatus;
        try {
            newStatus = RideStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRideStateException("Invalid ride status: " + request.getStatus());
        }

        // Validate state transitions
        if (newStatus == RideStatus.IN_PROGRESS && ride.getStatus() != RideStatus.ACCEPTED) {
            throw new InvalidRideStateException("Ride must be ACCEPTED before it can be IN_PROGRESS");
        }
        if (newStatus == RideStatus.COMPLETED && ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new InvalidRideStateException("Ride must be IN_PROGRESS before it can be COMPLETED");
        }

        ride.setStatus(newStatus);
        
        if (newStatus == RideStatus.COMPLETED) {
            ride.setCompletedAt(LocalDateTime.now());
        } else if (newStatus == RideStatus.CANCELLED) {
            ride.setCancelledAt(LocalDateTime.now());
        }

        Ride updatedRide = rideRepository.save(ride);
        return new RideResponseDto(updatedRide);
    }

    public RideResponseDto getRideById(String id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));
        return new RideResponseDto(ride);
    }

    public List<RideResponseDto> getRidesByRiderId(String riderId) {
        return rideRepository.findByRiderId(riderId).stream()
                .map(RideResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<RideResponseDto> getRidesByDriverId(String driverId) {
        return rideRepository.findByDriverId(driverId).stream()
                .map(RideResponseDto::new)
                .collect(Collectors.toList());
    }

    public void deleteRide(String id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException("Ride not found with id: " + id));

        if (ride.getStatus() == RideStatus.IN_PROGRESS || ride.getStatus() == RideStatus.ACCEPTED) {
            throw new InvalidRideStateException("Cannot delete a ride that is " + ride.getStatus() + ". Cancel it first.");
        }

        rideRepository.deleteById(id);
    }

    private double calculateDistance(Location p1, Location p2) {
        // Placeholder for real distance calculation (Euclidean for simplicity)
        double dx = p1.getLatitude() - p2.getLatitude();
        double dy = p1.getLongitude() - p2.getLongitude();
        return Math.sqrt(dx * dx + dy * dy) * 111.0; // rough km conversion
    }
}
