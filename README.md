# Track Warrant Management System

A Spring Boot REST API application for managing railroad track warrants.  Uses an H2 in-memory
database, a 17-line warrant model matching standard railroad operating practice, and a two-page
web UI (entry form + printable output form).

## License

Copyright (C) 2026 Paul — Trainbeans organization.  
Licensed under the [GNU General Public License v2.0](LICENSE).

## Features

- ✅ 17-line track warrant entry form (`index.html`) with auto-check and auto-number
- ✅ Printable output form (`warrant-form.html`) — standard and 58 mm thermal receipt modes
- ✅ Spring Boot REST API (`/api/warrants`)
- ✅ H2 in-memory database with JPA/Hibernate
- ✅ SOLID-layered architecture: entity, DTO, factory, mapper, service, controller, repository
- ✅ Swagger UI API documentation
- ✅ 36 tests — integration, service unit, E2E, and form-contract tests

## Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.3 |
| Persistence | Spring Data JPA + H2 (in-memory) |
| Code generation | Lombok |
| API docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven (wrapper included) |
| Testing | JUnit 5, AssertJ, Spring Boot Test |

## Quick Start

See [QUICKSTART.md](QUICKSTART.md) for step-by-step instructions.

```bash
# From the repository root
cd main
./mvnw spring-boot:run
```

The application starts on **http://localhost:8080**.

## Web UI

| Page | URL | Purpose |
|------|-----|---------|
| Entry form | http://localhost:8080 | Fill in and submit a new track warrant |
| Output / print form | http://localhost:8080/warrant-form.html?id={warrantId} | View and print a saved warrant |

### Entry form highlights

- Header fields: **Warrant NO** (auto-filled from `/api/warrants/next-number`), **Date** (today),
  **TO** (train crew), **AT** (location), **TRAIN/ENGINE**, **Issued By**
- Lines 1–17 with checkboxes labelled `(Mark "X" in box for each item instructed)`
- Lines with fill-in blanks auto-check their box when a blank is filled in
- Lines 8, 10, and 15 are fixed-text (checkbox only, no fill-in blanks)
- Footer: **OK** time, **DISPATCHER**, **RELAYED TO**, **COPIED BY**,
  **LIMITS REPORTED CLEAR AT / BY**
- Submits to `POST /api/warrants`; on success redirects to the output form

### Print modes (output form)

| Mode | Description |
|------|-------------|
| Standard | Full-page layout, system default font size |
| 58 mm thermal receipt | `@page { size: 58mm auto; margin: 2mm 2mm 2mm 4mm; }`, 8 pt font, stacked layout |

## 17-Line Warrant Model

| # | Text |
|---|------|
| 1 | Track Warrant NO. ___ is Void. |
| 2 | Proceed from ___ to ___ on ___ Track |
| 3 | Proceed from ___ to ___ on ___ Track *(extension / duplicate)* |
| 4 | Work between ___ and ___ on ___ Track |
| 5 | Not in effect until ___M |
| 6 | This Authority Expires at ___M |
| 7 | Not in effect until after arrival of ___ at ___ |
| 8 | Hold Main Track at Last Named Point. |
| 9 | Do Not Foul Limits ahead of ___. |
| 10 | Clear Main Track at Last Named Point. |
| 11 | Between ___ and ___ Make All Movements at restricted speed. Limits occupied by train or engine. |
| 12 | Between ___ and ___ Make All Movements at restricted speed and stop short of Men or Machines fouling track. |
| 13 | Do not exceed ___ MPH between ___ and ___ |
| 14 | Do not exceed ___ MPH between ___ and ___ *(second speed restriction)* |
| 15 | Protection as prescribed by Rule 99 not required. |
| 16 | Track Bulletins in Effect: ___ |
| 17 | Other Specific Instructions: ___ |

## API Endpoints

### Base URL: `http://localhost:8080/api/warrants`

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/warrants` | Create a new warrant |
| `GET` | `/api/warrants` | List all warrants |
| `GET` | `/api/warrants/{warrantId}` | Get a single warrant |
| `GET` | `/api/warrants/active` | List active warrants |
| `GET` | `/api/warrants/train/{trainId}` | List warrants by train ID |
| `GET` | `/api/warrants/next-number` | Get next daily warrant number |
| `PUT` | `/api/warrants/{id}/complete` | Mark a warrant as COMPLETED |
| `PUT` | `/api/warrants/{id}/cancel` | Cancel a warrant |
| `DELETE` | `/api/warrants/{id}` | Delete a warrant |

### Create warrant — request body

```json
{
  "warrantNumber": "1",
  "warrantDate": "2026-03-13",
  "trainCrew": "Smith",
  "location": "Barstow",
  "trainId": "BNSF-4523",
  "issuedBy": "Dispatcher Jones",
  "line2Instruction": "Proceed from Barstow to Needles on Main Track",
  "line6Instruction": "This Authority Expires at 1800M",
  "okTime": "1430",
  "dispatcher": "Jones",
  "copiedBy": "Smith"
}
```

Only the fields relevant to the warrant need to be supplied; all 17 line fields, header fields,
and footer fields are optional strings.

## Data Model

### Entity fields

**Header**

| Field | Type | Description |
|-------|------|-------------|
| `warrantId` | String | Composite key: `{date}-{number}` (e.g. `2026-03-13-1`) |
| `warrantNumber` | String | Daily sequence number (auto-assigned) |
| `warrantDate` | LocalDate | Issue date |
| `trainCrew` | String | TO field — train crew name |
| `location` | String | AT field — issuing location |
| `trainId` | String | TRAIN/ENGINE number |
| `issuedBy` | String | Dispatcher / issuing authority |
| `issuedDateTime` | LocalDateTime | Timestamp when issued |

**Lines**

`line1Instruction` … `line17Instruction` — String fields, one per warrant line.

**Footer**

| Field | Type | Description |
|-------|------|-------------|
| `okTime` | String | OK time (___M) |
| `dispatcher` | String | Dispatcher signature |
| `relayedTo` | String | Relay recipient |
| `copiedBy` | String | Who copied the warrant |
| `limitsClearAt` | String | Limits reported clear at time |
| `limitsClearBy` | String | Limits cleared by whom |

**Audit / status**

| Field | Type | Description |
|-------|------|-------------|
| `status` | WarrantStatus | `ACTIVE`, `COMPLETED`, `CANCELLED`, `EXPIRED` |
| `createdDateTime` | LocalDateTime | Record creation timestamp |
| `lastModifiedDateTime` | LocalDateTime | Last update timestamp |

## Sample Data

Three warrants are seeded on every startup by `DataInitializer`:

| Warrant ID | Train | Key Lines | Status |
|------------|-------|-----------|--------|
| `2026-03-13-1` | BNSF-4523 | Line 2: Barstow → Needles, Line 6 expiry | ACTIVE |
| `2026-03-13-2` | UP-1234 | Line 2: Needles → Kingman, Line 13 speed restriction | ACTIVE |
| `2026-03-13-3` | AMTK-100 | Line 2: Kingman → Flagstaff, Line 5 time restriction | COMPLETED |

## Running Tests

```bash
cd main
./mvnw test
```

All 36 tests should pass:

| Test class | Count | Coverage |
|-----------|-------|---------|
| `TrackWarrantControllerIntegrationTest` | 13 | REST API endpoints |
| `TrackWarrantServiceTest` | 13 | Business logic |
| `TrackWarrantFormContractTest` | 7 | Line model / form field contract |
| `TrackWarrantEntryToOutputE2ETest` | 2 | End-to-end create → retrieve |
| `MainApplicationTests` | 1 | Context load |

## Project Structure

```
TrackWarrants/
├── .github/
│   └── copilot-instructions.md      ← Copilot agent onboarding
├── LICENSE
├── README.md
├── QUICKSTART.md
├── pom.xml                          ← parent POM (aggregator)
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
        │   │   └── service/
        │   │       └── TrackWarrantService.java
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

## Other Access Points

| Resource | URL |
|----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| H2 Console | http://localhost:8080/h2-console |
| Actuator | http://localhost:8080/actuator |

**H2 connection settings:**
- JDBC URL: `jdbc:h2:mem:trackwarrants`
- Username: `sa`
- Password: *(leave empty)*

