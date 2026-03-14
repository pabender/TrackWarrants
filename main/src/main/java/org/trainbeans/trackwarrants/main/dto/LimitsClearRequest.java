package org.trainbeans.trackwarrants.main.dto;

import lombok.Data;

/**
 * Request DTO for recording limits reported clear on a track warrant.
 */
@Data
public class LimitsClearRequest {
    private String limitsClearAt;
    private String limitsClearBy;
}
