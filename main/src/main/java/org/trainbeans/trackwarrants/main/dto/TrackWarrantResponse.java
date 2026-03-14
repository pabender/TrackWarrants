package org.trainbeans.trackwarrants.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for track warrant responses.
 * Pure data structure - mapping logic moved to TrackWarrantMapper component.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackWarrantResponse {

    private Long id;
    private String warrantId;
    private Integer warrantNumber;
    private String warrantDate;
    private String trainId;
    private String trainCrew;
    private String location;
    private String startingLocation;
    private LocalDateTime issuedDateTime;
    private String status;
    private String issuedBy;

    // Footer fields
    private String okTime;
    private String dispatcher;
    private String relayedTo;
    private String copiedBy;
    private String limitsClearAt;
    private String limitsClearBy;

    // Line fill-in values (boilerplate text lives in the UI templates, not here)
    private String line1VoidNumber;
    private String line2From;
    private String line2To;
    private String line2Track;
    private String line3From;
    private String line3To;
    private String line3Track;
    private String line4From;
    private String line4To;
    private String line4Track;
    private String line5Until;
    private String line6Time;
    private String line7Train;
    private String line7At;
    private Boolean line8Checked;
    private String line9Ahead;
    private Boolean line10Checked;
    private String line11From;
    private String line11To;
    private String line12From;
    private String line12To;
    private String line13Mph;
    private String line13From;
    private String line13To;
    private String line14Mph;
    private String line14From;
    private String line14To;
    private Boolean line15Checked;
    private String line16Text;
    private String line17Text;

    private LocalDateTime createdDateTime;
    private LocalDateTime lastModifiedDateTime;
}
