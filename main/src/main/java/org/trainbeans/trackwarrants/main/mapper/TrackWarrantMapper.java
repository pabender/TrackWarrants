package org.trainbeans.trackwarrants.main.mapper;

import org.springframework.stereotype.Component;
import org.trainbeans.trackwarrants.main.dto.TrackWarrantResponse;
import org.trainbeans.trackwarrants.main.entity.TrackWarrant;

/**
 * Mapper component for converting TrackWarrant entities to DTOs.
 * Centralizes mapping logic following Single Responsibility Principle.
 */
@Component
public class TrackWarrantMapper {

    /**
     * Convert TrackWarrant entity to response DTO.
     *
     * @param entity the TrackWarrant entity
     * @return the response DTO
     */
    public TrackWarrantResponse toResponse(TrackWarrant entity) {
        if (entity == null) {
            return null;
        }

        return TrackWarrantResponse.builder()
            .id(entity.getId())
            .warrantId(entity.getWarrantId())
            .warrantNumber(entity.getWarrantNumber())
            .warrantDate(entity.getWarrantDate())
            .trainId(entity.getTrainId())
            .trainCrew(entity.getTrainCrew())
            .location(entity.getLocation())
            .startingLocation(entity.getStartingLocation())
            .issuedDateTime(entity.getIssuedDateTime())
            .status(entity.getStatus() != null ? entity.getStatus().name() : null)
            .issuedBy(entity.getIssuedBy())
            .okTime(entity.getOkTime())
            .dispatcher(entity.getDispatcher())
            .relayedTo(entity.getRelayedTo())
            .copiedBy(entity.getCopiedBy())
            .limitsClearAt(entity.getLimitsClearAt())
            .limitsClearBy(entity.getLimitsClearBy())
            .line1VoidNumber(entity.getLine1VoidNumber())
            .line2From(entity.getLine2From())
            .line2To(entity.getLine2To())
            .line2Track(entity.getLine2Track())
            .line3From(entity.getLine3From())
            .line3To(entity.getLine3To())
            .line3Track(entity.getLine3Track())
            .line4From(entity.getLine4From())
            .line4To(entity.getLine4To())
            .line4Track(entity.getLine4Track())
            .line5Until(entity.getLine5Until())
            .line6Time(entity.getLine6Time())
            .line7Train(entity.getLine7Train())
            .line7At(entity.getLine7At())
            .line8Checked(entity.getLine8Checked())
            .line9Ahead(entity.getLine9Ahead())
            .line10Checked(entity.getLine10Checked())
            .line11From(entity.getLine11From())
            .line11To(entity.getLine11To())
            .line12From(entity.getLine12From())
            .line12To(entity.getLine12To())
            .line13Mph(entity.getLine13Mph())
            .line13From(entity.getLine13From())
            .line13To(entity.getLine13To())
            .line14Mph(entity.getLine14Mph())
            .line14From(entity.getLine14From())
            .line14To(entity.getLine14To())
            .line15Checked(entity.getLine15Checked())
            .line16Text(entity.getLine16Text())
            .line17Text(entity.getLine17Text())
            .createdDateTime(entity.getCreatedDateTime())
            .lastModifiedDateTime(entity.getLastModifiedDateTime())
            .build();
    }
}
