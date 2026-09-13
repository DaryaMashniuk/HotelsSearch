<div align="center">
<h3 align="center">Hotel Management Service</h3>

  <p align="center">
    A RESTful API application built with Spring Boot 4 & Java 21 for managing hotel properties, advanced searching, dynamic histogram analytics, and amenities configuration.
  </p>
</div>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li><a href="#key-features">Key Features</a></li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation-and-running">Installation and Running</a></li>
        <li><a href="#configuration">Configuration</a></li>
      </ul>
    </li>
    <li><a href="#api-endpoints">API Endpoints</a></li>
    <li><a href="#database--auditing">Database & Auditing</a></li>
    <li><a href="#architecture">Architecture</a></li>
    <li><a href="#testing">Testing</a></li>
    <li><a href="#environment-variables">Environment Variables</a></li>
  </ol>
</details>

## About The Project

Hotel Management Service is a modern backend microservice designed for processing hotel directory operations. It features clean multi-layered architecture, flexible multi-parameter searching, dynamic aggregation metrics for data reporting, and easy database profile switching.

## Key Features

- **Layered Architecture**: Clean separation between Controller, Service, and Repository layers using DTO patterns and Mappers.
- **Hotel Operations**: CRUD actions for full and summary hotel views.
- **Dynamic Search**: Multi-criteria hotel search filtered by name, brand, city, country, or amenities.
- **Analytics Histogram**: Dynamic aggregation endpoint summarizing hotel distribution by attributes (brand, city, country, amenities).
- **Amenities Management**: Bulk update and attachment of amenities to existing property records.
- **Database Migrations**: Version-controlled database schema management via Liquibase.
- **Swagger Documentation**: Live interactive OpenAPI UI for quick integration testing.
- **Pluggable Persistence**: Easily configurable database profiles (H2 default, ready for PostgreSQL/MySQL).

---

## Getting Started

### Prerequisites

- **Java JDK 21**
- **Apache Maven 3.9+**

### Installation and Running


1. **Clone the repository:**
```bash
   git clone [https://github.com/your-username/hotel-management-service.git](https://github.com/your-username/hotel-management-service.git)
   cd hotel-management-service
```

2. **Run the application:**

```bash
mvn spring-boot:run
```

The application will start on port **8092** with base context `/property-view`._

### Configuration

The application uses profile-based configuration to allow switching between database providers:

- `h2` _(default)_: Embedded in-memory database configuration for rapid testing.

- `postgres` / `mysql`: Custom configurations can be created to switch between dbs.

## API Endpoints

### Hotel Management

|Method|Endpoint|Description|
|---|---|---|
|`GET`|`/property-view/hotels`|Get brief summary list of all hotels|
|`GET`|`/property-view/hotels/{id}`|Get detailed information for a specific hotel|
|`POST`|`/property-view/hotels`|Create a new hotel record|
|`POST`|`/property-view/hotels/{id}/amenities`|Add list of amenities to a hotel|
|`GET`|`/property-view/search`|Search hotels by criteria (`name`, `brand`, `city`, `country`, `amenities`)|
|`GET`|`/property-view/histogram/{param}`|Get hotel count grouped by parameter (`brand`, `city`, `country`, `amenities`)|

### Interactive API Documentation

- **Swagger UI**: `http://localhost:8092/property-view/swagger-ui.html`

- **OpenAPI Spec**: `http://localhost:8092/property-view/v3/api-docs`

## Database & Auditing

### Database Schema (Entity Model)

```
hotels
├── id (PK)
├── name
├── description
├── brand
├── house_number
├── street
├── city
├── country
├── post_code
├── phone
├── email
├── check_in
└── check_out

hotel_amenities
├── hotel_id (FK → hotels.id)
└── amenity (VARCHAR)
```

### Schema Management

- **Liquibase Changelogs**: Located in `src/main/resources/db/changelog/` for seamless DB evolution across target environments.

## Architecture

### System Layers

1. **Controller Layer**: Handles REST requests under `/property-view`, validates input payloads, and returns API responses/DTOs.

2. **Service Layer**: Implements business rules, DTO mapping, and histogram aggregation logic.

3. **Repository Layer**: Spring Data JPA repositories with custom Specification execution for dynamic queries.

4. **Domain/Entity Layer**: JPA Entities mapping database records and relationships.

### Database Abstraction

The application decouples business logic from persistence technologies. Switching from H2 to PostgreSQL requires only passing active profile parameters:

```Bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

## Testing

Execute the unit and integration test suite via Maven:

```Bash
# Run all unit and integration tests
mvn clean test
```

## Environment Variables

|Variable|Description|Default Value|
|---|---|---|
|`SERVER_PORT`|HTTP Server Port|`8092`|
|`SPRING_PROFILES_ACTIVE`|Active profile (`h2`, `postgres`)|`h2`|
|`DB_URL`|JDBC Database Connection URL|`jdbc:h2:mem:hoteldb`|
|`DB_USERNAME`|Database username|`sa`|
|`DB_PASSWORD`|Database password|`""`|
