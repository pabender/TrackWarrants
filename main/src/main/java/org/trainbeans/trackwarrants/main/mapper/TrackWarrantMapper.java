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
            .line1Instruction(entity.getLine1Instruction())
            .line2Instruction(entity.getLine2Instruction())
            .line3Instruction(entity.getLine3Instruction())
            .line4Instruction(entity.getLine4Instruction())
            .line5Instruction(entity.getLine5Instruction())
            .line6Instruction(entity.getLine6Instruction())
            .line7Instruction(entity.getLine7Instruction())
            .line8Instruction(entity.getLine8Instruction())
            .line9Instruction(entity.getLine9Instruction())
            .line10Instruction(entity.getLine10Instruction())
            .line11Instruction(entity.getLine11Instruction())
            .line12Instruction(entity.getLine12Instruction())
            .line13Instruction(entity.getLine13Instruction())
            .line14Instruction(entity.getLine14Instruction())
            .line15Instruction(entity.getLine15Instruction())
            .line16Instruction(entity.getLine16Instruction())
            .line17Instruction(entity.getLine17Instruction())
            .createdDateTime(entity.getCreatedDateTime())
            .lastModifiedDateTime(entity.getLastModifiedDateTime())
            .build();
    }
}
