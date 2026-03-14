package org.trainbeans.trackwarrants.main.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.trainbeans.trackwarrants.main.dto.CreateWarrantRequest;
import org.trainbeans.trackwarrants.main.entity.TrackWarrant;
import org.trainbeans.trackwarrants.main.repository.TrackWarrantRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for TrackWarrant REST API endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("TrackWarrant REST API Integration Tests")
class TrackWarrantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrackWarrantRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/warrants - Should create a new warrant")
    void testCreateWarrant() throws Exception {
        CreateWarrantRequest request = CreateWarrantRequest.builder()
            .warrantId("TW-TEST-001")
            .warrantNumber(1)
            .warrantDate("2026-03-13")
            .trainId("TEST-TRAIN-123")
            .trainCrew("ENGINEER A / CONDUCTOR B")
            .location("STATION A")
            .startingLocation("Station A")
            .issuedDateTime(LocalDateTime.now())
            .issuedBy("Test Dispatcher")
            .okTime("0900")
            .dispatcher("DISPATCHER X")
            .copiedBy("CONDUCTOR B")
            .line2From("Station A")
            .line2To("Station B")
            .line2Track("Test Line")
            .build();

        mockMvc.perform(post("/api/warrants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.warrantId").value("TW-TEST-001"))
            .andExpect(jsonPath("$.warrantNumber").value(1))
            .andExpect(jsonPath("$.warrantDate").value("2026-03-13"))
            .andExpect(jsonPath("$.trainId").value("TEST-TRAIN-123"))
            .andExpect(jsonPath("$.trainCrew").value("ENGINEER A / CONDUCTOR B"))
            .andExpect(jsonPath("$.location").value("STATION A"))
            .andExpect(jsonPath("$.okTime").value("0900"))
            .andExpect(jsonPath("$.dispatcher").value("DISPATCHER X"))
            .andExpect(jsonPath("$.copiedBy").value("CONDUCTOR B"))
            .andExpect(jsonPath("$.line2From").value("Station A"))
            .andExpect(jsonPath("$.line2To").value("Station B"))
            .andExpect(jsonPath("$.line2Track").value("Test Line"))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("GET /api/warrants - Should retrieve all warrants")
    void testGetAllWarrants() throws Exception {
        // Create test data
        createTestWarrant("TW-001", "TRAIN-001", TrackWarrant.WarrantStatus.ACTIVE);
        createTestWarrant("TW-002", "TRAIN-002", TrackWarrant.WarrantStatus.VOID);

        mockMvc.perform(get("/api/warrants"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].warrantId").exists())
            .andExpect(jsonPath("$[1].warrantId").exists());
    }

    @Test
    @DisplayName("GET /api/warrants/{warrantId} - Should retrieve a single warrant")
    void testGetWarrantById() throws Exception {
        createTestWarrant("TW-SINGLE-001", "TRAIN-999", TrackWarrant.WarrantStatus.ACTIVE);

        mockMvc.perform(get("/api/warrants/TW-SINGLE-001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.warrantId").value("TW-SINGLE-001"))
            .andExpect(jsonPath("$.trainId").value("TRAIN-999"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/warrants/{warrantId} - Should return 404 for non-existent warrant")
    void testGetNonExistentWarrant() throws Exception {
        mockMvc.perform(get("/api/warrants/NON-EXISTENT"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/warrants/active - Should retrieve only active warrants")
    void testGetActiveWarrants() throws Exception {
        createTestWarrant("TW-ACTIVE-001", "TRAIN-001", TrackWarrant.WarrantStatus.ACTIVE);
        createTestWarrant("TW-VOID-001", "TRAIN-002", TrackWarrant.WarrantStatus.VOID);
        createTestWarrant("TW-ACTIVE-002", "TRAIN-003", TrackWarrant.WarrantStatus.ACTIVE);

        mockMvc.perform(get("/api/warrants/active"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].status").value("ACTIVE"))
            .andExpect(jsonPath("$[1].status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /api/warrants/train/{trainId} - Should retrieve warrants for specific train")
    void testGetWarrantsByTrainId() throws Exception {
        createTestWarrant("TW-001", "TRAIN-777", TrackWarrant.WarrantStatus.ACTIVE);
        createTestWarrant("TW-002", "TRAIN-777", TrackWarrant.WarrantStatus.VOID);
        createTestWarrant("TW-003", "TRAIN-888", TrackWarrant.WarrantStatus.ACTIVE);

        mockMvc.perform(get("/api/warrants/train/TRAIN-777"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].trainId").value("TRAIN-777"))
            .andExpect(jsonPath("$[1].trainId").value("TRAIN-777"));
    }

    @Test
    @DisplayName("PUT /api/warrants/{warrantId}/complete - Should mark warrant as void")
    void testCompleteWarrant() throws Exception {
        createTestWarrant("TW-COMPLETE-001", "TRAIN-123", TrackWarrant.WarrantStatus.ACTIVE);

        mockMvc.perform(put("/api/warrants/TW-COMPLETE-001/complete"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.warrantId").value("TW-COMPLETE-001"))
            .andExpect(jsonPath("$.status").value("VOID"));
    }

    @Test
    @DisplayName("PUT /api/warrants/{warrantId}/complete - Should return 404 for non-existent warrant")
    void testCompleteNonExistentWarrant() throws Exception {
        mockMvc.perform(put("/api/warrants/NON-EXISTENT/complete"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/warrants/{warrantId} - Should delete a warrant")
    void testDeleteWarrant() throws Exception {
        createTestWarrant("TW-DELETE-001", "TRAIN-789", TrackWarrant.WarrantStatus.ACTIVE);

        mockMvc.perform(delete("/api/warrants/TW-DELETE-001"))
            .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/api/warrants/TW-DELETE-001"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/warrants - Should return 400 for duplicate warrant ID")
    void testCreateDuplicateWarrant() throws Exception {
        createTestWarrant("TW-DUPLICATE", "TRAIN-001", TrackWarrant.WarrantStatus.ACTIVE);

        CreateWarrantRequest request = CreateWarrantRequest.builder()
            .warrantId("TW-DUPLICATE")
            .trainId("TRAIN-002")
            .startingLocation("Start")
            .build();

        mockMvc.perform(post("/api/warrants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Complete workflow - Create, retrieve, complete, and verify")
    void testCompleteWorkflow() throws Exception {
        // Create warrant
        CreateWarrantRequest request = CreateWarrantRequest.builder()
            .warrantId("TW-WORKFLOW-001")
            .warrantNumber(1)
            .warrantDate("2026-03-13")
            .trainId("WORKFLOW-TRAIN")
            .trainCrew("ENGINEER W / CONDUCTOR W")
            .location("START POINT")
            .startingLocation("Start Point")
            .issuedDateTime(LocalDateTime.now())
            .issuedBy("Test Dispatcher")
            .okTime("0900")
            .dispatcher("Test Dispatcher")
            .copiedBy("CONDUCTOR W")
            .line2From("Start Point")
            .line2To("End Point")
            .line2Track("Main")
            .build();

        // Step 1: Create
        mockMvc.perform(post("/api/warrants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Step 2: Retrieve
        mockMvc.perform(get("/api/warrants/TW-WORKFLOW-001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.warrantId").value("TW-WORKFLOW-001"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));

        // Step 3: Complete
        mockMvc.perform(put("/api/warrants/TW-WORKFLOW-001/complete"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("VOID"));

        // Step 4: Verify voided
        mockMvc.perform(get("/api/warrants/TW-WORKFLOW-001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("VOID"));
    }

    @Test
    @DisplayName("POST /api/warrants - Should round-trip all entry form fields in response")
    void testCreateWarrantRoundTripAllEntryFormFields() throws Exception {
        LocalDateTime issued = LocalDateTime.of(2026, 3, 11, 9, 15);

        CreateWarrantRequest request = CreateWarrantRequest.builder()
            .warrantId("TW-FULL-001")
            .warrantNumber(1)
            .warrantDate("2026-03-11")
            .trainId("BNSF-4523")
            .trainCrew("ENGINEER JONES / CONDUCTOR SMITH")
            .location("RIVER JCT")
            .startingLocation("RIVER JCT")
            .issuedDateTime(issued)
            .issuedBy("DISPATCHER SMITH")
            .okTime("0915")
            .dispatcher("DISPATCHER SMITH")
            .copiedBy("CONDUCTOR SMITH")
            .relayedTo("YARDMASTER")
            .limitsClearAt("NORTH YARD")
            .limitsClearBy("CONDUCTOR SMITH")
            .line1VoidNumber("TW-FULL-001")
            .line2From("RIVER JCT")
            .line2To("NORTH YARD")
            .line2Track("MAIN")
            .line3From("RIVER JCT")
            .line3To("NORTH YARD")
            .line3Track("MAIN")
            .line4From("SIDING A")
            .line4To("SIDING B")
            .line4Track("SIDING")
            .line5Until("09:45")
            .line6Time("17:30")
            .line7Train("TRAIN 12")
            .line7At("WEST JCT")
            .line8Checked(true)
            .line9Ahead("WEST JCT")
            .line10Checked(true)
            .line11From("MP 10")
            .line11To("MP 12")
            .line12From("MP 9")
            .line12To("MP 13")
            .line13Mph("45")
            .line13From("RIVER JCT")
            .line13To("NORTH YARD")
            .line14Mph("45")
            .line14From("SIDING A")
            .line14To("SIDING B")
            .line15Checked(true)
            .line16Text("TB-01, TB-02, TB-03")
            .line17Text("Reduce speed through work zone")
            .build();

        mockMvc.perform(post("/api/warrants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.warrantId").value("TW-FULL-001"))
            .andExpect(jsonPath("$.warrantNumber").value(1))
            .andExpect(jsonPath("$.warrantDate").value("2026-03-11"))
            .andExpect(jsonPath("$.trainId").value("BNSF-4523"))
            .andExpect(jsonPath("$.trainCrew").value("ENGINEER JONES / CONDUCTOR SMITH"))
            .andExpect(jsonPath("$.location").value("RIVER JCT"))
            .andExpect(jsonPath("$.okTime").value("0915"))
            .andExpect(jsonPath("$.dispatcher").value("DISPATCHER SMITH"))
            .andExpect(jsonPath("$.copiedBy").value("CONDUCTOR SMITH"))
            .andExpect(jsonPath("$.relayedTo").value("YARDMASTER"))
            .andExpect(jsonPath("$.limitsClearAt").value("NORTH YARD"))
            .andExpect(jsonPath("$.limitsClearBy").value("CONDUCTOR SMITH"))
            .andExpect(jsonPath("$.issuedDateTime").value("2026-03-11T09:15:00"))
            .andExpect(jsonPath("$.issuedBy").value("DISPATCHER SMITH"))
            .andExpect(jsonPath("$.line1VoidNumber").value("TW-FULL-001"))
            .andExpect(jsonPath("$.line2From").value("RIVER JCT"))
            .andExpect(jsonPath("$.line2To").value("NORTH YARD"))
            .andExpect(jsonPath("$.line2Track").value("MAIN"))
            .andExpect(jsonPath("$.line3From").value("RIVER JCT"))
            .andExpect(jsonPath("$.line3To").value("NORTH YARD"))
            .andExpect(jsonPath("$.line3Track").value("MAIN"))
            .andExpect(jsonPath("$.line4From").value("SIDING A"))
            .andExpect(jsonPath("$.line4To").value("SIDING B"))
            .andExpect(jsonPath("$.line4Track").value("SIDING"))
            .andExpect(jsonPath("$.line5Until").value("09:45"))
            .andExpect(jsonPath("$.line6Time").value("17:30"))
            .andExpect(jsonPath("$.line7Train").value("TRAIN 12"))
            .andExpect(jsonPath("$.line7At").value("WEST JCT"))
            .andExpect(jsonPath("$.line8Checked").value(true))
            .andExpect(jsonPath("$.line9Ahead").value("WEST JCT"))
            .andExpect(jsonPath("$.line10Checked").value(true))
            .andExpect(jsonPath("$.line11From").value("MP 10"))
            .andExpect(jsonPath("$.line11To").value("MP 12"))
            .andExpect(jsonPath("$.line12From").value("MP 9"))
            .andExpect(jsonPath("$.line12To").value("MP 13"))
            .andExpect(jsonPath("$.line13Mph").value("45"))
            .andExpect(jsonPath("$.line13From").value("RIVER JCT"))
            .andExpect(jsonPath("$.line13To").value("NORTH YARD"))
            .andExpect(jsonPath("$.line14Mph").value("45"))
            .andExpect(jsonPath("$.line14From").value("SIDING A"))
            .andExpect(jsonPath("$.line14To").value("SIDING B"))
            .andExpect(jsonPath("$.line15Checked").value(true))
            .andExpect(jsonPath("$.line16Text").value("TB-01, TB-02, TB-03"))
            .andExpect(jsonPath("$.line17Text").value("Reduce speed through work zone"))
            .andExpect(jsonPath("$.status").value("ACTIVE"))
            .andExpect(jsonPath("$.createdDateTime").exists())
            .andExpect(jsonPath("$.lastModifiedDateTime").exists());
    }

    @Test
    @DisplayName("PUT /api/warrants/{warrantId}/limits-clear - Should record limits clear and void warrant")
    void testRecordLimitsClear() throws Exception {
        createTestWarrant("TW-LIMITS-001", "TRAIN-555", TrackWarrant.WarrantStatus.ACTIVE);

        String body = "{\"limitsClearAt\": \"NORTH YARD\", \"limitsClearBy\": \"CONDUCTOR SMITH\"}";

        mockMvc.perform(put("/api/warrants/TW-LIMITS-001/limits-clear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.warrantId").value("TW-LIMITS-001"))
            .andExpect(jsonPath("$.status").value("VOID"))
            .andExpect(jsonPath("$.limitsClearAt").value("NORTH YARD"))
            .andExpect(jsonPath("$.limitsClearBy").value("CONDUCTOR SMITH"));
    }

    @Test
    @DisplayName("PUT /api/warrants/{warrantId}/limits-clear - Should return 404 for non-existent warrant")
    void testRecordLimitsClearNotFound() throws Exception {
        String body = "{\"limitsClearAt\": \"NORTH YARD\", \"limitsClearBy\": \"CONDUCTOR SMITH\"}";

        mockMvc.perform(put("/api/warrants/NON-EXISTENT/limits-clear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isNotFound());
    }

    private void createTestWarrant(String warrantId, String trainId, TrackWarrant.WarrantStatus status) {
        TrackWarrant warrant = TrackWarrant.builder()
            .warrantId(warrantId)
            .warrantNumber(1)
            .warrantDate("2026-03-13")
            .trainId(trainId)
            .trainCrew("ENGINEER TEST")
            .location("TEST YARD")
            .startingLocation("Test Start")
            .issuedDateTime(LocalDateTime.now())
            .status(status)
            .issuedBy("Test Dispatcher")
            .okTime("0800")
            .dispatcher("TEST DISP")
            .copiedBy("TEST CREW")
            .line2From("Test Start")
            .line2To("Test End")
            .line2Track("Test")
            .build();

        repository.save(warrant);
    }
}

