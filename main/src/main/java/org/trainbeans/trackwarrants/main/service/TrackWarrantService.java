package org.trainbeans.trackwarrants.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.trainbeans.trackwarrants.main.dto.CreateWarrantRequest;
import org.trainbeans.trackwarrants.main.entity.TrackWarrant;
import org.trainbeans.trackwarrants.main.exception.InvalidWarrantTransitionException;
import org.trainbeans.trackwarrants.main.exception.WarrantNotFoundException;
import org.trainbeans.trackwarrants.main.factory.TrackWarrantFactory;
import org.trainbeans.trackwarrants.main.repository.TrackWarrantRepository;
import org.trainbeans.trackwarrants.main.validation.TrackWarrantValidator;

import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing track warrants.
 * Orchestrates business logic using validator, factory, and policy components.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TrackWarrantService implements TrackWarrantUseCase {

    private final TrackWarrantRepository repository;
    private final WarrantStatusTransitionPolicy transitionPolicy;
    private final TrackWarrantValidator validator;
    private final TrackWarrantFactory factory;

    /**
     * Create a new track warrant.
     */
    @Override
    public TrackWarrant createWarrant(CreateWarrantRequest request) {
        log.info("Creating new track warrant: {}", request.getWarrantId());

        // Delegate validation to validator component
        validator.validateCreate(request);

        // Delegate entity creation to factory component
        TrackWarrant warrant = factory.create(request);

        TrackWarrant saved = repository.save(warrant);
        log.info("Track warrant created successfully: {}", saved.getWarrantId());
        return saved;
    }

    /**
     * Get all track warrants.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TrackWarrant> getAllWarrants() {
        log.debug("Retrieving all track warrants");
        return repository.findAll();
    }

    /**
     * Get a track warrant by warrant ID.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TrackWarrant> getWarrantByWarrantId(String warrantId) {
        log.debug("Retrieving track warrant: {}", warrantId);
        return repository.findByWarrantId(warrantId);
    }

    /**
     * Get a track warrant by database ID.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TrackWarrant> getWarrantById(Long id) {
        log.debug("Retrieving track warrant by ID: {}", id);
        return repository.findById(id);
    }

    /**
     * Get all active track warrants.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TrackWarrant> getActiveWarrants() {
        log.debug("Retrieving active track warrants");
        return repository.findByStatus(TrackWarrant.WarrantStatus.ACTIVE);
    }

    /**
     * Get all track warrants for a specific train.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TrackWarrant> getWarrantsByTrainId(String trainId) {
        log.debug("Retrieving track warrants for train: {}", trainId);
        return repository.findByTrainId(trainId);
    }

    /**
     * Mark a track warrant as void.
     */
    @Override
    public TrackWarrant completeWarrant(String warrantId) {
        log.info("Voiding track warrant: {}", warrantId);
        TrackWarrant updated = updateWarrantStatus(warrantId, TrackWarrant.WarrantStatus.VOID);
        log.info("Track warrant voided: {}", warrantId);
        return updated;
    }

    /**
     * Update warrant status.
     */
    @Override
    public TrackWarrant updateWarrantStatus(String warrantId, TrackWarrant.WarrantStatus status) {
        log.debug("Updating warrant {} status to {}", warrantId, status);

        TrackWarrant warrant = repository.findByWarrantId(warrantId)
            .orElseThrow(() -> new WarrantNotFoundException(warrantId));

        TrackWarrant.WarrantStatus current = warrant.getStatus();
        if (!transitionPolicy.isAllowed(current, status)) {
            throw new InvalidWarrantTransitionException(current, status);
        }

        warrant.setStatus(status);
        return repository.save(warrant);
    }

    /**
     * Record limits reported clear and mark warrant as void.
     */
    public TrackWarrant recordLimitsClear(String warrantId, String limitsClearAt, String limitsClearBy) {
        log.info("Recording limits clear for warrant: {}", warrantId);

        TrackWarrant warrant = repository.findByWarrantId(warrantId)
            .orElseThrow(() -> new WarrantNotFoundException(warrantId));

        TrackWarrant.WarrantStatus current = warrant.getStatus();
        if (!transitionPolicy.isAllowed(current, TrackWarrant.WarrantStatus.VOID)) {
            throw new InvalidWarrantTransitionException(current, TrackWarrant.WarrantStatus.VOID);
        }

        warrant.setLimitsClearAt(limitsClearAt);
        warrant.setLimitsClearBy(limitsClearBy);
        warrant.setStatus(TrackWarrant.WarrantStatus.VOID);

        TrackWarrant saved = repository.save(warrant);
        log.info("Limits clear recorded and warrant voided: {}", warrantId);
        return saved;
    }

    /**
     * Delete a track warrant by warrant ID.
     */
    @Override
    public void deleteWarrant(String warrantId) {
        log.debug("Deleting track warrant: {}", warrantId);

        TrackWarrant warrant = repository.findByWarrantId(warrantId)
            .orElseThrow(() -> new WarrantNotFoundException(warrantId));

        repository.delete(warrant);
    }

    /**
     * Returns the next sequential warrant number for today.
     */
    @Transactional(readOnly = true)
    public int nextDailyWarrantNumber() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return repository.findByWarrantDate(today).size() + 1;
    }
}
