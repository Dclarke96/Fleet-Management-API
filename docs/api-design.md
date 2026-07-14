# API Design

## Overview

The Fleet Management API provides RESTful endpoints for managing fleet vehicles, maintenance records, and user authentication.

The API follows these principles:

* JSON request and response payloads.
* DTO-based API contracts.
* Consistent response envelopes.
* Centralized error handling.
* JWT-based authentication for protected resources.

---

# Authentication

## POST `/api/auth/register`

Creates a new user account and company.

### Request

```json
{
  "username": "fleetadmin",
  "password": "password123",
  "companyName": "Example Fleet Company"
}
```

### Response

```json
{
  "success": true,
  "data": "Registration successful",
  "message": "User registered successfully",
  "timestamp": "2026-07-12T19:00:00"
}
```

---

## POST `/api/auth/login`

Authenticates a user and returns a JWT token.

### Request

```json
{
  "username": "fleetadmin",
  "password": "password123"
}
```

### Response

```json
{
  "success": true,
  "data": "eyJhbGciOiJIUzI1...",
  "message": "Login successful",
  "timestamp": "2026-07-12T19:00:00"
}
```

---

# Authorization

Protected endpoints require:

```
Authorization: Bearer <JWT_TOKEN>
```

---

# Vehicle Endpoints

Base Route:

```
/api/vehicles
```

---

## GET `/api/vehicles`

Retrieves vehicles with pagination.

### Query Parameters

| Parameter | Description                |
| --------- | -------------------------- |
| page      | Page number                |
| size      | Number of records per page |

Example:

```
GET /api/vehicles?page=0&size=10
```

---

## GET `/api/vehicles/{id}`

Retrieves a single vehicle.

### Response

Returns:

`VehicleResponseDTO`

### Errors

* 404 - Vehicle not found

---

## GET `/api/vehicles/search`

Searches vehicles.

Example:

```
GET /api/vehicles/search?q=Ford&page=0&size=10
```

---

## POST `/api/vehicles`

Creates a new vehicle.

### Request

```json
{
  "title": "Truck 1",
  "VIN": "1HGBH41JXMN109186",
  "licensePlate": "ABC123",
  "make": "Ford",
  "model": "F-150",
  "vehicleYear": 2020,
  "location": "Warehouse 1"
}
```

### Response

Returns:

`VehicleResponseDTO`

Status:

```
201 Created
```

---

## PUT `/api/vehicles/{id}`

Updates an existing vehicle.

### Request

`VehicleRequestDTO`

### Errors

* 404 - Vehicle not found
* 400 - Validation failure

---

## DELETE `/api/vehicles/{id}`

Deletes a vehicle.

### Response

Returns:

```json
{
  "success": true,
  "data": null,
  "message": "Vehicle deleted successfully"
}
```

---

# Maintenance Endpoints

Base Route:

```
/api/maintenance
```

---

## GET `/api/maintenance`

Retrieves maintenance records with pagination.

---

## GET `/api/maintenance/{id}`

Retrieves maintenance record by ID.

---

## GET `/api/maintenance/vehicle/{vehicleId}`

Retrieves maintenance history for a vehicle.

Supports pagination.

Example:

```
GET /api/maintenance/vehicle/1?page=0&size=10
```

---

## POST `/api/maintenance`

Creates a maintenance record.

Status:

```
201 Created
```

---

## PUT `/api/maintenance/{id}`

Updates an existing maintenance record.

---

## DELETE `/api/maintenance/{id}`

Deletes a maintenance record.

---

# Standard Success Response

All successful API responses follow:

```json
{
  "success": true,
  "data": {},
  "message": "Operation completed successfully",
  "timestamp": "2026-07-12T19:00:00"
}
```

---

# Standard Error Response

All errors follow:

```json
{
  "status": 404,
  "error": "Resource Not Found",
  "message": "Vehicle not found",
  "path": "/api/vehicles/10",
  "timestamp": "2026-07-12T19:00:00",
  "traceId": "abc-123"
}
```

Common HTTP status codes:

| Status | Meaning                  |
| ------ | ------------------------ |
| 400    | Invalid request          |
| 401    | Authentication required  |
| 403    | Insufficient permissions |
| 404    | Resource not found       |
| 409    | Data conflict            |
| 500    | Unexpected server error  |
