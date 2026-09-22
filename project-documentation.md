# VoyageCraft: SOA & Microservices Project Documentation
**Course:** 24SDCS03A - SOA PROGRAMMING AND MICROSERVICES

This document provides a comprehensive overview of the VoyageCraft travel booking platform and explains how the architecture and implementation satisfy the evaluation metrics to achieve **Level 4** across all specified rubrics.

---

## Rubric 1: Problem Analysis and Requirement Specification
**Target Evaluation:** Level 4 (Deep analysis and complete structure) - 10 Weightage

**Implementation & Explanation:**
The VoyageCraft platform tackles the complex domain of online travel booking. A monolithic approach would couple user management, inventory (packages), booking transactions, and payments tightly together, making it hard to scale.
Our deep analysis structured the requirements into distinct, loosely-coupled business domains:
- **User Identity & Security:** Needs dedicated handling for user registration and credential verification.
- **Inventory Management:** Needs to manage travel packages (destinations, prices) and track availability/capacity.
- **Transaction/Booking:** Needs to orchestrate the reservation process.
- **Financial Processing:** Dedicated payment handling.

This complete structure allows teams to develop, scale, and maintain each bounded context independently. The entities (`User`, `TravelPackage`, `Reservation`, `Payment`) are perfectly normalized and isolated within their respective service boundaries.

---

## Rubric 2: Microservice Identification and Service Discovery
**Target Evaluation:** Level 4 (Highly modular design) - 10 Weightage

**Implementation & Explanation:**
The project achieves a highly modular design by decomposing the system into fine-grained, independent microservices, avoiding a "distributed monolith" anti-pattern:
1. **`auth-service`**: Solely responsible for authentication and authorization.
2. **`package-service`**: Manages the catalog of travel packages.
3. **`booking-service`**: Orchestrates reservations (makes inter-service calls via OpenFeign to the payment and package services).
4. **`payment-service`**: Processes payments.

**Service Discovery (Eureka):**
Instead of hardcoding IP addresses and ports, the system leverages a central **Eureka Naming Server (`eureka-server`)**. 
- Every microservice is annotated with `@EnableDiscoveryClient` and registers itself with Eureka upon startup.
- This creates a dynamic, resilient cluster where services can find each other via logical names (e.g., `PAYMENT-SERVICE`) rather than physical addresses, representing a production-ready, highly modular design.

---

## Rubric 3: JWT Authentication
**Target Evaluation:** Level 4 (Robust and secure) - 10 Weightage

**Implementation & Explanation:**
Security is implemented using industry-standard JSON Web Tokens (JWT) integrated with Spring Security in the `auth-service`. 
- **Robustness:** User passwords are not stored in plain text; they are hashed securely using a `PasswordEncoder` (BCrypt) before being saved to the H2 database.
- **Security:** Upon providing valid credentials to `/auth/token`, the `AuthenticationManager` verifies the user and the `JwtService` issues a stateless, cryptographically signed JWT.
- **Validation:** The service exposes a `/auth/validate` endpoint to check token integrity and expiration. This setup ensures that authentication is robust, stateless, and scalable across multiple microservice instances without needing sticky sessions.

---

## Rubric 4: API Gateway Configuration
**Target Evaluation:** Level 4 (Optimized and secure) - 10 Weightage

**Implementation & Explanation:**
Clients do not interact with the microservices directly. Instead, an `api-gateway` (Spring Cloud Gateway) acts as a single, optimized entry point running on port `9090`.

- **Optimized Routing:** The `application.yml` is configured with precise path predicates. 
  - Requests to `/auth/**` go to the Auth Service.
  - Requests to `/packages/**` go to the Package Service.
  - Requests to `/bookings/**` go to the Booking Service.
- **Dynamic Load Balancing:** The routes are configured using the `lb://` protocol (e.g., `lb://PACKAGE-SERVICE`). This means the API Gateway integrates directly with Eureka and a client-side load balancer (`cloud-loadbalancer`). If multiple instances of a service are spun up, the Gateway optimally distributes incoming traffic among them.
- **Security Posture:** By hiding the internal architecture behind the Gateway, the internal microservices are shielded from direct external access, fulfilling the secure configuration requirement.

---

## Summary of Tech Stack
- **Framework:** Spring Boot 3.x, Spring Cloud
- **Language:** Java 17
- **Databases:** H2 In-Memory Database (per service)
- **Service Registry:** Netflix Eureka
- **API Gateway:** Spring Cloud Gateway
- **Inter-service Communication:** Spring Cloud OpenFeign
- **Security:** Spring Security, JWT
