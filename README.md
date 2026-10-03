# RideLink - Driver & Vehicle Service (Member 2)

## 1. Overview
The **Driver & Vehicle Service** is an independently deployable microservice developed for the **RideLink** ride-sharing platform as part of the IT3130 Application Development group project.

### Assigned Member Responsibilities (Member 2):
1. **Driver Operational Profile**: Registration, profile viewing, and updating.
2. **Vehicle Management**: Registering vehicle specifications and linking them to drivers.
3. **Availability Management**: Toggling driver availability (`AVAILABLE`, `BUSY`, `OFFLINE`).
4. **Service Area & Location**: Tracking driver operating zones and simulated GPS coordinates.
5. **Driver Retrieval**: Providing eligible, available drivers for ride requests (used by Member 3 - Ride Management Service).
6. **Interservice Integration**: Validating driver account IDs with Member 1's Account Service via REST HTTP calls.

---

## 2. Technology Stack & Architecture

* **Language**: Java 17 (LTS)
* **Framework**: Spring Boot 4.1.1
* **Database**: MongoDB (Spring Data MongoDB)
* **Security**: Spring Security & JJWT 0.13.0 (Stateless JWT Bearer Token Authentication)
* **Build Tool**: Maven 3.9+
* **Testing**: JUnit 5 & Mockito
* **Architecture Pattern**: Microservices Architecture with **Database-per-Service** pattern

---

## 3. Configuration & Ports

| Property | Value | Description |
| :--- | :--- | :--- |
| **Service Port** | `8082` | Dedicated HTTP port for Member 2 |
| **MongoDB Database** | `ridelink_driver_vehicle_db` | Independent database (collections: `drivers`, `vehicles`) |
| **MongoDB URI** | `mongodb://localhost:27017/ridelink_driver_vehicle_db` | Local MongoDB connection |
| **Account Service URL** | `http://localhost:8081` | Member 1 base URL for interservice account validation |
| **JWT Secret** | Shared development secret | Matches Account Service for Bearer token validation |

---

## 4. REST API Specification

### Driver Endpoints (`/api/v1/drivers`)

| Method | Endpoint | Description | Security |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/drivers` | Register driver operational profile | Authenticated |
| `GET` | `/api/v1/drivers/{id}` | Get driver by internal Driver ID | Authenticated |
| `GET` | `/api/v1/drivers/account/{accountId}` | Get driver by Account ID | Authenticated |
| `PUT` | `/api/v1/drivers/{id}` | Update driver profile details | Authenticated |
| `PATCH` | `/api/v1/drivers/{id}/availability` | Update availability (`AVAILABLE`, `BUSY`, `OFFLINE`) | Authenticated |
| `PATCH` | `/api/v1/drivers/{id}/location` | Update simulated GPS location (`latitude`, `longitude`) | Authenticated |
| `GET` | `/api/v1/drivers` | List all drivers | Authenticated |
| `GET` | `/api/v1/drivers/available` | Query available eligible drivers (query params: `serviceArea`, `vehicleType`) | Public / Inter-service |

### Vehicle Endpoints (`/api/v1/vehicles`)

| Method | Endpoint | Description | Security |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/vehicles` | Register vehicle and link to driver | Authenticated |
| `GET` | `/api/v1/vehicles/{id}` | Get vehicle by ID | Authenticated |
| `GET` | `/api/v1/vehicles/driver/{driverId}` | Get vehicle registered to a driver | Authenticated |
| `PUT` | `/api/v1/vehicles/{id}` | Update vehicle specifications | Authenticated |
| `DELETE` | `/api/v1/vehicles/{id}` | Delete vehicle (unlinks driver & sets to OFFLINE) | Authenticated |
| `GET` | `/api/v1/vehicles` | List all vehicles | Authenticated |

### Monitoring

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/actuator/health` | Service and MongoDB health status |
| `GET` | `/actuator/info` | Application metadata |

---

## 5. Business Rules Enforced

1. **One Driver per Account**: A user can only register one driver profile.
2. **Unique License Number**: Driver's license number must be unique across the platform.
3. **Unique Vehicle Plate**: Vehicle license plate must be unique.
4. **One Vehicle per Driver**: Each driver can only have one active vehicle linked at a time.
5. **No Availability without Vehicle**: A driver **cannot** set their status to `AVAILABLE` unless they have a registered vehicle.
6. **Graceful Vehicle Deletion**: Deleting a vehicle automatically removes the vehicle link from the driver and forces their availability to `OFFLINE`.
7. **Consistent Error Responses**: All exceptions return structured JSON:
   ```json
   {
       "status": 400,
       "message": "Validation failed",
       "errors": { "licenseNumber": "Driver's license number is required" }
   }
   ```

---

## 6. Testing with Postman

A ready-to-import Postman Collection is provided in the repository:
* **File:** `RideLink_Driver_Vehicle_Service.postman_collection.json`

### To Run:
1. Open Postman.
2. Click **Import** -> Select `RideLink_Driver_Vehicle_Service.postman_collection.json`.
3. The collection is organized into 5 folders:
   * `0. Health & Actuator`
   * `1. Driver Operations` (Registration, profile, location updates)
   * `2. Vehicle Operations` (Registration, vehicle lookup, updates)
   * `3. Availability & Ride Assignment` (Setting available, querying eligible drivers)
   * `4. Negative Scenarios` (Missing fields, duplicates, available without vehicle, 403 unauthorized)
   * `5. Vehicle Teardown` (Delete vehicle)

---

## 7. Automated Unit Tests

Unit tests are written with JUnit 5 and Mockito:
* `DriverServiceTest`: 9 test cases covering registration, duplicate validation, availability business logic, and eligible driver queries.
* `VehicleServiceTest`: 7 test cases covering vehicle registration, driver linking, duplicate plate checks, and teardown logic.

### To execute unit tests:
```powershell
.\mvnw.cmd test
```
* **Result:** `16 tests run, 0 failures, 0 errors, 0 skipped` (**BUILD SUCCESS**).

---

## 8. How to Run the Service

```powershell
cd driver-vehicle-service
.\mvnw.cmd spring-boot:run
```
The service will start on `http://localhost:8082`.
