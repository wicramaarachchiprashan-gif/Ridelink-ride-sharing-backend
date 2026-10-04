package com.ridelink.farepaymentservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ridelink.farepaymentservice.dto.PaymentRequestDto;
import com.ridelink.farepaymentservice.dto.PaymentResponseDto;
import com.ridelink.farepaymentservice.dto.RideResponseDto;
import com.ridelink.farepaymentservice.exception.PaymentNotFoundException;
import com.ridelink.farepaymentservice.model.Payment;
import com.ridelink.farepaymentservice.model.PaymentStatus;
import com.ridelink.farepaymentservice.repository.PaymentRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    private final String rideServiceUrl;
    private final HttpServletRequest request;

    public PaymentService(
            PaymentRepository paymentRepository,
            RestTemplate restTemplate,
            @Value("${ride.service.url}") String rideServiceUrl,
            HttpServletRequest request) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = restTemplate;
        this.rideServiceUrl = rideServiceUrl;
        this.request = request;
    }

    public double calculateFare(String rideId) {
        // Fetch ride details from Ride Management Service
        String url = rideServiceUrl + "/api/v1/rides/" + rideId;
        
        // Pass the authorization header to the downstream service
        String token = request.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", token);
        }
        
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<RideResponseDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, RideResponseDto.class);
        
        RideResponseDto ride = response.getBody();
        if (ride == null) {
            throw new PaymentNotFoundException("Ride not found for ID: " + rideId);
        }

        // Example calculation: Base fare 5.0 + (Distance * Rate 1.5 per km)
        double baseFare = 5.0;
        double ratePerKm = 1.5;
        return baseFare + (ride.getDistance() * ratePerKm);
    }

    public PaymentResponseDto processPayment(PaymentRequestDto requestDto) {
        double amount = calculateFare(requestDto.getRideId());

        Payment payment = Payment.builder()
                .rideId(requestDto.getRideId())
                .riderId(requestDto.getRiderId())
                .amount(amount)
                .paymentMethod(requestDto.getPaymentMethod())
                .status(PaymentStatus.SUCCESS) // Mocking actual payment gateway
                .transactionDate(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        return mapToDto(savedPayment);
    }

    public PaymentResponseDto getPaymentByRideId(String rideId) {
        Payment payment = paymentRepository.findByRideId(rideId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for ride ID: " + rideId));
        return mapToDto(payment);
    }

    public List<PaymentResponseDto> getPaymentsByRiderId(String riderId) {
        List<Payment> payments = paymentRepository.findByRiderId(riderId);
        return payments.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private PaymentResponseDto mapToDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .rideId(payment.getRideId())
                .riderId(payment.getRiderId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionDate(payment.getTransactionDate())
                .build();
    }
}
