package com.ridelink.drivervehicleservice.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ridelink.drivervehicleservice.dto.VehicleRegisterRequest;
import com.ridelink.drivervehicleservice.dto.VehicleResponse;
import com.ridelink.drivervehicleservice.dto.VehicleUpdateRequest;
import com.ridelink.drivervehicleservice.exception.ConflictException;
import com.ridelink.drivervehicleservice.exception.ResourceNotFoundException;
import com.ridelink.drivervehicleservice.model.Driver;
import com.ridelink.drivervehicleservice.model.DriverAvailability;
import com.ridelink.drivervehicleservice.model.Vehicle;
import com.ridelink.drivervehicleservice.model.VehicleType;
import com.ridelink.drivervehicleservice.repository.DriverRepository;
import com.ridelink.drivervehicleservice.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Driver sampleDriver;
    private Vehicle sampleVehicle;

    @BeforeEach
    void setUp() {
        sampleDriver = new Driver();
        sampleDriver.setId("driver-101");
        sampleDriver.setAvailability(DriverAvailability.OFFLINE);

        sampleVehicle = new Vehicle();
        sampleVehicle.setId("veh-101");
        sampleVehicle.setDriverId("driver-101");
        sampleVehicle.setVehicleType(VehicleType.CAR);
        sampleVehicle.setMake("Toyota");
        sampleVehicle.setModel("Prius");
        sampleVehicle.setLicensePlate("CAB-1234");
        sampleVehicle.setColor("White");
        sampleVehicle.setSeatingCapacity(4);
        sampleVehicle.setCreatedAt(LocalDateTime.now());
        sampleVehicle.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testRegisterVehicle_Success() {
        VehicleRegisterRequest request = new VehicleRegisterRequest();
        request.setDriverId("driver-101");
        request.setVehicleType(VehicleType.CAR);
        request.setMake("Toyota");
        request.setModel("Prius");
        request.setLicensePlate("CAB-1234");
        request.setColor("White");
        request.setSeatingCapacity(4);

        when(driverRepository.findById("driver-101")).thenReturn(Optional.of(sampleDriver));
        when(vehicleRepository.existsByDriverId("driver-101")).thenReturn(false);
        when(vehicleRepository.existsByLicensePlate("CAB-1234")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(sampleVehicle);

        VehicleResponse response = vehicleService.registerVehicle(request);

        assertNotNull(response);
        assertEquals("CAB-1234", response.getLicensePlate());
        assertEquals("veh-101", sampleDriver.getVehicleId());
        verify(driverRepository).save(sampleDriver);
    }

    @Test
    void testRegisterVehicle_DriverNotFound_ThrowsResourceNotFoundException() {
        VehicleRegisterRequest request = new VehicleRegisterRequest();
        request.setDriverId("non-existent");

        when(driverRepository.findById("non-existent")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.registerVehicle(request));
    }

    @Test
    void testRegisterVehicle_DriverAlreadyHasVehicle_ThrowsConflictException() {
        VehicleRegisterRequest request = new VehicleRegisterRequest();
        request.setDriverId("driver-101");

        when(driverRepository.findById("driver-101")).thenReturn(Optional.of(sampleDriver));
        when(vehicleRepository.existsByDriverId("driver-101")).thenReturn(true);

        assertThrows(ConflictException.class, () -> vehicleService.registerVehicle(request));
    }

    @Test
    void testRegisterVehicle_DuplicateLicensePlate_ThrowsConflictException() {
        VehicleRegisterRequest request = new VehicleRegisterRequest();
        request.setDriverId("driver-101");
        request.setLicensePlate("CAB-1234");

        when(driverRepository.findById("driver-101")).thenReturn(Optional.of(sampleDriver));
        when(vehicleRepository.existsByDriverId("driver-101")).thenReturn(false);
        when(vehicleRepository.existsByLicensePlate("CAB-1234")).thenReturn(true);

        assertThrows(ConflictException.class, () -> vehicleService.registerVehicle(request));
    }

    @Test
    void testGetVehicleById_Success() {
        when(vehicleRepository.findById("veh-101")).thenReturn(Optional.of(sampleVehicle));

        VehicleResponse response = vehicleService.getVehicleById("veh-101");

        assertNotNull(response);
        assertEquals("veh-101", response.getId());
    }

    @Test
    void testUpdateVehicle_Success() {
        when(vehicleRepository.findById("veh-101")).thenReturn(Optional.of(sampleVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(sampleVehicle);

        VehicleUpdateRequest request = new VehicleUpdateRequest();
        request.setColor("Black");
        request.setSeatingCapacity(5);

        VehicleResponse response = vehicleService.updateVehicle("veh-101", request);

        assertNotNull(response);
        assertEquals("Black", sampleVehicle.getColor());
        assertEquals(5, sampleVehicle.getSeatingCapacity());
    }

    @Test
    void testDeleteVehicle_Success_UnlinksDriver() {
        sampleDriver.setVehicleId("veh-101");
        sampleDriver.setAvailability(DriverAvailability.AVAILABLE);

        when(vehicleRepository.findById("veh-101")).thenReturn(Optional.of(sampleVehicle));
        when(driverRepository.findById("driver-101")).thenReturn(Optional.of(sampleDriver));

        vehicleService.deleteVehicle("veh-101");

        verify(vehicleRepository).delete(sampleVehicle);
        assertNull(sampleDriver.getVehicleId());
        assertEquals(DriverAvailability.OFFLINE, sampleDriver.getAvailability());
        verify(driverRepository).save(sampleDriver);
    }
}
