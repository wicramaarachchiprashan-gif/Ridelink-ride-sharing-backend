package com.ridelink.accountservice.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ridelink.accountservice.dto.request.LoginRequest;
import com.ridelink.accountservice.dto.response.LoginResponse;
import com.ridelink.accountservice.dto.request.RegisterRequest;
import com.ridelink.accountservice.dto.response.UserResponse;

import com.ridelink.accountservice.exception.BadRequestException;

import com.ridelink.accountservice.model.AccountStatus;
import com.ridelink.accountservice.model.Role;
import com.ridelink.accountservice.model.User;

import com.ridelink.accountservice.repository.UserRepository;

import com.ridelink.accountservice.security.JwtService;

/*
 * Handles registration and login business logic.
 */
@Service
public class AuthService {

        private final UserRepository userRepository;

        private final PasswordEncoder passwordEncoder;

        private final JwtService jwtService;

        public AuthService(
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService) {

                this.userRepository = userRepository;

                this.passwordEncoder = passwordEncoder;

                this.jwtService = jwtService;
        }

        /*
         * Register a passenger or driver.
         */
        public UserResponse register(
                        RegisterRequest request) {

                String email = request.getEmail()
                                .trim()
                                .toLowerCase();

                /*
                 * Check duplicate email.
                 */
                if (userRepository.existsByEmail(email)) {

                        throw new BadRequestException(
                                        "An account with this email already exists.");
                }

                /*
                 * ADMIN cannot be created through public registration.
                 *
                 * Admin is created by DataInitializer.
                 */
                if (request.getRole() == Role.ADMIN) {

                        throw new BadRequestException(
                                        "ADMIN accounts cannot be created through public registration.");
                }

                User user = new User();

                user.setFirstName(
                                request.getFirstName().trim());

                user.setLastName(
                                request.getLastName().trim());

                user.setEmail(email);

                user.setPhone(
                                request.getPhone().trim());

                /*
                 * Hash password before saving.
                 */
                user.setPassword(
                                passwordEncoder.encode(
                                                request.getPassword()));

                user.setRole(
                                request.getRole());

                user.setStatus(
                                AccountStatus.ACTIVE);

                LocalDateTime now = LocalDateTime.now();

                user.setCreatedAt(now);

                user.setUpdatedAt(now);

                User savedUser = userRepository.save(user);

                return new UserResponse(savedUser);
        }

        /*
         * Login.
         */
        public LoginResponse login(
                        LoginRequest request) {

                String email = request.getEmail()
                                .trim()
                                .toLowerCase();

                /*
                 * Find user.
                 */
                User user = userRepository
                                .findByEmail(email)
                                .orElseThrow(() -> new BadRequestException(
                                                "Invalid email or password."));

                /*
                 * Check account status.
                 */
                if (user.getStatus() != AccountStatus.ACTIVE) {

                        throw new BadRequestException(
                                        "This account is not active.");
                }

                /*
                 * Compare entered password with
                 * stored BCrypt password.
                 */
                boolean passwordMatches = passwordEncoder.matches(
                                request.getPassword(),
                                user.getPassword());

                if (!passwordMatches) {

                        throw new BadRequestException(
                                        "Invalid email or password.");
                }

                /*
                 * Generate JWT.
                 */
                String token = jwtService.generateToken(user);

                return new LoginResponse(
                                token,
                                jwtService.getExpiration(),
                                new UserResponse(user));
        }
}