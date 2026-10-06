package com.ridelink.dto.request;

import com.ridelink.model.Role;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleUpdateRequest {

    @NotNull(message = "Role is required")
    private Role role;

}