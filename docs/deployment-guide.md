# Deployment Guide

## Overview

This guide describes the production deployment requirements for the Fleet Management API.

The application is designed around externalized configuration, environment-specific Spring profiles, containerized deployment, and health monitoring to support modern cloud hosting platforms.

---

## Production Requirements

Before deploying the application, ensure the following requirements are met.

### Java

- Java 17

### Database

- PostgreSQL
- Existing database instance
- Database user with appropriate permissions

### Build Tool

- Gradle (or Gradle Wrapper)

### Container Runtime (Optional)

- Docker
- Docker Compose

---

## Spring Profiles

The application supports multiple runtime profiles.

| Profile | Purpose |
|----------|---------|
| local | Local development |
| test | Automated testing |
| docker | Docker Compose development |
| prod | Production deployment |

Production deployments should use:

```
SPRING_PROFILES_ACTIVE=prod
```

---

## Required Environment Variables

The application uses environment variables for sensitive configuration.

| Variable | Description |
|----------|-------------|
| DB_URL | PostgreSQL JDBC connection string |
| DB_USERNAME | Database username |
| DB_PASSWORD | Database password |
| JWT_SECRET | Secret used to sign JWT tokens |

Example:

```text
SPRING_PROFILES_ACTIVE=prod

DB_URL=jdbc:postgresql://hostname:5432/fleetdb
DB_USERNAME=fleetuser
DB_PASSWORD=change-me
JWT_SECRET=replace-with-a-long-random-secret
```

---

## Building the Application

Build the project using the Gradle Wrapper.

```bash
./gradlew clean build
```

The executable JAR will be generated in:

```
build/libs/
```

---

## Running the Application

Run the application:

```bash
java -jar build/libs/FleetManagementAPI.jar
```

or configure your hosting platform to execute the generated Spring Boot JAR.

---

## Docker Deployment

Build and start the application:

```bash
docker compose up --build
```

The Docker Compose configuration starts:

- Fleet Management API
- PostgreSQL

The API will be available on:

```
http://localhost:8080
```

---

## Health Monitoring

Spring Boot Actuator provides application health monitoring.

Health endpoint:

```
GET /actuator/health
```

Example:

```
http://localhost:8080/actuator/health
```

Deployment platforms can use this endpoint for health checks and readiness verification.

---

## API Documentation

Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```
http://localhost:8080/v3/api-docs
```

---

## Logging

The application uses structured request logging.

Each request receives a unique trace identifier that is included in request logs and error responses to simplify troubleshooting.

Sensitive information such as passwords and JWT tokens is intentionally excluded from application logs.

---

## Production Checklist

Before releasing a deployment, verify:

- Spring profile set to `prod`
- Environment variables configured
- Database connectivity confirmed
- Health endpoint responding
- Swagger documentation accessible (if enabled)
- Application builds successfully
- Automated tests pass
- Secrets are not committed to source control

---

## Deployment Verification

After deployment:

1. Verify the application starts successfully.
2. Check the health endpoint.
3. Authenticate through the API.
4. Execute a protected endpoint.
5. Review application logs for startup errors.
6. Confirm database connectivity.
7. Verify the OpenAPI documentation loads successfully.

Successful completion of these checks indicates the deployment is ready for use.