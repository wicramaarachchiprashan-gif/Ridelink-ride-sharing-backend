package com.ridelink.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import com.ridelink.dto.request.RoleUpdateRequest;
import com.ridelink.dto.request.StatusUpdateRequest;
import com.ridelink.dto.response.UserResponse;

import com.ridelink.service.AccountService;

@RestController
@RequestMapping("/api/v1/admin/accounts")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

        private final AccountService accountService;

        public AdminController(
                        AccountService accountService) {

                this.accountService = accountService;
        }

        @GetMapping
        public ResponseEntity<List<UserResponse>> getAllAccounts() {

                return ResponseEntity.ok(
                                accountService.getAllAccounts());
        }

        @GetMapping("/{id}")
        public ResponseEntity<UserResponse> getAccount(
                        @PathVariable String id) {

                return ResponseEntity.ok(
                                accountService.getAccountById(id));
        }

        @PatchMapping("/status/{id}")
        public ResponseEntity<UserResponse> updateStatus(
                        @PathVariable String id,
                        @Valid @RequestBody StatusUpdateRequest request) {

                return ResponseEntity.ok(
                                accountService.updateAccountStatus(
                                                id,
                                                request.getStatus()));
        }

        @PatchMapping("/role/{id}")
        public ResponseEntity<UserResponse> updateRole(
                        @PathVariable String id,
                        @Valid @RequestBody RoleUpdateRequest request) {

                return ResponseEntity.ok(
                                accountService.updateAccountRole(
                                                id,
                                                request.getRole()));
        }
}