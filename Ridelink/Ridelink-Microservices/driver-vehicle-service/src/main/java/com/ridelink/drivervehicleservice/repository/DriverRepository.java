package com.ridelink.drivervehicleservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ridelink.drivervehicleservice.model.Driver;
import com.ridelink.drivervehicleservice.model.DriverAvailability;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {

    Optional<Driver> findByAccountId(String accountId);

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    boolean existsByAccountId(String accountId);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Driver> findByAvailability(DriverAvailability availability);

    List<Driver> findByAvailabilityAndServiceArea(DriverAvailability availability, String serviceArea);

    List<Driver> findByServiceArea(String serviceArea);
}
