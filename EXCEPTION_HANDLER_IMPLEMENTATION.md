# Professional Global Exception Handler Implementation

## Overview
Created a professional-level global exception handling system for the Fleet Management API that provides structured, consistent error responses across all endpoints.

## Files Created/Modified

### 1. New Exception Classes (in `/exception/` directory)
- **ResourceNotFoundException.java** - Thrown when a requested resource isn't found (404)
- **ValidationException.java** - Thrown when business logic validation fails (400)  
- **DuplicateResourceException.java** - Thrown when creating duplicate resources (409)

### 2. New DTO
- **ErrorResponse.java** (in `/dto/` directory) - Structured error response containing:
  - HTTP status code
  - Error type
  - Error message
  - Request path
  - Timestamp
  - Trace ID (UUID for tracking)
  - Field errors (for validation errors)

### 3. Enhanced GlobalExceptionHandler
Handles the following exception types:
- `ResourceNotFoundException` → 404 NOT_FOUND
- `ValidationException` → 400 BAD_REQUEST
- `DuplicateResourceException` → 409 CONFLICT
- `MethodArgumentNotValidException` → 400 BAD_REQUEST (with field errors)
- `IllegalArgumentException` → 400 BAD_REQUEST
- `NoHandlerFoundException` → 404 NOT_FOUND
- Generic `Exception` → 500 INTERNAL_SERVER_ERROR

Each exception handler:
- Generates a unique trace ID for debugging
- Logs the error appropriately
- Returns structured JSON response
- Includes request path and timestamp

### 4. Updated Services
**VehicleService.java**
- Replaced `IllegalArgumentException` with proper custom exceptions
- `updateVehicle()` and `deleteVehicle()` now throw `ResourceNotFoundException`
- `validateVehicle()` now throws `ValidationException` with field names

**MaintenanceService.java**
- Updated all methods to throw `ResourceNotFoundException` for missing records
- Updated all methods to throw `ValidationException` for validation failures
- Consistent error handling across all CRUD operations

### 5. Updated Controllers
**VehicleController.java**
- Removed try-catch blocks for exception handling
- Exception handlers now manage all errors
- Added HTTP 201 CREATED status for successful POST requests
- Simplified code with cleaner logic flow

**MaintenanceController.java**
- Added HTTP 201 CREATED status for successful POST requests
- Aligned with best practices for REST APIs

## Benefits

### Consistency
- All errors return the same structured JSON format
- Predictable error handling across the API

### Debugging
- Each error includes a unique trace ID for tracking
- Request path and timestamp included
- Detailed logging of all exceptions

### Validation
- Field-level validation errors with specific field names
- Clear business rule violations

### Maintainability
- Centralized exception handling (no scattered try-catch blocks)
- Easy to add new exception types
- Single location for logging configuration

### API Clarity
- Proper HTTP status codes for different error scenarios
- Structured error responses for better client handling
- Clear error messages

## Error Response Examples

### Resource Not Found (404)
```json
{
  "status": 404,
  "error": "Resource Not Found",
  "message": "Vehicle not found with id: '999'",
  "path": "/api/vehicles/999",
  "timestamp": "2026-03-11T10:30:45.123456",
  "traceId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

### Validation Error (400)
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Make and model are required",
  "path": "/api/vehicles",
  "timestamp": "2026-03-11T10:30:45.123456",
  "traceId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

### Field Validation Error (400)
```json
{
  "status": 400,
  "error": "Invalid Request",
  "message": "Request validation failed",
  "path": "/api/vehicles",
  "timestamp": "2026-03-11T10:30:45.123456",
  "traceId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "fieldErrors": [
    {
      "field": "title",
      "message": "must not be blank",
      "rejectedValue": ""
    }
  ]
}
```

## Implementation Notes

- Custom exceptions extend `RuntimeException` for unchecked exception handling
- Trace IDs use UUID for uniqueness and debuggability
- All exceptions are logged with appropriate severity levels
- GlobalExceptionHandler uses `@ControllerAdvice` for centralized handling
- Field errors extracted from Spring's validation framework
