package org.trainbeans.trackwarrants.main.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for creating a new track warrant.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWarrantRequest {

    private String warrantId;
    private Integer warrantNumber;
    private String warrantDate;
    private String trainId;
    private String trainCrew;
    private String location;
    private String startingLocation;
    private LocalDateTime issuedDateTime;
    private String issuedBy;

    // Footer fields
    private String okTime;
    private String dispatcher;
    private String relayedTo;
    private String copiedBy;
    private String limitsClearAt;
    private String limitsClearBy;

    private String line1Instruction;
    private String line2Instruction;
    private String line3Instruction;
    private String line4Instruction;
    private String line5Instruction;
    private String line6Instruction;
    private String line7Instruction;
    private String line8Instruction;
    private String line9Instruction;
    private String line10Instruction;
    private String line11Instruction;
    private String line12Instruction;
    private String line13Instruction;
    private String line14Instruction;
    private String line15Instruction;
    private String line16Instruction;
    private String line17Instruction;
}
