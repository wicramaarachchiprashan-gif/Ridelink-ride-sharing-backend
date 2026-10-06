package com.ridelink.dto.request;

import com.ridelink.model.PaymentMethod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDto {

    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @NotBlank(message = "Rider ID is required")
    private String riderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
