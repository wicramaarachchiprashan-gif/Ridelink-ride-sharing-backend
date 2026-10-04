package com.ridelink.accountservice.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ridelink.accountservice.dto.request.ChangePasswordRequest;
import com.ridelink.accountservice.dto.request.UpdateProfileRequest;
import com.ridelink.accountservice.dto.response.UserResponse;

import com.ridelink.accountservice.exception.BadRequestException;
import com.ridelink.accountservice.exception.ResourceNotFoundException;

import com.ridelink.accountservice.model.User;

import com.ridelink.accountservice.repository.UserRepository;

/*
 * Handles logged-in user's account operations.
 */
@Service
public class AccountService {

        private final UserRepository userRepository;

        private final PasswordEncoder passwordEncoder;

        public AccountService(
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {

                this.userRepository = userRepository;

                this.passwordEncoder = passwordEncoder;
        }

        /*
         * Get the currently logged-in user.
         */
        public UserResponse getMyProfile(
                        String email) {

                User user = findUserByEmail(email);

                return new UserResponse(user);
        }

        /*
         * Update the logged-in user's profile.
         */
        public UserResponse updateMyProfile(
                        String email,
                        UpdateProfileRequest request) {

                User user = findUserByEmail(email);

                user.setFirstName(
                                request.getFirstName().trim());

                user.setLastName(
                                request.getLastName().trim());

                user.setPhone(
                                request.getPhone().trim());

                user.setUpdatedAt(
                                LocalDateTime.now());

                User savedUser = userRepository.save(user);

                return new UserResponse(savedUser);
        }

        /*
         * Change the logged-in user's password.
         */
        public void changePassword(
                        String email,
                        ChangePasswordRequest request) {

                User user = findUserByEmail(email);

                /*
                 * Check current password.
                 */
                boolean currentPasswordCorrect = passwordEncoder.matches(
                                request.getCurrentPassword(),
                                user.getPassword());

                if (!currentPasswordCorrect) {

                        throw new BadRequestException(
                                        "Current password is incorrect.");
                }

                /*
                 * Make sure the new password is different.
                 */
                if (passwordEncoder.matches(
                                request.getNewPassword(),
                                user.getPassword())) {

                        throw new BadRequestException(
                                        "New password must be different from the current password.");
                }

                /*
                 * Hash and save the new password.
                 */
                user.setPassword(
                                passwordEncoder.encode(
                                                request.getNewPassword()));

                user.setUpdatedAt(
                                LocalDateTime.now());

                userRepository.save(user);
        }

        /*
         * Find user by email.
         */
        public User findUserByEmail(
                        String email) {

                return userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User account was not found."));
        }

        /*
         * ADMIN:
         * Return all user accounts.
         */
        public java.util.List<UserResponse> getAllAccounts() {

                return userRepository
                                .findAll()
                                .stream()
                                .map(UserResponse::new)
                                .toList();
        }

        /*
         * ADMIN:
         * Find one account by MongoDB ID.
         */
        public UserResponse getAccountById(
                        String id) {

                User user = userRepository
                                .findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Account not found."));

                return new UserResponse(user);
        }

        /*
         * ADMIN:
         * Change account status.
         */
        public UserResponse updateAccountStatus(
                        String id,
                        com.ridelink.accountservice.model.AccountStatus status) {

                User user = userRepository
                                .findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Account not found."));

                user.setStatus(status);

                user.setUpdatedAt(
                                LocalDateTime.now());

                User savedUser = userRepository.save(user);

                return new UserResponse(savedUser);
        }

        /*
         * ADMIN:
         * Change account role.
         */
        public UserResponse updateAccountRole(
                        String id,
                        com.ridelink.accountservice.model.Role role) {

                User user = userRepository
                                .findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Account not found."));

                user.setRole(role);

                user.setUpdatedAt(
                                LocalDateTime.now());

                User savedUser = userRepository.save(user);

                return new UserResponse(savedUser);
        }
}