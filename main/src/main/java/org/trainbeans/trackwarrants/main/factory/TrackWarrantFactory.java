package org.trainbeans.trackwarrants.main.factory;

import org.springframework.stereotype.Component;
import org.trainbeans.trackwarrants.main.dto.CreateWarrantRequest;
import org.trainbeans.trackwarrants.main.entity.TrackWarrant;

import java.time.LocalDateTime;

/**
 * Factory component for creating TrackWarrant entities.
 * Centralizes entity construction logic following Single Responsibility Principle.
 */
@Component
public class TrackWarrantFactory {

    /**
     * Create a new TrackWarrant entity from a request.
     *
     * @param request the creation request
     * @return a new TrackWarrant entity with ACTIVE status
     */
    public TrackWarrant create(CreateWarrantRequest request) {
        return TrackWarrant.builder()
            .warrantId(request.getWarrantId())
            .warrantNumber(request.getWarrantNumber())
            .warrantDate(request.getWarrantDate())
            .trainId(request.getTrainId())
            .trainCrew(trimToNull(request.getTrainCrew()))
            .location(trimToNull(request.getLocation()))
            .startingLocation(request.getStartingLocation())
            .issuedDateTime(request.getIssuedDateTime() != null ? request.getIssuedDateTime() : LocalDateTime.now())
            .status(TrackWarrant.WarrantStatus.ACTIVE)
            .issuedBy(trimToNull(request.getIssuedBy()))
            .okTime(trimToNull(request.getOkTime()))
            .dispatcher(trimToNull(request.getDispatcher()))
            .relayedTo(trimToNull(request.getRelayedTo()))
            .copiedBy(trimToNull(request.getCopiedBy()))
            .limitsClearAt(trimToNull(request.getLimitsClearAt()))
            .limitsClearBy(trimToNull(request.getLimitsClearBy()))
            .line1Instruction(trimToNull(request.getLine1Instruction()))
            .line2Instruction(trimToNull(request.getLine2Instruction()))
            .line3Instruction(trimToNull(request.getLine3Instruction()))
            .line4Instruction(trimToNull(request.getLine4Instruction()))
            .line5Instruction(trimToNull(request.getLine5Instruction()))
            .line6Instruction(trimToNull(request.getLine6Instruction()))
            .line7Instruction(trimToNull(request.getLine7Instruction()))
            .line8Instruction(trimToNull(request.getLine8Instruction()))
            .line9Instruction(trimToNull(request.getLine9Instruction()))
            .line10Instruction(trimToNull(request.getLine10Instruction()))
            .line11Instruction(trimToNull(request.getLine11Instruction()))
            .line12Instruction(trimToNull(request.getLine12Instruction()))
            .line13Instruction(trimToNull(request.getLine13Instruction()))
            .line14Instruction(trimToNull(request.getLine14Instruction()))
            .line15Instruction(trimToNull(request.getLine15Instruction()))
            .line16Instruction(trimToNull(request.getLine16Instruction()))
            .line17Instruction(trimToNull(request.getLine17Instruction()))
            .build();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
