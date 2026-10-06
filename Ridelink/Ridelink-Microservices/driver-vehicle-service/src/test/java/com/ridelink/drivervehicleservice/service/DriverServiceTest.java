package com.ridelink.drivervehicleservice.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ridelink.drivervehicleservice.dto.DriverAvailabilityRequest;
import com.ridelink.drivervehicleservice.dto.DriverLocationRequest;
import com.ridelink.drivervehicleservice.dto.DriverRegisterRequest;
import com.ridelink.drivervehicleservice.dto.DriverResponse;
import com.ridelink.drivervehicleservice.dto.EligibleDriverResponse;
import com.ridelink.drivervehicleservice.exception.BadRequestException;
import com.ridelink.drivervehicleservice.exception.ConflictException;
import com.ridelink.drivervehicleservice.exception.ResourceNotFoundException;
import com.ridelink.drivervehicleservice.model.Driver;
import com.ridelink.drivervehicleservice.model.DriverAvailability;
import com.ridelink.drivervehicleservice.model.Location;
import com.ridelink.drivervehicleservice.model.Vehicle;
import com.ridelink.drivervehicleservice.model.VehicleType;
import com.ridelink.drivervehicleservice.repository.DriverRepository;
import com.ridelink.drivervehicleservice.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private DriverService driverService;

    private Driver sampleDriver;

    @BeforeEach
    void setUp() {
        sampleDriver = new Driver();
        sampleDriver.setId("driver-101");
        sampleDriver.setAccountId("acc-101");
        sampleDriver.setFirstName("Kamal");
        sampleDriver.setLastName("Perera");
        sampleDriver.setPhone("0771234567");
        sampleDriver.setLicenseNumber("B1234567");
        sampleDriver.setAvailability(DriverAvailability.OFFLINE);
        sampleDriver.setServiceArea("Colombo");
        sampleDriver.setCurrentLocation(new Location(6.9271, 79.8612));
        sampleDriver.setCreatedAt(LocalDateTime.now());
        sampleDriver.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testRegisterDriver_Success() {
        DriverRegisterRequest request = new DriverRegisterRequest();
        request.setAccountId("acc-101");
        request.setFirstName("Kamal");
        request.setLastName("Perera");
        request.setPhone("0771234567");
        request.setLicenseNumber("B1234567");
        request.setServiceArea("Colombo");

        when(driverRepository.existsByAccountId("acc-101")).thenReturn(false);
        when(driverRepository.existsByLicenseNumber("B1234567")).thenReturn(false);
        doNothing().when(accountServiceClient).validateDriverAccount("acc-101");
        when(driverRepository.save(any(Driver.class))).thenReturn(sampleDriver);

        DriverResponse response = driverService.registerDriver(request);

        assertNotNull(response);
        assertEquals("Kamal", response.getFirstName());
        assertEquals("OFFLINE", response.getAvailability());
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    void testRegisterDriver_DuplicateAccount_ThrowsConflictException() {
        DriverRegisterRequest request = new DriverRegisterRequest();
        request.setAccountId("acc-101");

        when(driverRepository.existsByAccountId("acc-101")).thenReturn(true);

        assertThrows(ConflictException.class, () -> driverService.registerDriver(request));
    }

    @Test
    void testRegisterDriver_DuplicateLicense_ThrowsConflictException() {
        DriverRegisterRequest request = new DriverRegisterRequest();
        request.setAccountId("acc-101");
        request.setLicenseNumber("B1234567");

        when(driverRepository.existsByAccountId("acc-101")).thenReturn(false);
        when(driverRepository.existsByLicenseNumber("B1234567")).thenReturn(true);

        assertThrows(ConflictException.class, () -> driverService.registerDriver(request));
    }

    @Test
    void testGetDriverById_Success() {
        when(driverRepository.findById("driver-101")).thenReturn(Optional.of(sampleDriver));

        DriverResponse response = driverService.getDriverById("driver-101");

        assertNotNull(response);
        assertEquals("driver-101", response.getId());
    }

    @Test
    void testGetDriverById_NotFound_ThrowsResourceNotFoundException() {
        when(driverRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> driverService.getDriverById("non-existent"));
    }

    @Test
    void testUpdateAvailability_AvailableWithoutVehicle_ThrowsBadRequestException() {
        sampleDriver.setVehicleId(null);
        when(driverRepository.findById("driver-101")).thenReturn(Optional.of(sampleDriver));

        DriverAvailabilityRequest request = new DriverAvailabilityRequest();
        request.setAvailability(DriverAvailability.AVAILABLE);

        assertThrows(BadRequestException.class, () -> driverService.updateAvailability("driver-101", request));
    }

    @Test
    void testUpdateAvailability_AvailableWithVehicle_Success() {
        sampleDriver.setVehicleId("veh-101");
        when(driverRepository.findById("driver-101")).thenReturn(Optional.of(sampleDriver));
        when(driverRepository.save(any(Driver.class))).thenReturn(sampleDriver);

        DriverAvailabilityRequest request = new DriverAvailabilityRequest();
        request.setAvailability(DriverAvailability.AVAILABLE);

        DriverResponse response = driverService.updateAvailability("driver-101", request);

        assertNotNull(response);
        verify(driverRepository).save(sampleDriver);
    }

    @Test
    void testUpdateLocation_Success() {
        when(driverRepository.findById("driver-101")).thenReturn(Optional.of(sampleDriver));
        when(driverRepository.save(any(Driver.class))).thenReturn(sampleDriver);

        DriverLocationRequest request = new DriverLocationRequest();
        request.setLatitude(6.9300);
        request.setLongitude(79.8700);

        DriverResponse response = driverService.updateLocation("driver-101", request);

        assertNotNull(response);
        assertEquals(6.9300, sampleDriver.getCurrentLocation().getLatitude());
        assertEquals(79.8700, sampleDriver.getCurrentLocation().getLongitude());
    }

    @Test
    void testGetEligibleAvailableDrivers_Success() {
        sampleDriver.setAvailability(DriverAvailability.AVAILABLE);
        sampleDriver.setVehicleId("veh-101");

        Vehicle vehicle = new Vehicle();
        vehicle.setId("veh-101");
        vehicle.setVehicleType(VehicleType.CAR);
        vehicle.setLicensePlate("CAB-1234");
        vehicle.setSeatingCapacity(4);

        when(driverRepository.findByAvailabilityAndServiceArea(DriverAvailability.AVAILABLE, "Colombo"))
                .thenReturn(List.of(sampleDriver));
        when(vehicleRepository.findById("veh-101")).thenReturn(Optional.of(vehicle));

        List<EligibleDriverResponse> eligible = driverService.getEligibleAvailableDrivers("Colombo", VehicleType.CAR);

        assertEquals(1, eligible.size());
        assertEquals("driver-101", eligible.get(0).getDriverId());
        assertEquals("CAB-1234", eligible.get(0).getLicensePlate());
    }
}
