package org.trainbeans.trackwarrants.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA Entity representing a railroad track warrant.
 * Authorizes a train to occupy a specific section of track for a designated time period.
 */
@Entity
@Table(name = "track_warrants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackWarrant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String warrantId;

    @Column(nullable = false)
    private Integer warrantNumber;

    @Column(nullable = false)
    private String warrantDate;   // yyyy-MM-dd

    @Column(nullable = false)
    private String trainId;

    private String trainCrew;     // "TO:" field

    private String location;      // "AT:" field

    @Column(nullable = false)
    private String startingLocation;

    @Column(nullable = false)
    private LocalDateTime issuedDateTime;

    private String issuedBy;

    // Footer fields
    private String okTime;          // "OK ___M"
    private String dispatcher;      // "DISPATCHER ___"
    private String relayedTo;       // "RELAYED TO ___"
    private String copiedBy;        // "COPIED BY ___"
    private String limitsClearAt;   // "LIMITS REPORTED CLEAR AT ___"
    private String limitsClearBy;   // "BY ___"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WarrantStatus status;

    // Line-driven persisted instruction storage (fill-in values only, no boilerplate).
    // Line 1: "Track Warrant NO. ___ is Void."
    private String line1VoidNumber;
    // Line 2: "Proceed from ___ to ___ on ___ Track"
    private String line2From;
    private String line2To;
    private String line2Track;
    // Line 3: "Proceed from ___ to ___ on ___ Track"
    private String line3From;
    private String line3To;
    private String line3Track;
    // Line 4: "Work between ___ and ___ on ___ Track"
    private String line4From;
    private String line4To;
    private String line4Track;
    // Line 5: "Not in effect until ___M"
    private String line5Until;
    // Line 6: "This Authority Expires at ___M"
    private String line6Time;
    // Line 7: "Not in effect until after arrival of ___ at ___"
    private String line7Train;
    private String line7At;
    // Line 8: "Hold Main Track at Last Named Point." (fixed text — boolean flag only)
    private Boolean line8Checked;
    // Line 9: "Do Not Foul Limints ahead of ___."
    private String line9Ahead;
    // Line 10: "Clear Main Track at Last Named Point." (fixed text — boolean flag only)
    private Boolean line10Checked;
    // Line 11: "Between ___ and ___ Make All Movements at restricted speed. Limits occupied by train or engine."
    private String line11From;
    private String line11To;
    // Line 12: "Between ___ and ___ Make All Movements at restricted speed and stop short of Men or Machines fouling track."
    private String line12From;
    private String line12To;
    // Line 13: "Do not exceed ___ MPH between ___ and ___"
    private String line13Mph;
    private String line13From;
    private String line13To;
    // Line 14: "Do not exceed ___ MPH between ___ and ___"
    private String line14Mph;
    private String line14From;
    private String line14To;
    // Line 15: "Protection as prescribed by Rule 99 not required." (fixed text — boolean flag only)
    private Boolean line15Checked;
    // Line 16: "Track Bulletins in Effect: ___"
    @Column(length = 500)
    private String line16Text;
    // Line 17: "Other Specific Instructions: ___"
    @Column(length = 500)
    private String line17Text;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDateTime;

    private LocalDateTime lastModifiedDateTime;

    @PrePersist
    protected void onCreate() {
        createdDateTime = LocalDateTime.now();
        lastModifiedDateTime = LocalDateTime.now();
        if (status == null) {
            status = WarrantStatus.ACTIVE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDateTime = LocalDateTime.now();
    }

    /**
     * Enumeration for track warrant status
     */
    public enum WarrantStatus {
        ACTIVE,
        EXPIRED,
        CANCELLED,
        COMPLETED
    }
}
