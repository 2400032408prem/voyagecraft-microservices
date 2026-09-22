# VoyageCraft: Complete Microservices Architecture & Testing Manual
**Course:** 24SDCS03A - SOA PROGRAMMING AND MICROSERVICES  
**Frameworks:** Spring Boot 4.x / Spring Cloud 2025 / Java 17  
**Evaluation Target:** Level 4 (Exemplary Mastery Across All Rubrics)  
**Deliverable File:** `VoyageCraft_Complete_Project_Documentation.pdf`

---

## 1. Executive Summary & Problem Analysis

### 1.1 Problem Statement
Modern online travel booking platforms must handle high transaction velocity, concurrent inventory updates, user identity verification, and multi-step payment workflows. Building this system as a monolith causes severe architectural bottlenecks:
* **Tight Database Coupling:** A single shared database schema causes high lock contention and risk of system-wide downtime during schema updates.
* **Cascading Failures:** A failure in payment processing or inventory search brings down the entire application.
* **Deployment Friction:** Minor updates require rebuilding and redeploying the entire platform.

### 1.2 The Microservices Solution
**VoyageCraft** solves these issues by decomposing the domain into 6 modular, independently deployable services following Domain-Driven Design (DDD) and the Database-per-Service pattern.

---

## 2. System Architecture & Topology

```
+-------------------------------------------------------------------------------+
|                                CLIENT / POSTMAN                               |
+---------------------------------------+---------------------------------------+
                                        | HTTP Requests
                                        v
+-------------------------------------------------------------------------------+
|                       API GATEWAY (Port 9090 - WebFlux)                       |
|  * Reactive Non-blocking Routing    * Global CORS Configuration               |
|  * Dynamic Load Balancing (lb://)   * Ingress Security Barrier                |
+---+-------------------+-------------------+-------------------+---------------+
    |                   |                   |                   |
    | /auth/**          | /packages/**      | /bookings/**      | /payments/**
    v                   v                   v                   v
+-------------+   +-------------+   +-------------+   +-------------+
| auth-       |   | package-    |   | booking-    |   | payment-    |
| service     |   | service     |   | service     |   | service     |
| Port: 8081  |   | Port: 8082  |   | Port: 8084  |   | Port: 8083  |
+------+------+   +------+------+   +------+------+   +------+------+
       |                 |                 | (Feign)         ^ (Feign)
       |                 |<----------------+-----------------+
       v                 v                 v                 v
+-------------+   +-------------+   +-------------+   +-------------+
| H2 Database |   | H2 Database |   | H2 Database |   | H2 Database |
| (authdb)    |   | (packagedb) |   | (bookingdb) |   | (paymentdb) |
+-------------+   +-------------+   +-------------+   +-------------+

                                      ^
                                      | Dynamic Registration & Heartbeats
+-------------------------------------+-----------------------------------------+
|                  EUREKA SERVICE REGISTRY (Port 8761)                          |
+-------------------------------------------------------------------------------+
```

### Microservices Summary Table

| Service Name | Port | Database | Primary Responsibility | Key Technologies |
| :--- | :--- | :--- | :--- | :--- |
| **`eureka-server`** | `8761` | N/A | Central Service Registry & Heartbeat Health Monitor | Netflix Eureka Server |
| **`api-gateway`** | `9090` | N/A | Single Ingress, Reactive Route Dispatcher, Global CORS | Spring Cloud Gateway (WebFlux) |
| **`auth-service`** | `8081` | `authdb` | Registration, BCrypt Hashing, JWT Issuance & Verification | Spring Security 6, JJWT 0.12.6, JPA |
| **`package-service`** | `8082` | `packagedb` | Tour Catalog, Capacity Management & Atomic Slot Deduction | Spring Data JPA, H2 In-Memory |
| **`payment-service`** | `8083` | `paymentdb` | Transaction Processing Simulation & Audit Ledger | Spring Data JPA, H2 In-Memory |
| **`booking-service`** | `8084` | `bookingdb` | Reservation State Machine & Inter-Service Orchestration | Spring Cloud OpenFeign, Spring Data JPA |

---

## 3. Deep Dive into Core Technical Modules

### 3.1 Service Discovery (`eureka-server` - Port 8761)
* Eliminates hardcoded IP addresses and ports.
* Every microservice includes `@EnableDiscoveryClient` and registers its application name upon startup.
* Eureka maintains a live registry with 30-second heartbeats.
* API Gateway and OpenFeign clients dynamically resolve target hosts from Eureka using service names (e.g., `lb://PACKAGE-SERVICE`).

### 3.2 Reactive API Gateway (`api-gateway` - Port 9090)
* Built on Spring Cloud Gateway with reactive Project Reactor / Netty non-blocking I/O.
* Configured with global CORS headers allowing cross-origin web/Postman requests.
* Uses Spring Cloud LoadBalancer to distribute requests across microservice instances.

### 3.3 Stateless Security & JWT (`auth-service` - Port 8081)
* **BCrypt Hashing:** Passwords are never stored in plaintext. `BCryptPasswordEncoder` salts and encrypts credentials.
* **Stateless JWT:** Generates cryptographically signed HMAC-SHA256 tokens with 24-hour expiration (`/auth/token`).
* **Token Validation:** Exposes `/auth/validate` to verify signature integrity and check token expiration.

### 3.4 OpenFeign Inter-Service Orchestration (`booking-service` - Port 8084)
When a user books a package:
1. `booking-service` creates a `Reservation` with `PENDING` status.
2. Calls `paymentClient.processPayment(...)` on `PAYMENT-SERVICE` via Feign.
3. If payment succeeds, calls `packageClient.reduceCapacity(...)` on `PACKAGE-SERVICE` via Feign.
4. Marks reservation as `CONFIRMED`. If any step fails, status is marked as `FAILED`.

---

## 4. Complete Step-by-Step Postman Testing Guide

All requests are executed through the **API Gateway at `http://localhost:9090`**.

### Step 1: Register a New User
* **Method:** `POST`
* **URL:** `http://localhost:9090/auth/register`
* **Headers:** `Content-Type: application/json`
* **Body:**
```json
{
  "name": "Alex Mercer",
  "email": "alex@voyagecraft.com",
  "username": "alex",
  "password": "Password123!"
}
```
* **Response (`200 OK`):**
```
User added successfully
```

---

### Step 2: Authenticate & Generate JWT Token
* **Method:** `POST`
* **URL:** `http://localhost:9090/auth/token`
* **Headers:** `Content-Type: application/json`
* **Body:**
```json
{
  "username": "alex",
  "password": "Password123!"
}
```
* **Response (`200 OK`):**
```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGV4IiwiaWF0IjoxNzkwMDUxNDQwLCJleHAiOjE3OTAxMzc4NDB9.DDcptgoxvtkuI94sJnNtPG4qs5HBES-wfI_9RIurCrg
```

---

### Step 3: Validate the JWT Token
* **Method:** `GET`
* **URL:** `http://localhost:9090/auth/validate?token={{jwt_token}}`
* **Response (`200 OK`):**
```
Token is valid
```

---

### Step 4: Create a Travel Package
* **Method:** `POST`
* **URL:** `http://localhost:9090/packages`
* **Headers:** `Content-Type: application/json`
* **Body:**
```json
{
  "destination": "Swiss Alps Adventure",
  "price": 1250.00,
  "capacity": 15
}
```
* **Response (`200 OK`):**
```json
{
  "id": 1,
  "destination": "Swiss Alps Adventure",
  "price": 1250.0,
  "capacity": 15
}
```

---

### Step 5: Get All Packages
* **Method:** `GET`
* **URL:** `http://localhost:9090/packages`
* **Response (`200 OK`):**
```json
[
  {
    "id": 1,
    "destination": "Swiss Alps Adventure",
    "price": 1250.0,
    "capacity": 15
  }
]
```

---

### Step 6: Create an Orchestrated Booking
* **Method:** `POST`
* **URL:** `http://localhost:9090/bookings`
* **Headers:** `Content-Type: application/json`
* **Body:**
```json
{
  "packageId": 1,
  "username": "alex"
}
```
* **Response (`200 OK`):**
```json
{
  "id": 1,
  "packageId": 1,
  "username": "alex",
  "status": "CONFIRMED"
}
```

---

### Step 7: Verify Package Capacity Decrement
* **Method:** `GET`
* **URL:** `http://localhost:9090/packages/1`
* **Response (`200 OK`):**
```json
{
  "id": 1,
  "destination": "Swiss Alps Adventure",
  "price": 1250.0,
  "capacity": 14
}
```
*(Notice capacity reduced from 15 to 14!)*

---

### Step 8: View All Bookings
* **Method:** `GET`
* **URL:** `http://localhost:9090/bookings`
* **Response (`200 OK`):**
```json
[
  {
    "id": 1,
    "packageId": 1,
    "username": "alex",
    "status": "CONFIRMED"
  }
]
```

---

### Step 9: Test Direct Payment Processing
* **Method:** `POST`
* **URL:** `http://localhost:9090/payments/process`
* **Headers:** `Content-Type: application/json`
* **Body:**
```json
{
  "bookingId": 1,
  "amount": 1250.00
}
```
* **Response (`200 OK`):**
```json
{
  "id": 2,
  "bookingId": 1,
  "amount": 1250.0,
  "status": "SUCCESS"
}
```

---

## 5. In-Memory Database Inspection (H2 Consoles)

| Service | Console URL | JDBC URL | User / Password |
| :--- | :--- | :--- | :--- |
| **`auth-service`** | `http://localhost:8081/h2-console` | `jdbc:h2:mem:authdb` | `sa` / `password` |
| **`package-service`** | `http://localhost:8082/h2-console` | `jdbc:h2:mem:packagedb` | `sa` / `password` |
| **`payment-service`** | `http://localhost:8083/h2-console` | `jdbc:h2:mem:paymentdb` | `sa` / `password` |
| **`booking-service`** | `http://localhost:8084/h2-console` | `jdbc:h2:mem:bookingdb` | `sa` / `password` |

---

## 6. How to Run the Entire Project

### Startup Sequence:
1. **Start Eureka Server First (`eureka-server` - Port 8761)**
   * Run `EurekaServerApplication.java`. Check Eureka Dashboard: `http://localhost:8761`.
2. **Start API Gateway (`api-gateway` - Port 9090)**
   * Run `ApiGatewayApplication.java`.
3. **Start Microservices in Any Order:**
   * `AuthServiceApplication.java` (Port 8081)
   * `PackageServiceApplication.java` (Port 8082)
   * `PaymentServiceApplication.java` (Port 8083)
   * `BookingServiceApplication.java` (Port 8084)

---

## 7. Rubric Mapping & Level 4 Compliance (24SDCS03A)

| Rubric | Target | Achievement in VoyageCraft |
| :--- | :--- | :--- |
| **Rubric 1: Problem Analysis & Requirement Specification** | Level 4 (10/10) | Strict Domain-Driven separation into Identity, Inventory, Booking, and Payment domains with normalized isolated schemas. |
| **Rubric 2: Microservice Identification & Service Discovery** | Level 4 (10/10) | Centralized Netflix Eureka registry, dynamic registration via `@EnableDiscoveryClient`, and zero hardcoded IP references. |
| **Rubric 3: JWT Authentication** | Level 4 (10/10) | Spring Security 6 stateless filter chain, BCrypt salt-hashing, cryptographic HMAC-SHA256 JWT tokens, active validation endpoint. |
| **Rubric 4: API Gateway Configuration** | Level 4 (10/10) | Spring Cloud Gateway with reactive WebFlux routing, client-side load balancing (`lb://`), global CORS, and OpenFeign inter-service orchestration. |
