# Design Decisions

## Architecture Style

### Decision

The Fleet Management API started with a traditional layered architecture:

```
Controller → Service → Repository → Database
```

This approach was selected initially because it supported rapid development, clear separation of responsibilities, and efficient feature delivery during early application development.

As the application matured, the architecture began evolving toward a **Clean Architecture / Domain-Centric approach**.

### Reasoning

The architectural evolution is driven by:

* Better isolation of business rules from frameworks and infrastructure.
* Improved testability of core application behavior.
* Reduced coupling to Spring, HTTP, and persistence technologies.
* Creation of reusable architectural patterns for future projects.

### ADR-001: Incremental Clean Architecture Migration

**Decision:** Adopt Clean Architecture principles incrementally rather than rewriting the application.

**Reasoning:**

A complete rewrite would introduce unnecessary risk and delay feature delivery.

The project follows a strangler-style migration approach:

* Maintain working functionality.
* Improve boundaries gradually.
* Refactor individual business capabilities when appropriate.

---

# DTO Usage

## Decision

Use Data Transfer Objects (DTOs) as the boundary between API contracts and internal application models.

Examples:

* `VehicleRequestDTO`
* `VehicleResponseDTO`
* `MaintenanceRequestDTO`
* `MaintenanceResponseDTO`

## Reasoning

DTOs provide:

* Protection of internal persistence models.
* Stable API contracts.
* Flexibility for future API changes.
* Reduced coupling between external consumers and internal implementation.

The current implementation separates API models from persistence entities.

Future architecture iterations may introduce additional separation between DTOs, application models, and domain models.

---

# Validation Strategy

## Decision

Validation is performed at multiple application boundaries.

## API Boundary Validation

Jakarta Validation is used for structural input validation.

Examples:

* Required fields
* String length requirements
* Input formatting rules

Examples:

```java
@NotBlank
@Size
@Pattern
```

## Application Business Validation

Business rules are currently enforced within service workflows.

Examples:

* Preventing duplicate resources.
* Verifying resource existence.
* Validating relationships between entities.

As domain models are introduced, appropriate business rules may move into domain entities or use cases.

---

# Repository Pattern

## Decision

The current application uses Spring Data repositories to manage persistence operations.

Examples:

* `VehicleRepository`
* `MaintenanceRepository`
* `UserRepository`

## Current State

Repositories currently combine:

* Repository abstraction
* JPA persistence implementation

This approach provides efficient database access while maintaining a clear service/repository separation.

## Future Direction

The target Clean Architecture design separates:

```
Domain Repository Interface

        ↓

Infrastructure Persistence Implementation
```

This would further reduce coupling between business logic and database technology.

---

# Service and Use Case Layer

## Decision

Application logic is currently organized within service classes.

Examples:

* `VehicleService`
* `MaintenanceService`
* `AuthenticationService`

## Current Responsibility

Services currently handle:

* Application workflows.
* Business validation.
* Repository coordination.
* Transaction boundaries.

## Future Direction

As domain complexity increases, selected workflows may migrate toward dedicated use cases and richer domain models.

Example:

```
RegisterVehicleUseCase
CreateMaintenanceRecordUseCase
```

This allows complex business behavior to exist closer to the domain while keeping application orchestration separate.
