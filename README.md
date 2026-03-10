# Fleet Management API

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.1.3-brightgreen)

## Overview
Fleet Management API is a backend RESTful service for managing vehicles and maintenance records.  
It provides CRUD operations, search functionality, and input validation, supporting the creation of a scalable fleet management system.

This project was developed as part of a professional portfolio to demonstrate software engineering skills including backend API design, layered architecture, and documentation practices.

---

## Tech Stack
- **Language:** Java 17  
- **Framework:** Spring Boot 3.x  
- **Database:** PostgreSQL  
- **Validation:** Jakarta Validation  
- **Build Tool:** Gradle  
- **Design Pattern:** Layered Architecture (Controller → Service → Repository)  
- **Testing:** JUnit (if applicable)  

---

## Architecture
The backend follows a **3-tier layered architecture**:

Controller Layer → Service Layer → Repository Layer → Database

- Controllers handle API requests and map to services.  
- Services contain business logic and validation.  
- Repositories manage data access via JPA.  
- Entities (`Vehicle`, `MaintenanceRecord`) are mapped to database tables.  
- DTOs decouple internal entities from API requests/responses.  

For a detailed overview, see: [Architecture Overview](docs/architecture.md)

---

## API Documentation
The API exposes endpoints for managing vehicles and maintenance records.  
Endpoints include GET, POST, PUT, DELETE, and search functionality.

Detailed endpoint documentation with request/response examples: [API Design](docs/api-design.md)

---

## Design Decisions
All major architecture and implementation decisions are documented, including:
- Why layered architecture was chosen  
- Why DTOs are used  
- Validation strategies  
- Repository query design  

See full explanation: [Design Decisions](docs/design-decisions.md)

---

## Project Roadmap
This project is designed with a phased roadmap:

- Version 1 (Completed): Vehicle and Maintenance CRUD, DTO mapping, validation, search  
- Version 2 (Planned): Authentication/Authorization, role-based access, reporting, API documentation website  
- Future Ideas: Notifications, analytics dashboard, data import/export  

Detailed roadmap: [Project Roadmap](docs/project-roadmap.md)

---

## Getting Started

### Prerequisites
- Java 17
- PostgreSQL database
- Gradle

### Running the Project
1. Clone the repository:

    ```bash
    git clone https://github.com/Dclarke96/Fleet-Management-API.git
    cd Fleet-Management-API
    git checkout dev
    ```

2. Configure PostgreSQL connection in application.properties or environment variables
3. Build and run: 

    ```bash
    ./gradlew bootRun
    ```
    
4. The API will start at http://localhost:8080
