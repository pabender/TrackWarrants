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

    // Line-driven persisted instruction storage (one field per line).
    @Column(length = 500)
    private String line1Instruction;
    @Column(length = 500)
    private String line2Instruction;
    @Column(length = 500)
    private String line3Instruction;
    @Column(length = 500)
    private String line4Instruction;
    @Column(length = 500)
    private String line5Instruction;
    @Column(length = 500)
    private String line6Instruction;
    @Column(length = 500)
    private String line7Instruction;
    @Column(length = 500)
    private String line8Instruction;
    @Column(length = 500)
    private String line9Instruction;
    @Column(length = 500)
    private String line10Instruction;
    @Column(length = 500)
    private String line11Instruction;
    @Column(length = 500)
    private String line12Instruction;
    @Column(length = 500)
    private String line13Instruction;
    @Column(length = 500)
    private String line14Instruction;
    @Column(length = 500)
    private String line15Instruction;
    @Column(length = 500)
    private String line16Instruction;
    @Column(length = 500)
    private String line17Instruction;


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
