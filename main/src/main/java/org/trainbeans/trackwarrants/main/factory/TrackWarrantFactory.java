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
            .line1VoidNumber(trimToNull(request.getLine1VoidNumber()))
            .line2From(trimToNull(request.getLine2From()))
            .line2To(trimToNull(request.getLine2To()))
            .line2Track(trimToNull(request.getLine2Track()))
            .line3From(trimToNull(request.getLine3From()))
            .line3To(trimToNull(request.getLine3To()))
            .line3Track(trimToNull(request.getLine3Track()))
            .line4From(trimToNull(request.getLine4From()))
            .line4To(trimToNull(request.getLine4To()))
            .line4Track(trimToNull(request.getLine4Track()))
            .line5Until(trimToNull(request.getLine5Until()))
            .line6Time(trimToNull(request.getLine6Time()))
            .line7Train(trimToNull(request.getLine7Train()))
            .line7At(trimToNull(request.getLine7At()))
            .line8Checked(Boolean.TRUE.equals(request.getLine8Checked()) ? Boolean.TRUE : null)
            .line9Ahead(trimToNull(request.getLine9Ahead()))
            .line10Checked(Boolean.TRUE.equals(request.getLine10Checked()) ? Boolean.TRUE : null)
            .line11From(trimToNull(request.getLine11From()))
            .line11To(trimToNull(request.getLine11To()))
            .line12From(trimToNull(request.getLine12From()))
            .line12To(trimToNull(request.getLine12To()))
            .line13Mph(trimToNull(request.getLine13Mph()))
            .line13From(trimToNull(request.getLine13From()))
            .line13To(trimToNull(request.getLine13To()))
            .line14Mph(trimToNull(request.getLine14Mph()))
            .line14From(trimToNull(request.getLine14From()))
            .line14To(trimToNull(request.getLine14To()))
            .line15Checked(Boolean.TRUE.equals(request.getLine15Checked()) ? Boolean.TRUE : null)
            .line16Text(trimToNull(request.getLine16Text()))
            .line17Text(trimToNull(request.getLine17Text()))
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
