package com.ridelink.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DriverRegisterRequest {

    @NotBlank(message = "Account ID is required")
    private String accountId;

    @NotBlank(message = "First name is required")
    @Size(max = 20)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 20)
    private String lastName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    @NotBlank(message = "Driver's license number is required")
    private String licenseNumber;

    @NotBlank(message = "Service area is required")
    private String serviceArea;
}
