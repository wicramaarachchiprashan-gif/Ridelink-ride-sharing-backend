# RideLink - Microservices Version

This project is the first uploaded RideLink ZIP restructured from a modular monolith into independently runnable Spring Boot microservices.

## Architecture

- API Gateway: `8080`
- Account Service: `8081` -> `ridelink_account_db`
- Driver & Vehicle Service: `8082` -> `ridelink_driver_vehicle_db`
- Ride Management Service: `8083` -> `ridelink_ride_db`
- Fare & Payment Service: `8084` -> `ridelink_fare_payment_db`
- MongoDB: `27017`

All services use the same JWT secret so a token issued by Account Service can be validated by the other services.

## Request flow

Use the API Gateway for frontend requests:

- `/api/v1/auth/**` -> Account Service
- `/api/v1/accounts/**` -> Account Service
- `/api/v1/admin/accounts/**` -> Account Service
- `/api/v1/drivers/**` -> Driver & Vehicle Service
- `/api/v1/vehicles/**` -> Driver & Vehicle Service
- `/api/v1/rides/**` -> Ride Management Service
- `/api/v1/payments/**` -> Fare & Payment Service

Base URL for the frontend: `http://localhost:8080`

## Run locally without Docker

1. Start MongoDB on port 27017.
2. From the project root build everything:

   Windows PowerShell:
   `./mvnw.cmd clean package -DskipTests`

3. Open five terminals and run:

   `./mvnw.cmd -pl account-service spring-boot:run`

   `./mvnw.cmd -pl driver-vehicle-service spring-boot:run`

   `./mvnw.cmd -pl ride-management-service spring-boot:run`

   `./mvnw.cmd -pl fare-payment-service spring-boot:run`

   `./mvnw.cmd -pl api-gateway spring-boot:run`

4. Test login/register through `http://localhost:8080/api/v1/auth/...`.

## Swagger

Each business service exposes its own Swagger UI directly:

- Account: `http://localhost:8081/swagger-ui/index.html`
- Driver/Vehicle: `http://localhost:8082/swagger-ui/index.html`
- Ride: `http://localhost:8083/swagger-ui/index.html`
- Payment: `http://localhost:8084/swagger-ui/index.html`

## Docker

First build the JARs:

`./mvnw.cmd clean package -DskipTests`

Then:

`docker compose up --build`

## Important changes from the original ZIP

- Each logical module is now a separate Spring Boot Maven module and process.
- Driver/Vehicle now has its own `DriverVehicleServiceApplication` entry point.
- Ports are separated: 8081/8082/8083/8084.
- Interservice URLs point to the correct services rather than all using 8081.
- Ride and Payment Mongo configuration now uses environment-configurable standard MongoDB URIs instead of hard-coded localhost clients.
- Driver -> Account validation forwards the incoming Authorization header.
- Account Service now provides authenticated `GET /api/v1/accounts/{id}` for Driver Service validation.
- API Gateway on 8080 forwards frontend requests to the correct service.
