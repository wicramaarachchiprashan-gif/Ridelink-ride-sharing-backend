package com.ridelink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ridelink.model.Vehicle;
import com.ridelink.model.VehicleType;

@Repository
public interface VehicleRepository extends MongoRepository<Vehicle, String> {

    Optional<Vehicle> findByDriverId(String driverId);

    Optional<Vehicle> findByLicensePlate(String licensePlate);

    boolean existsByLicensePlate(String licensePlate);

    boolean existsByDriverId(String driverId);

    List<Vehicle> findByVehicleType(VehicleType vehicleType);
}
