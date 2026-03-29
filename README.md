# Getting Started with URL Shortener

This is a Spring Boot REST API application for URL shortening with analytics. The application provides three core features:

## Features

### 1. URL Shortening
- Shortens long URLs to memorable short aliases
- Supports both random (6-character) and custom aliases
- Validates URL format (HTTP/HTTPS only)
- Returns status 201 (Created) on success

### 2. Redirection
- Redirects clients from short URLs to original URLs
- Tracks each access for analytics
- Returns 404 (Not Found) if alias doesn't exist

### 3. Analytics
- Provides click statistics for shortened URLs
- Supports optional date range filtering
- Returns total clicks and creation timestamp

## Project Structure

```
.
├── src/
│   ├── main/
│   │   ├── java/com/mercedes/urlshortener/
│   │   │   ├── UrlShortenerApplication.java      (Entry point)
│   │   │   ├── config/                           (Configuration classes)
│   │   │   ├── controller/                       (REST endpoints)
│   │   │   ├── service/                          (Business logic)
│   │   │   ├── repository/                       (Data access)
│   │   │   ├── model/                            (Entities)
│   │   │   │   ├── UrlMapping.java
│   │   │   │   └── ClickEvent.java
│   │   │   ├── dto/                              (DTOs)
│   │   │   │   ├── ShortenRequest.java
│   │   │   │   ├── ShortenResponse.java
│   │   │   │   └── AnalyticsResponse.java
│   │   │   └── exception/                        (Exception handling)
│   │   │       ├── GlobalExceptionHandler.java
│   │   │       ├── AliasAlreadyExistsException.java
│   │   │       ├── UrlNotFoundException.java
│   │   │       └── InvalidUrlException.java
│   │   └── resources/
│   │       ├── application.yml                   (Configuration)
│   │       └── static/                           (Empty - no frontend)
│   └── test/
│       └── java/com/mercedes/urlshortener/       (Unit and integration tests)
├── schema.sql                                      (Database schema)
├── gradle.build                                    (Build configuration)
├── docker-compose.yml                             (Docker setup)
└── README.md                                       (API documentation)
```

## Running the Application

### Prerequisites
- Java 17+
- Gradle
- MySQL 8.0+

### Build and Run Locally

```bash
./gradlew clean build
./gradlew bootRun
```

### Run with Docker

```bash
docker-compose up --build
```

The application will be available at `http://localhost:8080`

## API Endpoints

### POST `/shorten` - Create a shortened URL

**Request:**
```json
{
  "fullUrl": "https://example.com/very/long/url",
  "alias": "my-short-alias"  // optional
}
```

**Response (201 Created):**
```json
{
  "shortUrl": "http://localhost:8080/my-short-alias"
}
```

### GET `/{alias}` - Redirect to original URL

Redirects to the original URL and tracks the access.

**Response:** 302 Found (redirect)

### GET `/analytics/{alias}` - Get statistics

**Query Parameters:**
- `startDate` (optional): `yyyy-MM-dd'T'HH:mm:ss`
- `endDate` (optional): `yyyy-MM-dd'T'HH:mm:ss`

**Response:**
```json
{
  "alias": "my-short-alias",
  "fullUrl": "https://example.com/very/long/url",
  "clicks": 42,
  "createdAt": "2026-03-29T10:15:30"
}
```

## Database Schema

### url_mapping
Stores the mapping between shortened aliases and original URLs.

| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | Primary key, auto-increment |
| full_url | VARCHAR(2048) | Original URL |
| alias | VARCHAR(255) | Unique shortened alias |
| created_at | DATETIME | Creation timestamp |

### click_event
Tracks each access to a shortened URL.

| Column | Type | Notes |
|--------|------|-------|
| id | BIGINT | Primary key, auto-increment |
| url_id | BIGINT | Foreign key to url_mapping |
| clicked_at | DATETIME | Access timestamp |

## Configuration

Edit `src/main/resources/application.yml` to configure:

- **Database connection**: `spring.datasource.*`
- **Port**: `server.port` (default: 8080)
- **Logging level**: `logging.level.*`

## Testing

Run the test suite:

```bash
./gradlew test
```

Tests include:
- Repository integration tests
- Controller integration tests
- Service unit tests
- Exception handling tests

## Exception Handling

The application provides centralized exception handling via `GlobalExceptionHandler`:

- `InvalidUrlException` → 400 Bad Request
- `AliasAlreadyExistsException` → 409 Conflict
- `UrlNotFoundException` → 404 Not Found
- Other exceptions → 500 Internal Server Error

