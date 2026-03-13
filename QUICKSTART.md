# Track Warrant Management System — Quick Start

## Prerequisites

- **Java 17** or later (`java -version`)
- **Git** (to clone the repository)
- Internet access on first build (Maven downloads dependencies)

---

## 1. Clone and build

```bash
git clone https://github.com/TrainBeans/TrackWarrants.git
cd TrackWarrants/main
./mvnw clean package -DskipTests
```

> **Windows:** use `mvnw.cmd` instead of `./mvnw`.

---

## 2. Run the application

### Option A — Maven wrapper (recommended for development)

```bash
cd TrackWarrants/main
./mvnw spring-boot:run
```

### Option B — Packaged JAR

```bash
cd TrackWarrants/main
java -jar target/main-0.0.1-SNAPSHOT.jar
```

The application starts on **http://localhost:8080**.  
Three sample warrants are seeded automatically on every startup.

---

## 3. Access points

| Resource | URL |
|----------|-----|
| **Warrant entry form** | http://localhost:8080 |
| **Warrant output / print form** | http://localhost:8080/warrant-form.html?id={warrantId} |
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **H2 Console** | http://localhost:8080/h2-console |
| **Actuator** | http://localhost:8080/actuator |

### H2 console connection settings

| Setting | Value |
|---------|-------|
| JDBC URL | `jdbc:h2:mem:trackwarrants` |
| Username | `sa` |
| Password | *(leave empty)* |

---

## 4. Fill in and submit a warrant (UI walkthrough)

1. Open **http://localhost:8080** — the entry form loads with the next warrant number and today's date pre-filled.
2. Fill in **TO**, **AT**, and **TRAIN/ENGINE** header fields.
3. Check the box next to each line you want to include, then fill in the blanks for that line.  
   *(Filling a blank automatically checks its box.)*
4. Fill in the footer fields: **OK** time, **DISPATCHER**, **RELAYED TO**, **COPIED BY**, etc.
5. Click **Submit**.  The form redirects to the printable output form.
6. On the output form, choose **Print (Standard)** or **Print (58mm Receipt)** as needed.

---

## 5. Quick API reference

```bash
# List all warrants
curl http://localhost:8080/api/warrants

# Get next daily warrant number
curl http://localhost:8080/api/warrants/next-number

# Get a single warrant
curl http://localhost:8080/api/warrants/2026-03-13-1

# Create a warrant (minimal example)
curl -X POST http://localhost:8080/api/warrants \
  -H "Content-Type: application/json" \
  -d '{
    "trainCrew": "Smith",
    "location": "Barstow",
    "trainId": "BNSF-4523",
    "issuedBy": "Jones",
    "line2Instruction": "Proceed from Barstow to Needles on Main Track"
  }'

# Complete a warrant
curl -X PUT http://localhost:8080/api/warrants/2026-03-13-1/complete

# Cancel a warrant
curl -X PUT http://localhost:8080/api/warrants/2026-03-13-1/cancel

# Delete a warrant
curl -X DELETE http://localhost:8080/api/warrants/2026-03-13-1
```

For the full API reference see the Swagger UI at http://localhost:8080/swagger-ui.html.

---

## 6. Run the tests

```bash
cd TrackWarrants/main
./mvnw test
```

Expected result: **36 tests, 0 failures**.

| Test class | Tests |
|-----------|-------|
| `TrackWarrantControllerIntegrationTest` | 13 |
| `TrackWarrantServiceTest` | 13 |
| `TrackWarrantFormContractTest` | 7 |
| `TrackWarrantEntryToOutputE2ETest` | 2 |
| `MainApplicationTests` | 1 |

---

## 7. Troubleshooting

### Port 8080 already in use

```bash
# Find the process
lsof -i :8080

# Stop it
kill <PID>
```

### Application will not start

```bash
# Verify Java version (must be 17+)
java -version

# Clean rebuild
cd TrackWarrants/main
./mvnw clean package -DskipTests
```

### Tests failing after a code change

```bash
./mvnw clean test
```

