# Architecture Overview

## Current State & Target Architecture

The Fleet Management API began with a **classic layered architecture**:

```
Controller → Service → Repository → Database
```

This structure supported rapid development, feature delivery, and early validation of the application domain.

As the project has matured, the architecture is evolving toward a **Clean Architecture / Domain-Centric approach** to improve long-term maintainability, testability, and reusability as a foundation for future applications.

The migration is intentionally incremental. The goal is not to rewrite the application, but to gradually improve boundaries while maintaining working functionality.

---

# Why the Architecture Is Evolving

The transition toward a domain-centric architecture is driven by several goals:

* Protect business logic from framework and infrastructure concerns.
* Reduce coupling between Spring, persistence technologies, and application rules.
* Improve testability of core business behavior.
* Create reusable patterns that can support future applications.
* Establish clearer boundaries as additional capabilities are introduced:

  * Authentication and authorization
  * Background processing
  * Reporting
  * Notifications
  * Additional fleet management domains

---

# Target Architecture (Future State)

The intended architecture follows Clean Architecture principles:

```
src/main/java/com/fleetmanagement/

├── domain/                      
│   ├── model/                   
│   ├── repository/              
│   ├── service/                 
│   └── usecase/                 

├── application/                
│   └── DTOs, orchestration, input/output models

├── infrastructure/              
│   ├── persistence/             
│   ├── web/                     
│   ├── external/               
│   └── config/                  

├── common/                      
│   └── shared utilities, exceptions, constants

└── FleetManagementApplication.java
```

### Target Responsibilities

## Domain Layer

Responsible for:

* Core business rules
* Domain entities
* Business invariants
* Domain-specific behavior

The domain layer should remain independent from:

* Spring Framework
* Database technology
* External services

---

## Application Layer

Responsible for:

* Application workflows
* Use case orchestration
* Input/output models
* Coordinating domain operations

---

## Infrastructure Layer

Responsible for external concerns:

* Database persistence
* REST adapters
* Security configuration
* External integrations
* Framework-specific implementations

---

# Migration Strategy

The migration follows a **Strangler Fig approach**.

Instead of rebuilding the entire application, functionality is migrated incrementally by business capability.

Example migration path:

1. Vehicle management
2. Maintenance management
3. Additional fleet capabilities

Existing layered components remain functional until their replacement is introduced.

This approach reduces risk while allowing architectural improvements over time.

---

# Current Architecture (Version 2.0)

The current implementation follows a layered architecture with additional separation for cross-cutting concerns.

```
API Layer
    |
    ↓
Service Layer
    |
    ↓
Repository Layer
    |
    ↓
Database
```

Supporting components include:

```
DTO Layer
Security Layer
Exception Handling
Logging
Configuration
```

---

## Current Layer Responsibilities

### API Layer

Responsible for:

* Handling HTTP requests and responses
* Request validation
* API contract management
* Mapping requests/responses through DTOs

Examples:

* `VehicleController`
* `MaintenanceController`
* `AuthController`

---

### Service Layer

Responsible for:

* Business workflows
* Application logic
* Coordination between repositories and models
* Transaction boundaries

Examples:

* `VehicleService`
* `MaintenanceService`
* `AuthenticationService`

---

### Persistence Layer

Responsible for:

* Database access
* Entity persistence
* Query operations

Examples:

* `VehicleRepository`
* `MaintenanceRepository`
* `UserRepository`

---

### Cross-Cutting Concerns

The application separates shared technical concerns:

## Security

Responsible for:

* JWT authentication
* Authorization checks
* User context handling

Examples:

* `JwtAuthFilter`
* `JwtService`
* `CurrentUserService`

---

## Exception Handling

Responsible for:

* Consistent API error responses
* Centralized exception management
* Traceability

Examples:

* `GlobalExceptionHandler`
* `ErrorResponse`

---

## Logging

Responsible for:

* Request visibility
* Operational troubleshooting
* Application diagnostics

---

# Entities & Relationships

Current entities represent persistence models mapped to database tables.

Examples:

## Vehicle

Represents fleet vehicle information including:

* Vehicle identification
* Fleet details
* Company ownership relationships

Future architecture iterations may move business rules from persistence entities into dedicated domain models.

---

## MaintenanceRecord

Represents vehicle maintenance history including:

* Maintenance details
* Vehicle relationships
* Operational records

Future domain migration may introduce richer maintenance rules and lifecycle behavior.

---

# Data Transfer Objects (DTOs)

DTOs provide a boundary between external API contracts and internal application models.

Examples:

* `VehicleRequestDTO`
* `VehicleResponseDTO`
* `MaintenanceRequestDTO`
* `MaintenanceResponseDTO`

Benefits:

* Prevent direct exposure of persistence models.
* Allow API contracts to evolve independently.
* Reduce coupling between clients and internal implementation details.

---

# Validation Strategy

Validation currently occurs at multiple levels.

## API Boundary Validation

Uses Jakarta Validation for structural validation:

Examples:

* Required fields
* String length limits
* Input formatting

---

## Application Business Validation

Business rules are currently enforced within service workflows.

Examples:

* Duplicate vehicle checks
* Resource existence checks
* Relationship validation

Future domain migration will move appropriate business rules into domain models and use cases.

---

# Architecture Diagram

Current architecture diagram should represent:

```
Controller
    |
    ↓
Service
    |
    ↓
Repository
    |
    ↓
Database


Supporting:
- DTOs
- Security
- Exception Handling
- Logging
```

Future architecture diagrams should represent the target Clean Architecture structure as migration progresses.
