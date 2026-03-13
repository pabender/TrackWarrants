package org.trainbeans.trackwarrants.main.exception;

/**
 * Thrown when a warrant cannot be found by identifier.
 */
public class WarrantNotFoundException extends WarrantException {
    public WarrantNotFoundException(String warrantId) {
        super("Warrant not found: " + warrantId);
    }
}

