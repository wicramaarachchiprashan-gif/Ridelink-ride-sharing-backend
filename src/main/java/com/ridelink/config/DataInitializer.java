package com.ridelink.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.CommandLineRunner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.ridelink.model.AccountStatus;
import com.ridelink.model.Role;
import com.ridelink.model.User;

import com.ridelink.repository.UserRepository;

@Configuration
public class DataInitializer {

        @Bean
        CommandLineRunner createAdmin(
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        @Value("${admin.email}") String adminEmail,
                        @Value("${admin.password}") String adminPassword,
                        @Value("${admin.first-name}") String firstName,
                        @Value("${admin.last-name}") String lastName,
                        @Value("${admin.phone}") String phone) {

                return args -> {

                        if (userRepository
                                        .findByEmail(adminEmail)
                                        .isEmpty()) {

                                User admin = new User();

                                admin.setFirstName(
                                                firstName);

                                admin.setLastName(
                                                lastName);

                                admin.setEmail(
                                                adminEmail.toLowerCase());

                                admin.setPhone(
                                                phone);

                                admin.setPassword(
                                                passwordEncoder.encode(
                                                                adminPassword));

                                admin.setRole(
                                                Role.ADMIN);

                                admin.setStatus(
                                                AccountStatus.ACTIVE);

                                LocalDateTime now = LocalDateTime.now();

                                admin.setCreatedAt(now);

                                admin.setUpdatedAt(now);

                                userRepository.save(admin);

                                System.out.println(
                                                "Default ADMIN account created.");

                        } else {

                                System.out.println(
                                                "ADMIN account already exists.");
                        }
                };
        }
}