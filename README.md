# Track Warrant Management System

A Spring Boot REST API application for managing railroad track warrants with H2 database storage.

## Features

- ✅ RESTful API for track warrant management
- ✅ H2 in-memory database for data storage
- ✅ JPA/Hibernate for data persistence
- ✅ Lombok for cleaner code
- ✅ Full CRUD operations
- ✅ Comprehensive test coverage
- ✅ Web UI for testing
- ✅ Swagger API documentation

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.3**
- **Spring Data JPA**
- **H2 Database**
- **Lombok**
- **JUnit 5**
- **AssertJ**
- **Maven**

## Quick Start

### Build the project
```bash
cd /home/paul/Trainbeans/TrackWarrants
mvn clean install
```

### Run the application
```bash
cd main
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

## API Endpoints

### Base URL: `http://localhost:8080/api/warrants`

### 1. Get All Warrants
```http
GET /api/warrants
```
Returns a list of all track warrants.

### 2. Get Active Warrants Only
```http
GET /api/warrants/active
```
Returns only warrants with status ACTIVE.

### 3. Get Single Warrant
```http
GET /api/warrants/{warrantId}
```
Returns a specific warrant by warrant ID.

**Example:** `GET /api/warrants/TW-2026-001`

### 4. Get Warrants by Train ID
```http
GET /api/warrants/train/{trainId}
```
Returns all warrants for a specific train.

**Example:** `GET /api/warrants/train/BNSF-4523`

### 5. Create New Warrant
```http
POST /api/warrants
Content-Type: application/json
```

**Request Body:**
```json
{
  "warrantId": "TW-2026-001",
  "trainId": "BNSF-4523",
  "startingLocation": "Chicago Union Station",
  "endingLocation": "St. Louis Terminal",
  "trackName": "Illinois Main Line",
  "issuedDateTime": "2026-03-04T14:30:00",
  "expirationDateTime": "2026-03-04T22:30:00",
  "maxSpeed": 79,
  "issuedBy": "Dispatcher Jones",
  "instructions": "Maintain maximum authorized speed. Report arrival at St. Louis.",
  "direction": "SOUTHWEST"
}
```

### 6. Complete a Warrant
```http
PUT /api/warrants/{warrantId}/complete
```
Marks the warrant as COMPLETED.

**Example:** `PUT /api/warrants/TW-2026-001/complete`

### 7. Cancel a Warrant
```http
PUT /api/warrants/{warrantId}/cancel
```
Marks the warrant as CANCELLED.

**Example:** `PUT /api/warrants/TW-2026-001/cancel`

### 8. Delete a Warrant
```http
DELETE /api/warrants/{warrantId}
```
Permanently deletes a warrant.

**Example:** `DELETE /api/warrants/TW-2026-001`

## Testing the API

### Using the Web UI
Navigate to **http://localhost:8080** to access the web-based interface for managing track warrants.

### Using curl

**Create a warrant:**
```bash
curl -X POST http://localhost:8080/api/warrants \
  -H "Content-Type: application/json" \
  -d '{
    "warrantId": "TW-2026-100",
    "trainId": "TEST-100",
    "startingLocation": "Station A",
    "endingLocation": "Station B",
    "maxSpeed": 60
  }'
```

**Get all warrants:**
```bash
curl http://localhost:8080/api/warrants
```

**Get a single warrant:**
```bash
curl http://localhost:8080/api/warrants/TW-2026-001
```

**Complete a warrant:**
```bash
curl -X PUT http://localhost:8080/api/warrants/TW-2026-100/complete
```

**Delete a warrant:**
```bash
curl -X DELETE http://localhost:8080/api/warrants/TW-2026-100
```

## Database Access

### H2 Console
Access the H2 database console at **http://localhost:8080/h2-console**

**Connection Settings:**
- JDBC URL: `jdbc:h2:mem:trackwarrants`
- Username: `sa`
- Password: *(leave empty)*

## API Documentation

Swagger UI is available at **http://localhost:8080/swagger-ui.html** (if configured)

## Data Model

### TrackWarrant Entity

| Field | Type | Description |
|-------|------|-------------|
| id | Long | Primary key (auto-generated) |
| warrantId | String | Unique warrant identifier |
| trainId | String | Train/locomotive number |
| startingLocation | String | Starting point of authorized section |
| endingLocation | String | Ending point of authorized section |
| trackName | String | Name of the track/line |
| issuedDateTime | LocalDateTime | When warrant was issued |
| expirationDateTime | LocalDateTime | When warrant expires |
| maxSpeed | Integer | Maximum authorized speed (MPH) |
| status | WarrantStatus | Current status (ACTIVE, EXPIRED, CANCELLED, COMPLETED) |
| issuedBy | String | Who issued the warrant |
| instructions | String | Special instructions/restrictions |
| direction | String | Direction of travel |
| createdDateTime | LocalDateTime | Record creation timestamp |
| lastModifiedDateTime | LocalDateTime | Last modification timestamp |

## Sample Data

The application comes pre-loaded with 3 sample track warrants for testing:
- TW-2026-001 (ACTIVE) - Chicago to St. Louis
- TW-2026-002 (ACTIVE) - Denver to Salt Lake City
- TW-2026-003 (COMPLETED) - Pittsburgh to Cleveland

## Running Tests

```bash
cd main
mvn test
```

All tests should pass (13 integration tests + 1 basic test = 14 total).

## Project Structure

```
TrackWarrants/
├── main/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/org/trainbeans/trackwarrants/main/
│   │   │   │   ├── MainApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   └── DataInitializer.java
│   │   │   │   ├── controller/
│   │   │   │   │   └── TrackWarrantController.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── CreateWarrantRequest.java
│   │   │   │   │   └── TrackWarrantResponse.java
│   │   │   │   ├── entity/
│   │   │   │   │   └── TrackWarrant.java
│   │   │   │   ├── exception/
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── TrackWarrantRepository.java
│   │   │   │   └── service/
│   │   │   │       └── TrackWarrantService.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── static/
│   │   │           └── index.html
│   │   └── test/
│   │       └── java/org/trainbeans/trackwarrants/main/
│   │           └── controller/
│   │               └── TrackWarrantControllerIntegrationTest.java
│   └── pom.xml
└── service/
    └── src/main/java/org/trainbeans/trackwarrants/
        └── TrackWarrant.java (original bean)
```

## License

Track Warrant Management System - For educational and demonstration purposes.

