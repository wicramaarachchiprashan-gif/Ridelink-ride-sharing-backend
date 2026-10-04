package com.ridelink.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * Main class of the RideLink Account Service.
 *
 * Spring Boot starts the whole application from here.
 */

@SpringBootApplication
public class AccountServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(
				AccountServiceApplication.class,
				args);
	}
}