package com.ridelink.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ridelink.dto.request.ChangePasswordRequest;
import com.ridelink.dto.request.UpdateProfileRequest;
import com.ridelink.dto.response.UserResponse;

import com.ridelink.exception.BadRequestException;
import com.ridelink.exception.ResourceNotFoundException;

import com.ridelink.model.User;

import com.ridelink.repository.UserRepository;

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

        public UserResponse getMyProfile(
                        String email) {

                User user = findUserByEmail(email);

                return new UserResponse(user);
        }

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

        public void changePassword(
                        String email,
                        ChangePasswordRequest request) {

                User user = findUserByEmail(email);

                boolean currentPasswordCorrect = passwordEncoder.matches(
                                request.getCurrentPassword(),
                                user.getPassword());

                if (!currentPasswordCorrect) {

                        throw new BadRequestException(
                                        "Current password is incorrect.");
                }

                if (passwordEncoder.matches(
                                request.getNewPassword(),
                                user.getPassword())) {

                        throw new BadRequestException(
                                        "New password must be different from the current password.");
                }

                user.setPassword(
                                passwordEncoder.encode(
                                                request.getNewPassword()));

                user.setUpdatedAt(
                                LocalDateTime.now());

                userRepository.save(user);
        }

        public User findUserByEmail(
                        String email) {

                return userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "User account was not found."));
        }

        public java.util.List<UserResponse> getAllAccounts() {

                return userRepository
                                .findAll()
                                .stream()
                                .map(UserResponse::new)
                                .toList();
        }

        public UserResponse getAccountById(
                        String id) {

                User user = userRepository
                                .findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Account not found."));

                return new UserResponse(user);
        }

        public UserResponse updateAccountStatus(
                        String id,
                        com.ridelink.model.AccountStatus status) {

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

        public UserResponse updateAccountRole(
                        String id,
                        com.ridelink.model.Role role) {

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