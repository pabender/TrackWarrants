package org.trainbeans.trackwarrants.main.service;

import org.trainbeans.trackwarrants.main.entity.TrackWarrant;

import java.util.List;
import java.util.Optional;

/**
 * Query use cases for track warrant management (read operations).
 * Segregated from command operations following Interface Segregation Principle.
 */
public interface TrackWarrantQueryUseCase {

    /**
     * Get all track warrants.
     */
    List<TrackWarrant> getAllWarrants();

    /**
     * Get a track warrant by warrant ID.
     */
    Optional<TrackWarrant> getWarrantByWarrantId(String warrantId);

    /**
     * Get a track warrant by database ID.
     */
    Optional<TrackWarrant> getWarrantById(Long id);

    /**
     * Get all active track warrants.
     */
    List<TrackWarrant> getActiveWarrants();

    /**
     * Get all track warrants for a specific train.
     */
    List<TrackWarrant> getWarrantsByTrainId(String trainId);
}

