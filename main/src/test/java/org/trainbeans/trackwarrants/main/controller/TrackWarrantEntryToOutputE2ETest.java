package org.trainbeans.trackwarrants.main.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.trainbeans.trackwarrants.main.dto.CreateWarrantRequest;
import org.trainbeans.trackwarrants.main.dto.TrackWarrantResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end parity test using REST + output-form contract checks.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Track Warrant Entry-to-Output Parity Tests")
class TrackWarrantEntryToOutputE2ETest {

    private static final Path OUTPUT_FORM = Path.of("src/main/resources/static/warrant-form.html");

    private static final String WARRANT_ID = "TW-E2E-001";
    private static final String TRAIN_ID = "BNSF-8899";
    private static final String START_LOCATION = "RIVER JCT";
    private static final String ISSUED_BY = "Dispatcher E2E";
    private static final String TRAIN_CREW = "ENGINEER E2E / CONDUCTOR E2E";
    private static final String LOCATION = "RIVER JCT";
    private static final String OK_TIME = "0915";
    private static final String DISPATCHER = "E2E DISPATCHER";
    private static final String COPIED_BY = "CONDUCTOR E2E";

    private static final LocalDateTime ISSUED_AT = LocalDateTime.of(2026, 3, 11, 9, 15);

    private static final List<String> REQUIRED_OUTPUT_BINDINGS = List.of(
        "document.getElementById('warrantNumber').textContent",
        "document.getElementById('trainId').textContent = warrant.trainId",
        "document.getElementById('location').textContent",
        "document.getElementById('okTime').textContent = warrant.okTime",
        "document.getElementById('dispatcher').textContent",
        "document.getElementById('copiedBy').textContent",
        "const text = structuredText || fallbackText;"
    );
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Created input values should be preserved and renderable by output form")
    void createdInputValuesShouldBePreservedAndRenderableByOutputForm() throws Exception {
        String baseUrl = "http://localhost:" + port;

        CreateWarrantRequest request = buildCreateRequest();

        ResponseEntity<TrackWarrantResponse> createResponse =
            restTemplate.postForEntity(baseUrl + "/api/warrants", request, TrackWarrantResponse.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());

        ResponseEntity<TrackWarrantResponse> getResponse =
            restTemplate.getForEntity(baseUrl + "/api/warrants/" + WARRANT_ID, TrackWarrantResponse.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        TrackWarrantResponse warrant = getResponse.getBody();
        assertNotNull(warrant);

        assertRoundTripParity(request, warrant);
        assertOutputFormBindings();
    }

    @Test
    @DisplayName("Line 3 should remain omitted when warrant includes line 2 only")
    void line3ShouldRemainOmittedWhenWarrantIncludesLine2Only() throws Exception {
        String baseUrl = "http://localhost:" + port;

        CreateWarrantRequest request = CreateWarrantRequest.builder()
            .warrantId("TW-E2E-002")
            .warrantNumber(2)
            .warrantDate("2026-03-11")
            .trainId("UP-2200")
            .trainCrew("ENGINEER UP / CONDUCTOR UP")
            .location("OMAHA YARD")
            .startingLocation("OMAHA YARD")
            .issuedDateTime(ISSUED_AT)
            .issuedBy(ISSUED_BY)
            .okTime("0915")
            .dispatcher(DISPATCHER)
            .copiedBy(COPIED_BY)
            .line2From("OMAHA YARD")
            .line2To("LINCOLN TERMINAL")
            .line2Track("NEBRASKA MAIN")
            .line6Time("18:30")
            .line7Train("UP-2200")
            .line7At("LINCOLN TERMINAL")
            .line9Ahead("LINCOLN TERMINAL")
            .line13Mph("50")
            .line13From("OMAHA YARD")
            .line13To("LINCOLN TERMINAL")
            .build();

        ResponseEntity<TrackWarrantResponse> createResponse =
            restTemplate.postForEntity(baseUrl + "/api/warrants", request, TrackWarrantResponse.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        ResponseEntity<TrackWarrantResponse> getResponse =
            restTemplate.getForEntity(baseUrl + "/api/warrants/TW-E2E-002", TrackWarrantResponse.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());

        TrackWarrantResponse warrant = getResponse.getBody();
        assertEquals("OMAHA YARD", warrant.getLine2From());
        assertEquals("LINCOLN TERMINAL", warrant.getLine2To());
        assertNull(warrant.getLine3From(), "Line 3 should remain absent when not explicitly entered");

        String outputHtml = Files.readString(OUTPUT_FORM);
        assertTrue(outputHtml.contains("case 2:"), "Expected line 2 fallback support");
        assertTrue(!outputHtml.contains("case 2:\n                case 3:")
                && !outputHtml.contains("case 2:\r\n                case 3:"),
            "Output fallback must not auto-populate line 3 from line 2");
    }

    private CreateWarrantRequest buildCreateRequest() {
        return CreateWarrantRequest.builder()
            .warrantId(WARRANT_ID)
            .warrantNumber(1)
            .warrantDate("2026-03-11")
            .trainId(TRAIN_ID)
            .trainCrew(TRAIN_CREW)
            .location(LOCATION)
            .startingLocation(START_LOCATION)
            .issuedDateTime(ISSUED_AT)
            .issuedBy(ISSUED_BY)
            .okTime(OK_TIME)
            .dispatcher(DISPATCHER)
            .copiedBy(COPIED_BY)
            .line1VoidNumber("TW-E2E-001")
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
    }

    private void assertRoundTripParity(CreateWarrantRequest request, TrackWarrantResponse warrant) {
        assertEquals(request.getWarrantId(),       warrant.getWarrantId());
        assertEquals(request.getWarrantNumber(),   warrant.getWarrantNumber());
        assertEquals(request.getWarrantDate(),     warrant.getWarrantDate());
        assertEquals(request.getTrainId(),         warrant.getTrainId());
        assertEquals(request.getTrainCrew(),       warrant.getTrainCrew());
        assertEquals(request.getLocation(),        warrant.getLocation());
        assertEquals(request.getStartingLocation(),warrant.getStartingLocation());
        assertEquals(request.getIssuedBy(),        warrant.getIssuedBy());
        assertEquals(request.getOkTime(),          warrant.getOkTime());
        assertEquals(request.getDispatcher(),      warrant.getDispatcher());
        assertEquals(request.getCopiedBy(),        warrant.getCopiedBy());
        assertEquals(request.getLine1VoidNumber(), warrant.getLine1VoidNumber());
        assertEquals(request.getLine2From(),       warrant.getLine2From());
        assertEquals(request.getLine2To(),         warrant.getLine2To());
        assertEquals(request.getLine2Track(),      warrant.getLine2Track());
        assertEquals(request.getLine3From(),       warrant.getLine3From());
        assertEquals(request.getLine3To(),         warrant.getLine3To());
        assertEquals(request.getLine3Track(),      warrant.getLine3Track());
        assertEquals(request.getLine4From(),       warrant.getLine4From());
        assertEquals(request.getLine4To(),         warrant.getLine4To());
        assertEquals(request.getLine4Track(),      warrant.getLine4Track());
        assertEquals(request.getLine5Until(),      warrant.getLine5Until());
        assertEquals(request.getLine6Time(),       warrant.getLine6Time());
        assertEquals(request.getLine7Train(),      warrant.getLine7Train());
        assertEquals(request.getLine7At(),         warrant.getLine7At());
        assertEquals(request.getLine8Checked(),    warrant.getLine8Checked());
        assertEquals(request.getLine9Ahead(),      warrant.getLine9Ahead());
        assertEquals(request.getLine10Checked(),   warrant.getLine10Checked());
        assertEquals(request.getLine11From(),      warrant.getLine11From());
        assertEquals(request.getLine11To(),        warrant.getLine11To());
        assertEquals(request.getLine12From(),      warrant.getLine12From());
        assertEquals(request.getLine12To(),        warrant.getLine12To());
        assertEquals(request.getLine13Mph(),       warrant.getLine13Mph());
        assertEquals(request.getLine13From(),      warrant.getLine13From());
        assertEquals(request.getLine13To(),        warrant.getLine13To());
        assertEquals(request.getLine14Mph(),       warrant.getLine14Mph());
        assertEquals(request.getLine14From(),      warrant.getLine14From());
        assertEquals(request.getLine14To(),        warrant.getLine14To());
        assertEquals(request.getLine15Checked(),   warrant.getLine15Checked());
        assertEquals(request.getLine16Text(),      warrant.getLine16Text());
        assertEquals(request.getLine17Text(),      warrant.getLine17Text());
    }

    private void assertOutputFormBindings() throws Exception {
        String outputHtml = Files.readString(OUTPUT_FORM);
        for (String binding : REQUIRED_OUTPUT_BINDINGS) {
            assertTrue(outputHtml.contains(binding), "Missing output binding: " + binding);
        }
    }
}
