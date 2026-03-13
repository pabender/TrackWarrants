package org.trainbeans.trackwarrants.main.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract tests for line-driven form parity between entry and output pages.
 */
@DisplayName("Track Warrant Form Contract Tests")
class TrackWarrantFormContractTest {

    private static final Path ENTRY_FORM = Path.of("src/main/resources/static/index.html");
    private static final Path OUTPUT_FORM = Path.of("src/main/resources/static/warrant-form.html");

    @Test
    @DisplayName("Entry form should contain all line-driven input fields")
    void entryFormShouldContainAllExpectedInputs() throws IOException {
        String html = Files.readString(ENTRY_FORM);

        List<String> headerIds = List.of("warrantNumber", "warrantDate", "trainCrew", "location", "trainId",
                                          "okTime", "dispatcher", "copiedBy");
        List<String> lineValueIds = List.of(
            "l1_void_number",
            "l2_from", "l2_to", "l2_track",
            "l3_from", "l3_to", "l3_track",
            "l4_from", "l4_to", "l4_track",
            "l5_until",
            "l6_time",
            "l7_train", "l7_at",
            "l9_ahead",
            "l11_from", "l11_to",
            "l12_from", "l12_to",
            "l13_mph", "l13_from", "l13_to",
            "l14_mph", "l14_from", "l14_to",
            "l16_b1", "l16_b2", "l16_b3", "l16_b4", "l16_b5", "l16_b6", "l16_b7", "l16_b8",
            "l16_b9", "l16_b10", "l16_b11", "l16_b12", "l16_b13", "l16_b14", "l16_b15", "l16_b16",
            "l17_text"
        );

        for (String id : headerIds) {
            assertTrue(html.contains("id=\"" + id + "\""), "Missing entry header input: " + id);
        }
        for (String id : lineValueIds) {
            assertTrue(html.contains("id=\"" + id + "\""), "Missing entry line input: " + id);
        }
        IntStream.rangeClosed(1, 17).forEach(i ->
            assertTrue(html.contains("id=\"l" + i + "_on\""), "Missing entry checkbox: l" + i + "_on"));
    }

    @Test
    @DisplayName("Entry form should build all line texts 1-14")
    void entryFormShouldBuildAllLineTexts() throws IOException {
        String html = Files.readString(ENTRY_FORM);

        List<String> prefixes = List.of(
            "Track Warrant NO.",
            "Proceed from",
            "Work between",
            "Not in effect until",
            "This Authority Expires at",
            "Not in effect until after arrival of",
            "Hold Main Track at Last Named Point.",
            "Do Not Foul Limints ahead of",
            "Clear Main Track at Last Named Point.",
            "Make All Movements at restricted speed. Limits occupied by train or engine.",
            "Make All Movements at restricted speed and stop short of Men or Machines fouling track.",
            "Do not exceed",
            "Protection as prescribed by Rule 99 not required.",
            "Track Bulletins in Effect:",
            "Other Specific Instructions:"
        );

        for (String prefix : prefixes) {
            assertTrue(html.contains(prefix), "Missing line text prefix in entry form script: " + prefix);
        }

        assertTrue(html.contains("function deriveFromLineDriven()"), "deriveFromLineDriven function missing in entry form");
    }

    @Test
    @DisplayName("Entry form should auto-check line when line data is populated")
    void entryFormShouldAutoCheckLineWhenDataIsPopulated() throws IOException {
        String html = Files.readString(ENTRY_FORM);

        assertTrue(html.contains("function syncLineChecksFromData()"), "syncLineChecksFromData function missing");
        assertTrue(html.contains("function attachLineAutoCheckHandlers()"), "attachLineAutoCheckHandlers function missing");
        assertTrue(html.contains(".line-text input[id^=\"l\"]"), "Line input selector missing for auto-check sync");
        assertTrue(html.contains("check.checked = true"), "Auto-check assignment missing");
        assertTrue(html.contains("lineInput.addEventListener('input'"), "Input listener missing for auto-check behavior");
        assertTrue(html.contains("function deriveFromLineDriven() {"), "deriveFromLineDriven function missing");
        assertTrue(html.contains("syncLineChecksFromData();"),
            "deriveFromLineDriven should sync checkboxes from populated line data before building line fields");
        assertTrue(html.contains("const lineValues = {"),
            "deriveFromLineDriven should build structured line fields");
    }

    @Test
    @DisplayName("Entry form inputs should map to output form rendering")
    void entryInputsShouldMapToOutputRendering() throws IOException {
        String entryHtml = Files.readString(ENTRY_FORM);
        String outputHtml = Files.readString(OUTPUT_FORM);

        // Header field bindings expected in output renderer.
        assertTrue(outputHtml.contains("warrant.warrantId"), "Output missing warrant number binding");
        assertTrue(outputHtml.contains("warrant.trainId"), "Output missing train id binding");
        assertTrue(outputHtml.contains("warrant.issuedBy"), "Output missing issuedBy binding");
        assertTrue(outputHtml.contains("warrant.location"), "Output missing location binding");
        assertTrue(outputHtml.contains("warrant.issuedDateTime"), "Output missing issued date/time binding");
        assertTrue(outputHtml.contains("warrant.okTime"), "Output missing okTime binding");
        assertTrue(outputHtml.contains("warrant.dispatcher"), "Output missing dispatcher binding");
        assertTrue(outputHtml.contains("warrant.copiedBy"), "Output missing copiedBy binding");

        // Canonical line phrases defined by entry form must exist in output line templates.
        List<String> canonicalLinePhrases = List.of(
            "Track Warrant NO.",
            "Proceed from",
            "Work between",
            "Not in effect until",
            "This Authority Expires at",
            "Not in effect until after arrival of",
            "Hold Main Track at Last Named Point.",
            "Do Not Foul Limints ahead of",
            "Clear Main Track at Last Named Point.",
            "Make All Movements at restricted speed. Limits occupied by train or engine.",
            "Make All Movements at restricted speed and stop short of Men or Machines fouling track.",
            "Do not exceed",
            "Protection as prescribed by Rule 99 not required.",
            "Track Bulletins in Effect:",
            "Other Specific Instructions:"
        );

        for (String phrase : canonicalLinePhrases) {
            assertTrue(entryHtml.contains(phrase), "Entry form missing canonical phrase: " + phrase);
            assertTrue(outputHtml.contains(phrase), "Output form missing canonical phrase: " + phrase);
        }

        // Ensure output prefers structured line fields and falls back only to derived defaults.
        assertTrue(outputHtml.contains("const text = structuredText || fallbackText;"),
            "Output should prefer structured line text, then fallback");
    }

    @Test
    @DisplayName("Output form should define and render lines 1-14")
    void outputFormShouldDefineAndRenderAllLines() throws IOException {
        String html = Files.readString(OUTPUT_FORM);

        IntStream.rangeClosed(1, 17).forEach(i ->
            assertTrue(html.contains("{ num: " + i + ", text:"), "Missing output template line " + i));

        assertTrue(html.contains("const WARRANT_LINES = ["), "WARRANT_LINES definition missing");
        assertTrue(html.contains("function renderBlankForm()"), "renderBlankForm missing");
        assertTrue(html.contains("function fallbackLine(num, warrant)"), "fallbackLine missing");
        assertTrue(html.contains("function displayWarrant(warrant)"), "displayWarrant missing");
        assertTrue(html.contains("line-checkbox"), "Output checkbox rendering missing");
    }

    @Test
    @DisplayName("Output form should support 58mm thermal receipt print mode")
    void outputFormShouldSupportReceiptPrintMode() throws IOException {
        String html = Files.readString(OUTPUT_FORM);

        assertTrue(html.contains("Print 58mm Receipt"), "Missing 58mm receipt print action");
        assertTrue(html.contains("58mm Preview"), "Missing 58mm preview action");
        assertTrue(html.contains("const requestedPrintMode = urlParams.get('printMode') || 'standard';"),
            "Missing printMode query parameter support");
        assertTrue(html.contains("function setPrintMode(mode)"), "Missing setPrintMode function");
        assertTrue(html.contains("function applyPageSizeStyle()"), "Missing applyPageSizeStyle function");
        assertTrue(html.contains("function printInMode(mode)"), "Missing printInMode function");
        assertTrue(html.contains("receipt-mode"), "Missing receipt-mode CSS hook");
        assertTrue(html.contains("@page { size: 58mm auto; margin: 2mm 2mm 2mm 4mm; }"),
            "Missing 58mm page sizing rule");
    }

    @Test
    @DisplayName("Output fallback should not auto-populate line 3 from line 2 data")
    void outputFallbackShouldNotAutoPopulateLine3() throws IOException {
        String html = Files.readString(OUTPUT_FORM);

        assertTrue(html.contains("case 2:"), "Expected fallback case for line 2");
        assertTrue(!html.contains("case 2:\n                case 3:")
                && !html.contains("case 2:\r\n                case 3:"),
            "Line 3 should not share line 2 fallback population");
    }
}
