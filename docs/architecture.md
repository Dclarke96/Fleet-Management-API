# Architecture Overview

## Project Structure

The Fleet Management API backend follows a **3-tier layered architecture**:

1. **Controller Layer**  
   - Handles HTTP requests and responses.  
   - Maps incoming requests to service methods.  
   - Controllers:
     - `VehicleController.java` → `/api/vehicles`
     - `MaintenanceController.java` → `/api/maintenance`

2. **Service Layer**  
   - Encapsulates business logic and validation.  
   - Handles relationships between entities.  
   - Services:
     - `VehicleService.java` → CRUD operations, search functionality, DTO mapping  
     - `MaintenanceService.java` → Validation and maintenance management

3. **Repository Layer**  
   - Handles data persistence using Spring Data JPA.  
   - Repositories:
     - `VehicleRepository.java` → Custom search queries
     - `MaintenanceRepository.java` → Maintenance queries by vehicle, ordered by date

## Entities & Relationships

- **Vehicle.java**
  - Fields: `title`, `VIN`, `licensePlate`, `make`, `model`, `vehicleYear`, `location`, maintenance alerts
  - Validation: `@NotBlank`, `@Size`, `@Min`
  - Relationships: One-to-many with `MaintenanceRecord`

- **MaintenanceRecord.java**
  - Tracks maintenance events for vehicles
  - Fields: `description`, `serviceDate`, `alertsEnabled`
  - Relationships: Many-to-one with `Vehicle`

## Data Transfer Objects (DTOs)

- `VehicleRequestDTO.java` → input validation for API requests
- `VehicleResponseDTO.java` → structured API responses

DTOs **decouple API representation from internal entity structure** for better maintainability and flexibility.

## Validation

- Jakarta Validation annotations ensure data integrity before hitting business logic:
  - `@NotBlank` for required fields
  - `@Size` for string length limits
  - `@Min` for numeric minimums

## Architecture Diagram

![Architecture Diagram](docs/assets/architecture.png)