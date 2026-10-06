package com.ridelink.dto.response;

import java.time.LocalDateTime;

import com.ridelink.model.PaymentMethod;
import com.ridelink.model.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {

    private String id;
    private String rideId;
    private String riderId;
    private double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime transactionDate;
}
