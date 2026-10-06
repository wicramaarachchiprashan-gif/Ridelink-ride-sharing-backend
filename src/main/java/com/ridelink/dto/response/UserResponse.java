package com.ridelink.dto.response;

import com.ridelink.model.User;

import lombok.Data;

@Data
public class UserResponse {

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String role;

    private String status;

    public UserResponse(User user) {

        this.id = user.getId();

        this.firstName = user.getFirstName();

        this.lastName = user.getLastName();

        this.email = user.getEmail();

        this.phone = user.getPhone();

        this.role = user.getRole().name();

        this.status = user.getStatus().name();
    }

}