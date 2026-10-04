package com.ridelink.accountservice.dto.request;

import com.ridelink.accountservice.model.AccountStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data 
public class StatusUpdateRequest {

    @NotNull(message = "Status is required")
    private AccountStatus status;

}