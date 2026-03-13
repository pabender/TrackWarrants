package org.trainbeans.trackwarrants.main.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.trainbeans.trackwarrants.main.entity.TrackWarrant;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TrackWarrant entities.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface TrackWarrantRepository extends JpaRepository<TrackWarrant, Long> {

    /**
     * Find a track warrant by its warrant ID.
     */
    Optional<TrackWarrant> findByWarrantId(String warrantId);

    /**
     * Find all track warrants for a specific train.
     */
    List<TrackWarrant> findByTrainId(String trainId);

    /**
     * Find all track warrants with a specific status.
     */
    List<TrackWarrant> findByStatus(TrackWarrant.WarrantStatus status);

    /**
     * Find all warrants for a given date (yyyy-MM-dd) — used for daily numbering.
     */
    List<TrackWarrant> findByWarrantDate(String warrantDate);

    /**
     * Find all active track warrants.
     */
    default List<TrackWarrant> findActiveWarrants() {
        return findByStatus(TrackWarrant.WarrantStatus.ACTIVE);
    }
}

