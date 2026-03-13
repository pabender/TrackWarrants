# Track Warrant Management System - Quick Start Guide

## 🚀 Starting the Application

### Option 1: Using Maven
```bash
cd /home/paul/Trainbeans/TrackWarrants/main
mvn spring-boot:run
```

### Option 2: Using the JAR file
```bash
cd /home/paul/Trainbeans/TrackWarrants/main
mvn clean package -DskipTests
java -jar target/main-0.0.1-SNAPSHOT.jar
```

The application will start on **http://localhost:8080**

---

## 🌐 Access Points

| Resource | URL | Description |
|----------|-----|-------------|
| **Web UI** | http://localhost:8080 | Interactive web interface |
| **H2 Console** | http://localhost:8080/h2-console | Database management console |
| **API Base** | http://localhost:8080/api/warrants | REST API base endpoint |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | API documentation (if configured) |
| **Actuator** | http://localhost:8080/actuator | Application health & metrics |

---

## 📋 Sample API Calls

### 1. Get All Warrants
```bash
curl http://localhost:8080/api/warrants | jq
```

### 2. Get Active Warrants Only
```bash
curl http://localhost:8080/api/warrants/active | jq
```

### 3. Get Single Warrant
```bash
curl http://localhost:8080/api/warrants/TW-2026-001 | jq
```

### 4. Get Warrants by Train ID
```bash
curl http://localhost:8080/api/warrants/train/BNSF-4523 | jq
```

### 5. Create New Warrant
```bash
curl -X POST http://localhost:8080/api/warrants \
  -H "Content-Type: application/json" \
  -d '{
    "warrantId": "TW-2026-100",
    "trainId": "FREIGHT-500",
    "startingLocation": "Kansas City Yard",
    "endingLocation": "Oklahoma City Terminal",
    "trackName": "Central Main",
    "maxSpeed": 70,
    "issuedBy": "Dispatcher Johnson",
    "instructions": "Proceed with normal operations",
    "direction": "SOUTH"
  }' | jq
```

### 6. Complete a Warrant
```bash
curl -X PUT http://localhost:8080/api/warrants/TW-2026-100/complete | jq
```

### 7. Cancel a Warrant
```bash
curl -X PUT http://localhost:8080/api/warrants/TW-2026-100/cancel | jq
```

### 8. Delete a Warrant
```bash
curl -X DELETE http://localhost:8080/api/warrants/TW-2026-100
```

---

## 🧪 Running Tests

### Run All Tests
```bash
cd /home/paul/Trainbeans/TrackWarrants/main
mvn test
```

### Run Only Integration Tests
```bash
mvn test -Dtest=TrackWarrantControllerIntegrationTest
```

### Run Only Service Tests
```bash
mvn test -Dtest=TrackWarrantServiceTest
```

### Run API Test Script
```bash
cd /home/paul/Trainbeans/TrackWarrants
./test-api.sh
```

---

## 🗄️ Database Configuration

The application uses an H2 in-memory database with the following settings:

- **JDBC URL:** `jdbc:h2:mem:trackwarrants`
- **Username:** `sa`
- **Password:** *(empty)*
- **Driver:** `org.h2.Driver`

### Accessing H2 Console:
1. Navigate to http://localhost:8080/h2-console
2. Use the connection settings above
3. Click "Connect"

### Sample SQL Queries:
```sql
-- View all warrants
SELECT * FROM track_warrants;

-- View active warrants
SELECT * FROM track_warrants WHERE status = 'ACTIVE';

-- Count warrants by status
SELECT status, COUNT(*) FROM track_warrants GROUP BY status;

-- Find warrants expiring soon
SELECT warrant_id, train_id, expiration_date_time 
FROM track_warrants 
WHERE expiration_date_time < CURRENT_TIMESTAMP + INTERVAL '2' HOUR
AND status = 'ACTIVE';
```

---

## 📊 Test Results

**Total Tests:** 26 tests
- **Integration Tests:** 12 tests (REST API)
- **Service Unit Tests:** 13 tests (Business Logic)
- **Basic Application Test:** 1 test

**Status:** ✅ All tests passing

---

## 🎯 Key Features Implemented

✅ **Create Warrant** - POST endpoint with validation  
✅ **Retrieve All Warrants** - GET endpoint with full list  
✅ **Retrieve Single Warrant** - GET endpoint by warrant ID  
✅ **Retrieve Active Warrants** - GET endpoint filtered by status  
✅ **Complete Warrant** - PUT endpoint to mark as completed  
✅ **Cancel Warrant** - PUT endpoint to cancel  
✅ **Delete Warrant** - DELETE endpoint to remove  
✅ **H2 Database** - In-memory storage with JPA  
✅ **RESTful Design** - Proper HTTP methods and status codes  
✅ **Error Handling** - Global exception handler  
✅ **Logging** - Comprehensive logging with SLF4J  
✅ **Sample Data** - Pre-loaded with 3 sample warrants  
✅ **Web UI** - Interactive interface for testing  
✅ **Automatic Timestamps** - Created/modified tracking  

---

## 🔍 Architecture Overview

```
┌─────────────────────────────────────────────────┐
│              REST Controller Layer               │
│         (TrackWarrantController.java)           │
│    Handles HTTP requests/responses              │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│              Service Layer                       │
│         (TrackWarrantService.java)              │
│    Business logic & transaction management      │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│              Repository Layer                    │
│      (TrackWarrantRepository.java)              │
│    Data access with Spring Data JPA             │
└─────────────────┬───────────────────────────────┘
                  │
┌─────────────────▼───────────────────────────────┐
│              H2 Database                         │
│         (In-Memory Database)                    │
│    Stores track warrant records                 │
└─────────────────────────────────────────────────┘
```

---

## 📝 Sample Warrants Included

1. **TW-2026-001** - Chicago to St. Louis (ACTIVE)
2. **TW-2026-002** - Denver to Salt Lake City (ACTIVE)
3. **TW-2026-003** - Pittsburgh to Cleveland (COMPLETED)

---

## 🐛 Troubleshooting

### Port 8080 already in use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### Application won't start
```bash
# Clean and rebuild
mvn clean install

# Check Java version
java -version  # Should be Java 17
```

### Tests failing
```bash
# Clean test data and rerun
mvn clean test
```

---

## 📞 Support

For issues or questions, check the application logs in the console output.

