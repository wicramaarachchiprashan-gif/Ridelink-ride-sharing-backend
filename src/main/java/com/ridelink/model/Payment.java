package com.ridelink.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;
    
    private String rideId;
    
    private String riderId;
    
    private double amount;
    
    private PaymentMethod paymentMethod;
    
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;
    
    private LocalDateTime transactionDate;
}
