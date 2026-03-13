package org.trainbeans.trackwarrants.main.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.trainbeans.trackwarrants.main.dto.CreateWarrantRequest;
import org.trainbeans.trackwarrants.main.exception.DuplicateWarrantIdException;
import org.trainbeans.trackwarrants.main.repository.TrackWarrantRepository;

/**
 * Validator component for track warrant business rules.
 * Centralizes validation logic following Single Responsibility Principle.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TrackWarrantValidator {

    private final TrackWarrantRepository repository;

    /**
     * Validate that a warrant can be created.
     * Checks for duplicate warrant IDs and other business rules.
     *
     * @param request the creation request
     * @throws DuplicateWarrantIdException if warrant ID already exists
     */
    public void validateCreate(CreateWarrantRequest request) {
        log.debug("Validating warrant creation request: {}", request.getWarrantId());

        // Check for duplicate warrant ID
        if (repository.findByWarrantId(request.getWarrantId()).isPresent()) {
            throw new DuplicateWarrantIdException(request.getWarrantId());
        }

        // Additional validation rules can be added here without modifying service
        // For example:
        // - Validate date ranges
        // - Validate speed limits
        // - Check track availability
        // - Validate location names
    }
}

