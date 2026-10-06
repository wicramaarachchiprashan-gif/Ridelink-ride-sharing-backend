package com.ridelink.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DriverUpdateRequest {

    @Size(max = 20)
    private String firstName;

    @Size(max = 20)
    private String lastName;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
    private String phone;

    private String serviceArea;
}
