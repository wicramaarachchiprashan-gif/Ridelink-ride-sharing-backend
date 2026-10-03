package com.ridelink.accountservice.dto.request;

import com.ridelink.accountservice.model.Role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data 
public class RoleUpdateRequest {

    @NotNull(message = "Role is required")
    private Role role;

}