package com.ridelink.ridemanagementservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ridelink.ridemanagementservice.model.Ride;
import com.ridelink.ridemanagementservice.model.RideStatus;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {

    List<Ride> findByRiderId(String riderId);
    
    List<Ride> findByDriverId(String driverId);
    
    List<Ride> findByStatus(RideStatus status);
}
