package com.ridelink.ridemanagementservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ridelink.ridemanagementservice.dto.RideAssignmentRequest;
import com.ridelink.ridemanagementservice.dto.RideRequestDto;
import com.ridelink.ridemanagementservice.dto.RideResponseDto;
import com.ridelink.ridemanagementservice.dto.RideStatusUpdateRequest;
import com.ridelink.ridemanagementservice.service.RideService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/rides")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    public ResponseEntity<RideResponseDto> requestRide(@Valid @RequestBody RideRequestDto request) {
        RideResponseDto response = rideService.requestRide(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RideResponseDto> getRideById(@PathVariable String id) {
        return ResponseEntity.ok(rideService.getRideById(id));
    }

    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<RideResponseDto>> getRidesByRiderId(@PathVariable String riderId) {
        return ResponseEntity.ok(rideService.getRidesByRiderId(riderId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RideResponseDto>> getRidesByDriverId(@PathVariable String driverId) {
        return ResponseEntity.ok(rideService.getRidesByDriverId(driverId));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<RideResponseDto> assignDriver(
            @PathVariable String id,
            @Valid @RequestBody RideAssignmentRequest request) {
        return ResponseEntity.ok(rideService.assignDriver(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RideResponseDto> updateRideStatus(
            @PathVariable String id,
            @Valid @RequestBody RideStatusUpdateRequest request) {
        return ResponseEntity.ok(rideService.updateRideStatus(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRide(@PathVariable String id) {
        rideService.deleteRide(id);
        return ResponseEntity.noContent().build();
    }
}
