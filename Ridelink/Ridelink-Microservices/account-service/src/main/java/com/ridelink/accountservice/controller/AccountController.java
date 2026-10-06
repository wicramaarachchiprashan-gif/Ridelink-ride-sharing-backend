package com.ridelink.accountservice.controller;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.ridelink.accountservice.dto.request.ChangePasswordRequest;
import com.ridelink.accountservice.dto.request.UpdateProfileRequest;
import com.ridelink.accountservice.dto.response.UserResponse;

import com.ridelink.accountservice.service.AccountService;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

        private final AccountService accountService;

        public AccountController(AccountService accountService) {
                this.accountService = accountService;
        }

        @GetMapping("/profile")
        public ResponseEntity<UserResponse> getMyProfile(
                        Authentication authentication) {

                String email = authentication.getName();

                return ResponseEntity.ok(
                                accountService.getMyProfile(email));
        }

        @GetMapping("/{id}")
        public ResponseEntity<UserResponse> getAccountById(@PathVariable String id) {
                return ResponseEntity.ok(accountService.getAccountById(id));
        }

        @PutMapping("/update")
        public ResponseEntity<UserResponse> updateMyProfile(
                        Authentication authentication,
                        @Valid @RequestBody UpdateProfileRequest request) {

                String email = authentication.getName();

                return ResponseEntity.ok(
                                accountService.updateMyProfile(
                                                email,
                                                request));
        }

        @PutMapping("/changePassword")
        public ResponseEntity<String> changePassword(
                        Authentication authentication,
                        @Valid @RequestBody ChangePasswordRequest request) {

                String email = authentication.getName();

                accountService.changePassword(
                                email,
                                request);

                return ResponseEntity.ok(
                                "Password changed successfully.");
        }
}