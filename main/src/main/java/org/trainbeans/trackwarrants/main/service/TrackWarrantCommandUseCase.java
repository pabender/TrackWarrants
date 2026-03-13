package org.trainbeans.trackwarrants.main.service;

import org.trainbeans.trackwarrants.main.dto.CreateWarrantRequest;
import org.trainbeans.trackwarrants.main.entity.TrackWarrant;

/**
 * Command use cases for track warrant management (write operations).
 * Segregated from query operations following Interface Segregation Principle.
 */
public interface TrackWarrantCommandUseCase {

    /**
     * Create a new track warrant.
     */
    TrackWarrant createWarrant(CreateWarrantRequest request);

    /**
     * Mark a track warrant as completed.
     */
    TrackWarrant completeWarrant(String warrantId);

    /**
     * Update warrant status.
     */
    TrackWarrant updateWarrantStatus(String warrantId, TrackWarrant.WarrantStatus status);

    /**
     * Cancel a track warrant.
     */
    TrackWarrant cancelWarrant(String warrantId);

    /**
     * Delete a track warrant by warrant ID.
     */
    void deleteWarrant(String warrantId);
}

