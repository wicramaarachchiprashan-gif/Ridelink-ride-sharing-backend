package com.ridelink.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ridelink.model.Ride;
import com.ridelink.model.RideStatus;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {

    List<Ride> findByRiderId(String riderId);

    List<Ride> findByDriverId(String driverId);

    List<Ride> findByStatus(RideStatus status);
}
