package com.ridelink.dto.response;

import lombok.Data;

@Data
public class AccountValidationResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String role;
    private String status;
}
