# API Design

## Vehicle Endpoints (`/api/vehicles`)

### GET /api/vehicles
- Returns all vehicles
- Response: Array of `VehicleResponseDTO`

### GET /api/vehicles/{id}
- Returns vehicle by ID
- Response: `VehicleResponseDTO`
- Error: 404 if vehicle not found

### POST /api/vehicles
- Creates a new vehicle
- Request Body: `VehicleRequestDTO`
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