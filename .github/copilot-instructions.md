# GitHub Copilot — Repository Onboarding

## What this repository is

**TrackWarrants** is a Spring Boot REST API application for managing railroad track warrants.
It uses an H2 in-memory database, a 17-line warrant model matching standard railroad operating
practice, and a two-page web UI (warrant entry form + printable output form).

**Technology stack**

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.3 |
| Persistence | Spring Data JPA + H2 (in-memory) |
| Code generation | Lombok |
| API docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven wrapper (`./mvnw`) |
| Testing | JUnit 5, AssertJ, Spring Boot Test, JaCoCo |

---

## Authoritative commands

All commands run from `TrackWarrants/main/` unless stated otherwise.

| Task | Command | Typical time |
|------|---------|--------------|
| Build (skip tests) | `./mvnw clean package -DskipTests` | ~20 s |
| Build + test | `./mvnw clean verify` | ~45 s |
| Run tests only | `./mvnw test` | ~45 s |
| Start dev server | `./mvnw spring-boot:run` | ~10 s |
| Start packaged JAR | `java -jar target/main-0.0.1-SNAPSHOT.jar` | ~5 s |

> **Run `./mvnw clean test`** (not just `./mvnw test`) after any code change to ensure stale
> class files are not masking failures.

---

## Shell helper scripts (repository root)

| Script | Purpose |
|--------|---------|
| `start.sh` | Build JAR if missing, then start the application in background |
| `stop.sh` | Gracefully stop the running application |
| `status.sh` | Print running/stopped status, PID, port, and log file path |
| `restart.sh` | `stop.sh` then `start.sh` |

---

## Project layout

```
TrackWarrants/
├── .github/
│   └── copilot-instructions.md   ← this file
├── LICENSE
├── README.md
├── QUICKSTART.md
├── pom.xml                        ← parent POM (aggregator)
├── start.sh / stop.sh / status.sh / restart.sh
└── main/
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/org/trainbeans/trackwarrants/main/
        │   │   ├── MainApplication.java
        │   │   ├── config/
        │   │   │   └── DataInitializer.java
        │   │   ├── controller/
        │   │   │   └── TrackWarrantController.java
        │   │   ├── dto/
        │   │   │   ├── CreateWarrantRequest.java
        │   │   │   └── TrackWarrantResponse.java
        │   │   ├── entity/
        │   │   │   └── TrackWarrant.java
        │   │   ├── exception/
        │   │   │   └── GlobalExceptionHandler.java
        │   │   ├── factory/
        │   │   │   └── TrackWarrantFactory.java
        │   │   ├── mapper/
        │   │   │   └── TrackWarrantMapper.java
        │   │   ├── repository/
        │   │   │   └── TrackWarrantRepository.java
        │   │   ├── service/
        │   │   │   └── TrackWarrantService.java
        │   │   └── validation/
        │   │       └── TrackWarrantValidator.java
        │   └── resources/
        │       ├── application.properties
        │       └── static/
        │           ├── index.html           ← warrant entry form
        │           └── warrant-form.html    ← output / print form
        └── test/
            └── java/org/trainbeans/trackwarrants/main/
                ├── MainApplicationTests.java
                ├── controller/
                │   ├── TrackWarrantControllerIntegrationTest.java
                │   ├── TrackWarrantEntryToOutputE2ETest.java
                │   └── TrackWarrantFormContractTest.java
                └── service/
                    └── TrackWarrantServiceTest.java
```

---

## Key architecture facts

### Composite warrant ID

The primary business key is a **composite daily warrant ID** constructed by the entry form and
sent in the `POST /api/warrants` request body:

```
warrantId = "{yyyy-MM-dd}-{warrantNumber}"   e.g.  "2026-03-13-1"
```

`warrantNumber` is the daily sequence counter returned by `GET /api/warrants/next-number`.

### Non-nullable entity fields

The following `TrackWarrant` fields are `@Column(nullable = false)` and **must** be supplied
when constructing test entities or `CreateWarrantRequest` objects:

| Field | Type | Notes |
|-------|------|-------|
| `warrantId` | `String` | Composite key, unique |
| `warrantNumber` | `Integer` | Daily sequence number |
| `warrantDate` | `String` | `yyyy-MM-dd` string |
| `trainId` | `String` | Train/engine identifier |
| `startingLocation` | `String` | Origin location |
| `issuedDateTime` | `LocalDateTime` | Defaults to `now()` in factory |
| `status` | `WarrantStatus` | Defaults to `ACTIVE` in `@PrePersist` |
| `createdDateTime` | `LocalDateTime` | Set automatically in `@PrePersist` |

All other fields (header, footer, line1–17 instructions) are nullable.

### In-memory database

H2 is used in embedded/in-memory mode. The database is reset on every restart.
Three sample warrants are seeded by `DataInitializer` on startup.

```
JDBC URL : jdbc:h2:mem:trackwarrants
Username : sa
Password : (empty)
```

### Service interface casting

`TrackWarrantController` injects `TrackWarrantUseCase` (interface) but casts to
`TrackWarrantService` (concrete class) when calling `nextDailyWarrantNumber()`, which is not
part of the interface:

```java
((TrackWarrantService) service).nextDailyWarrantNumber()
```

Do not remove this method from `TrackWarrantService` or move it only to the interface without
also providing an implementation.

### `trimToNull` in factory

`TrackWarrantFactory.trimToNull()` converts blank strings to `null` before persisting.
Tests that assert a specific field value should not pass whitespace-only strings.

---

## Required fields for test entity / request construction

When writing tests that create a `TrackWarrant` entity directly or via `CreateWarrantRequest`,
you must supply at minimum:

```java
TrackWarrant.builder()
    .warrantId("2026-03-13-1")
    .warrantNumber(1)
    .warrantDate("2026-03-13")
    .trainId("TEST-001")
    .startingLocation("Barstow")
    .issuedDateTime(LocalDateTime.now())
    .status(TrackWarrant.WarrantStatus.ACTIVE)
    .createdDateTime(LocalDateTime.now())
    .build();
```

---

## HTML form rules — do not break

### `index.html` (entry form)

- Inputs follow the naming scheme `l{N}_{field}` (e.g. `l2_from`, `l7_train`).
- Each line has a checkbox `l{N}_on`.
- Filling any blank in a line auto-checks the checkbox (`syncLineChecksFromData` /
  `attachLineAutoCheckHandlers`).
- The function `deriveFromLineDriven()` builds structured `lineValues` and calls
  `syncLineChecksFromData()` before assembling line fields.
- Submit posts to `POST /api/warrants`; on success redirects to
  `warrant-form.html?id={warrantId}`.

### `warrant-form.html` (output / print form)

- All 17 line templates are defined in `WARRANT_LINES` array: `{ num: N, text: '...' }`.
- Lines 8, 10, and 15 are fixed-text (no fill-in blanks).
- The output form prefers structured line fields from the API response and falls back to
  `fallbackLine()` for derived text.
- **58 mm thermal receipt print mode** — must not be altered without updating the contract test:
  - CSS: `@page { size: 58mm auto; margin: 2mm 2mm 2mm 4mm; }`
  - Body padding: `body.receipt-mode .warrant-form { padding: 2mm 2mm 2mm 4mm; }`
  - The left margin is intentionally 4 mm (not 2 mm) to prevent the leftmost character from
    being cut off on 58 mm thermal printers.

---

## REST API surface

Base URL: `http://localhost:8080/api/warrants`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/warrants` | Create a new warrant |
| `GET` | `/api/warrants` | List all warrants |
| `GET` | `/api/warrants/{warrantId}` | Get a single warrant by composite ID |
| `GET` | `/api/warrants/active` | List ACTIVE warrants |
| `GET` | `/api/warrants/train/{trainId}` | List warrants by train ID |
| `GET` | `/api/warrants/next-number` | Get next daily warrant number (integer) |
| `PUT` | `/api/warrants/{id}/complete` | Transition warrant to COMPLETED |
| `PUT` | `/api/warrants/{id}/cancel` | Transition warrant to CANCELLED |
| `DELETE` | `/api/warrants/{id}` | Delete a warrant |

Full interactive documentation: **http://localhost:8080/swagger-ui.html**
