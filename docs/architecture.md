# Architecture Overview

## Current State & Target Architecture

The Fleet Management API started with a **classic 3-tier layered architecture** (Controller → Service → Repository). This served us well for initial development and getting the app into private testing.

We are now evolving toward a **Clean Architecture (Domain-Centric)** style for better long-term maintainability, testability, and reusability as a template for future projects.

### Why the Evolution?
- Protect core business logic (vehicles, trips, maintenance rules, alerts, etc.) from framework and infrastructure details.
- Make the domain portable for other apps.
- Improve separation of concerns as we add production features (auth, background jobs, reporting, notifications).

### Target Structure (Gradual Migration)
src/main/java/com/fleetmanagement/
├── domain/                  # Core business rules - framework independent
│   ├── model/               # Entities, Value Objects, Aggregates
│   ├── repository/          # Repository interfaces (no JPA here)
│   ├── service/             # Domain services (rich business logic)
│   └── usecase/             # Application services / Use cases
│
├── application/             # Orchestration, DTOs, input/output models
│
├── infrastructure/          # Adapters
│   ├── persistence/         # JPA implementations, repositories
│   ├── web/                 # Controllers, REST adapters
│   ├── external/            # Third-party integrations (GPS, notifications)
│   └── config/              # Spring wiring, security, etc.
│
├── common/                  # Shared utilities, exceptions, constants
└── FleetManagementApplication.java


**Migration Strategy (Strangler Fig)**: We refactor one vertical slice at a time (e.g., Vehicle management first, then Maintenance). Existing layered code remains functional until migrated.

## Current Layers (During Transition)

1. **Web/Controller Layer** – HTTP handling (thin adapters)
2. **Application/Service Layer** – Use cases and orchestration
3. **Domain Layer** – Business entities and rules (growing)
4. **Infrastructure/Persistence Layer** – Data access implementations

## Entities & Relationships

- **Vehicle** (Domain Model): Core entity with business rules (VIN validation, status transitions, maintenance alerts logic).
- **MaintenanceRecord**: Tracks history with invariants (e.g., cannot schedule past dates for active vehicles).

## Data Transfer Objects (DTOs)

- `VehicleRequestDTO` / `VehicleResponseDTO` live in the application/web layer.
- Strict separation: DTOs for API contracts, Domain models for business logic.

## Validation

- Input validation at API boundary (Jakarta Validation).
- Business rule validation inside Domain models / Use Cases.

## Architecture Diagram

architecture.png