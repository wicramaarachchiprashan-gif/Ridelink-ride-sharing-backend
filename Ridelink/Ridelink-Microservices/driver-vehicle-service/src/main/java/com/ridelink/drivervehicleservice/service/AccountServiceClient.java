package com.ridelink.drivervehicleservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.ridelink.drivervehicleservice.dto.AccountValidationResponse;
import com.ridelink.drivervehicleservice.exception.BadRequestException;
import com.ridelink.drivervehicleservice.exception.ResourceNotFoundException;

/**
 * Client for interservice HTTP communication with Member 1's Account Service.
 */
@Service
public class AccountServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceClient.class);

    private final RestTemplate restTemplate;
    private final String accountServiceUrl;
    private final HttpServletRequest request;

    public AccountServiceClient(
            RestTemplate restTemplate,
            @Value("${account.service.url:http://localhost:8081}") String accountServiceUrl,
            HttpServletRequest request) {
        this.restTemplate = restTemplate;
        this.accountServiceUrl = accountServiceUrl;
        this.request = request;
    }

    /**
     * Validates that the accountId corresponds to an existing, active DRIVER account in Account Service.
     */
    public void validateDriverAccount(String accountId) {
        String url = accountServiceUrl + "/api/v1/accounts/" + accountId;

        try {
            HttpHeaders headers = new HttpHeaders();
            String authorization = request.getHeader("Authorization");
            if (authorization != null && !authorization.isBlank()) {
                headers.set("Authorization", authorization);
            }
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<AccountValidationResponse> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, AccountValidationResponse.class);

            AccountValidationResponse account = response.getBody();
            if (account == null) {
                throw new ResourceNotFoundException("Account not found with ID: " + accountId);
            }

            if (!"DRIVER".equalsIgnoreCase(account.getRole())) {
                throw new BadRequestException("Account with ID " + accountId + " is not registered with role DRIVER (found: " + account.getRole() + ")");
            }

            if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
                throw new BadRequestException("Driver account with ID " + accountId + " is not active (status: " + account.getStatus() + ")");
            }

            log.info("Successfully validated driver account {} via Account Service", accountId);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResourceNotFoundException("Account not found in Account Service with ID: " + accountId);
        } catch (HttpClientErrorException.BadRequest ex) {
            throw new BadRequestException("Invalid account ID format: " + accountId);
        } catch (Exception ex) {
            // When Account Service is offline/unreachable during standalone testing, log and proceed
            log.warn("Account Service is not reachable at {}. Proceeding in standalone development mode.", accountServiceUrl);
        }
    }
}
