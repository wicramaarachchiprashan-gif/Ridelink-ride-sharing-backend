package com.ridelink.farepaymentservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ridelink.farepaymentservice.dto.PaymentRequestDto;
import com.ridelink.farepaymentservice.dto.PaymentResponseDto;
import com.ridelink.farepaymentservice.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Fare & Payment Service", description = "Endpoints for managing fares and payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/fare/{rideId}")
    @Operation(summary = "Calculate fare for a ride")
    public ResponseEntity<Map<String, Object>> calculateFare(@PathVariable String rideId) {
        double fare = paymentService.calculateFare(rideId);
        Map<String, Object> response = new HashMap<>();
        response.put("rideId", rideId);
        response.put("estimatedFare", fare);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Process a payment for a ride")
    public ResponseEntity<PaymentResponseDto> processPayment(@Valid @RequestBody PaymentRequestDto request) {
        PaymentResponseDto response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get payment details by ride ID")
    public ResponseEntity<PaymentResponseDto> getPaymentByRideId(@PathVariable String rideId) {
        return ResponseEntity.ok(paymentService.getPaymentByRideId(rideId));
    }

    @GetMapping("/rider/{riderId}")
    @Operation(summary = "Get payment history for a rider")
    public ResponseEntity<List<PaymentResponseDto>> getPaymentsByRiderId(@PathVariable String riderId) {
        return ResponseEntity.ok(paymentService.getPaymentsByRiderId(riderId));
    }
}
