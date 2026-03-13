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
            .line2Instruction("Proceed from OMAHA YARD to LINCOLN TERMINAL on NEBRASKA MAIN Track")
            .line6Instruction("This Authority Expires at 18:30M")
            .line7Instruction("Not in effect until after arrival of UP-2200 at LINCOLN TERMINAL")
            .line9Instruction("Do Not Foul Limints ahead of LINCOLN TERMINAL.")
            .line13Instruction("Do not exceed 50 MPH between OMAHA YARD and LINCOLN TERMINAL")
            .build();

        ResponseEntity<TrackWarrantResponse> createResponse =
            restTemplate.postForEntity(baseUrl + "/api/warrants", request, TrackWarrantResponse.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        ResponseEntity<TrackWarrantResponse> getResponse =
            restTemplate.getForEntity(baseUrl + "/api/warrants/TW-E2E-002", TrackWarrantResponse.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());

        TrackWarrantResponse warrant = getResponse.getBody();
        assertEquals("Proceed from OMAHA YARD to LINCOLN TERMINAL on NEBRASKA MAIN Track", warrant.getLine2Instruction());
        assertNull(warrant.getLine3Instruction(), "Line 3 should remain absent when not explicitly entered");

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
            .line1Instruction("Track Warrant NO. TW-E2E-001 is Void.")
            .line2Instruction("Proceed from RIVER JCT to NORTH YARD on MAIN Track")
            .line3Instruction("Proceed from RIVER JCT to NORTH YARD on MAIN Track")
            .line4Instruction("Work between SIDING A and SIDING B on SIDING Track")
            .line5Instruction("Not in effect until 09:45M")
            .line6Instruction("This Authority Expires at 17:30M")
            .line7Instruction("Not in effect until after arrival of TRAIN 12 at WEST JCT")
            .line8Instruction("Hold Main Track at Last Named Point.")
            .line9Instruction("Do Not Foul Limints ahead of WEST JCT.")
            .line10Instruction("Clear Main Track at Last Named Point.")
            .line11Instruction("Between MP 10 and MP 12 Make All Movements at restricted speed. Limits occupied by train or engine.")
            .line12Instruction("Between MP 9 and MP 13 Make All Movements at restricted speed and stop short of Men or Machines fouling track.")
            .line13Instruction("Do not exceed 45 MPH between RIVER JCT and NORTH YARD")
            .line14Instruction("Do not exceed 45 MPH between SIDING A and SIDING B")
            .line15Instruction("Protection as prescribed by Rule 99 not required.")
            .line16Instruction("Track Bulletins in Effect: TB-01, TB-02, TB-03")
            .line17Instruction("Other Specific Instructions: Reduce speed through work zone")
            .build();
    }

    private void assertRoundTripParity(CreateWarrantRequest request, TrackWarrantResponse warrant) {
        assertEquals(request.getWarrantId(),      warrant.getWarrantId());
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
        assertEquals(request.getLine1Instruction(),  warrant.getLine1Instruction());
        assertEquals(request.getLine2Instruction(),  warrant.getLine2Instruction());
        assertEquals(request.getLine3Instruction(),  warrant.getLine3Instruction());
        assertEquals(request.getLine4Instruction(),  warrant.getLine4Instruction());
        assertEquals(request.getLine5Instruction(),  warrant.getLine5Instruction());
        assertEquals(request.getLine6Instruction(),  warrant.getLine6Instruction());
        assertEquals(request.getLine7Instruction(),  warrant.getLine7Instruction());
        assertEquals(request.getLine8Instruction(),  warrant.getLine8Instruction());
        assertEquals(request.getLine9Instruction(),  warrant.getLine9Instruction());
        assertEquals(request.getLine10Instruction(), warrant.getLine10Instruction());
        assertEquals(request.getLine11Instruction(), warrant.getLine11Instruction());
        assertEquals(request.getLine12Instruction(), warrant.getLine12Instruction());
        assertEquals(request.getLine13Instruction(), warrant.getLine13Instruction());
        assertEquals(request.getLine14Instruction(), warrant.getLine14Instruction());
        assertEquals(request.getLine15Instruction(), warrant.getLine15Instruction());
        assertEquals(request.getLine16Instruction(), warrant.getLine16Instruction());
        assertEquals(request.getLine17Instruction(), warrant.getLine17Instruction());
    }

    private void assertOutputFormBindings() throws Exception {
        String outputHtml = Files.readString(OUTPUT_FORM);
        for (String binding : REQUIRED_OUTPUT_BINDINGS) {
            assertTrue(outputHtml.contains(binding), "Missing output binding: " + binding);
        }
    }
}
