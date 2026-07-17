# Fleet Management API

![Build Status](https://github.com/Dclarke96/Fleet-Management-API/actions/workflows/build.yml/badge.svg)
![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.2-brightgreen)
![OpenAPI](https://img.shields.io/badge/OpenAPI-3.0-green)

## Overview

Fleet Management API is a backend service designed to help businesses track vehicles, manage maintenance records, and maintain operational visibility across their fleet.

The API provides secure, scalable RESTful endpoints for managing fleet data, including vehicle management, maintenance tracking, authentication, authorization, search functionality, pagination, and validation.

The project is designed to demonstrate production-oriented backend development practices, including layered architecture, DTO-based API boundaries, security implementation, centralized exception handling, OpenAPI documentation, and integration testing.

---

## Features

* JWT Authentication
* Company-level data isolation
* Vehicle Management
* Maintenance Tracking
* Search functionality
* Pagination
* Jakarta Validation
* Standardized API responses
* OpenAPI / Swagger documentation
* Integration testing

---

## Tech Stack

* **Language:** Java 17
* **Framework:** Spring Boot 3.4.2
* **Database:** PostgreSQL
* **Validation:** Jakarta Validation
* **Security:** Spring Security + JWT Authentication
* **API Documentation:** SpringDoc OpenAPI / Swagger UI
* **Build Tool:** Gradle
* **Architecture:** Layered Architecture with separated API, business logic, persistence, security, and cross-cutting concerns
* **Testing:** JUnit 5 + Spring Boot Test

---

## Architecture

The backend follows a layered architecture with clear separation between application responsibilities:

```
Controller Layer
        ↓
Service Layer
        ↓
Repository Layer
        ↓
Database
```

Supporting layers include:

* **API Layer** - Provides standardized API responses and pagination models.
* **DTO Layer** - Separates external API contracts from internal persistence models.
* **Security Layer** - Handles JWT authentication, authorization, and current user context.
* **Exception Layer** - Provides centralized exception handling and consistent error responses.
* **Logging Layer** - Provides request logging and traceability.

Key architectural decisions:

* Controllers remain thin and delegate business logic to services.
* Services contain application rules and workflows.
* Repositories manage database access through JPA.
* DTOs protect API contracts from internal entity changes.
* Security concerns are isolated from business logic.

For a detailed overview, see:

* [Architecture Overview](docs/architecture.md)

---

## API Documentation

The API exposes endpoints for managing:

* User authentication
* Vehicles
* Maintenance records

Supported operations include:

* GET
* POST
* PUT
* DELETE
* Search
* Pagination

Detailed endpoint documentation with request and response examples:

* [API Design](docs/api-design.md)

---

## OpenAPI Documentation

Interactive API documentation is available through Swagger UI after starting the application.

### Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

### OpenAPI JSON

```
http://localhost:8080/v3/api-docs
```

Swagger UI allows developers to:

* Browse all available endpoints
* View request and response schemas
* Authenticate using JWT bearer tokens
* Execute API requests directly from the browser

---

## Authentication

The API uses JWT-based authentication.

### Registration

Create a new account:

```
POST /api/auth/register
```

### Login

Authenticate and receive a JWT token:

```
POST /api/auth/login
```

Example authentication header for protected endpoints:

```
Authorization: Bearer <JWT_TOKEN>
```

Protected resources require a valid authentication token.

### Using Swagger UI

1. Register a new user or log in using:

```
POST /api/auth/register
```

or

```
POST /api/auth/login
```

2. Copy the JWT token returned from the login response.

3. Open Swagger UI and click the **Authorize** button.

4. Enter:

```
Bearer <JWT_TOKEN>
```

5. Execute protected endpoints directly from the documentation.

---

## Design Decisions

Major architecture and implementation decisions are documented, including:

* Why layered architecture was selected
* Why DTOs are used
* Authentication and authorization strategy
* Validation approach
* Exception handling design
* Repository query design
* Testing strategy

See:

* [Design Decisions](docs/design-decisions.md)

---

## Project Roadmap

This project has evolved through multiple development phases.

### Version 1.0

Completed:

* Vehicle CRUD operations
* Maintenance CRUD operations
* DTO mapping
* Validation
* Search functionality
* Pagination

### Version 2.0

Completed:

* JWT authentication
* User registration and login
* Role-based security foundation
* Company-level data isolation
* Centralized exception handling
* Production-oriented configuration
* Integration testing coverage
* OpenAPI / Swagger documentation

### Future Ideas

Potential future enhancements:

* Maintenance reminders
* Fleet analytics dashboard
* Reporting
* Driver assignments
* Data import/export
* Notifications
* Additional operational insights

Detailed roadmap:

* [Project Roadmap](docs/project-roadmap.md)

---

# Getting Started

## Prerequisites

Before running the application, ensure you have:

* Java 17
* PostgreSQL database
* Gradle

---

## Environment Configuration

The application uses Spring profiles:

* `local` - Local development environment
* `test` - Automated testing environment
* `prod` - Production environment

Sensitive configuration values should be provided through environment variables.

Required variables:

### Database

```
DB_PASSWORD=<database-password>
```

### JWT

```
JWT_SECRET=<jwt-secret-key>
```

---

## Running the Project

1. Clone the repository:

```bash
git clone https://github.com/Dclarke96/Fleet-Management-API.git
cd Fleet-Management-API
```

2. Configure PostgreSQL and environment variables.

3. Build and run:

```bash
./gradlew bootRun
```

4. The API will start at:

```
http://localhost:8080
```

5. Open Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

6. View OpenAPI JSON:

```
http://localhost:8080/v3/api-docs
```

---

## Running Tests

Execute the automated test suite:

```bash
./gradlew test
```

The project includes integration tests covering:

* Authentication flows
* Authorization rules
* Data integrity scenarios
* Exception handling
* Vehicle workflows
* Maintenance workflows

---