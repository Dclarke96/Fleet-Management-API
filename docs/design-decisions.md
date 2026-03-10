# Design Decisions

## Layered Architecture
- **Controller → Service → Repository** separation ensures:
  - Maintainable and testable code
  - Clear separation of business logic from API handling

## DTO Usage
- **VehicleRequestDTO / VehicleResponseDTO**
  - Prevents exposing internal entities
  - Validates input before reaching service layer
  - Standardizes API responses

## Validation
- Jakarta Validation annotations (`@NotBlank`, `@Size`, `@Min`) ensure:
  - Clean, predictable input
  - Reduced chance of invalid data in the database

## Repository Queries
- Custom search queries in `VehicleRepository` allow flexible filtering by title, make, model, and location
- MaintenanceRepository provides ordered retrieval for reporting and alerts

## Service Layer
- Business logic (like relationships between Vehicle and MaintenanceRecord) is encapsulated in services
- Facilitates testing independent of HTTP requests