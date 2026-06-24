# Design Decisions

## Architecture Style
- **Started with Layered Architecture** for rapid development.
- **Evolving to Clean/Domain-Centric Architecture** because:
  - Better isolation of business logic (critical for fleet rules).
  - Easier testing of core domain.
  - Higher reusability as a template for other projects.
  - Reduced coupling to Spring Data JPA, HTTP, etc.

**ADR-001**: Adopt Clean Architecture incrementally (dated: June 2026).

## DTO Usage
- Input/Output DTOs decouple API from domain models.
- Prevents leakage of internal structure and enables future API changes (GraphQL, etc.) with minimal impact.

## Validation Strategy
- **API Layer**: Structural + syntactic validation (`@NotBlank`, `@Size`, etc.).
- **Domain Layer**: Semantic/business rule validation (e.g., VIN format, maintenance scheduling invariants).

## Repository Pattern
- Interfaces defined in Domain/Application layer.
- Concrete implementations (JPA) in Infrastructure.
- This follows Dependency Inversion Principle.

## Service / Use Case Layer
- Business logic is moving into rich Domain models and dedicated Use Cases (e.g., `RegisterVehicleUseCase`).
- Avoids "fat service" anti-pattern common in pure layered designs.