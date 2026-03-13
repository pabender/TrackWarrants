package org.trainbeans.trackwarrants.main.exception;

/**
 * Thrown when attempting to create a warrant with an existing warrant ID.
 */
public class DuplicateWarrantIdException extends WarrantException {
    public DuplicateWarrantIdException(String warrantId) {
        super("Warrant ID already exists: " + warrantId);
    }
}
