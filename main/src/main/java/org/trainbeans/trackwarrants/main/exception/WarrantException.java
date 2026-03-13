package org.trainbeans.trackwarrants.main.exception;

/**
 * Base exception for all warrant-related domain exceptions.
 * Provides common behavior and allows polymorphic exception handling.
 */
public abstract class WarrantException extends RuntimeException {

    protected WarrantException(String message) {
        super(message);
    }

    protected WarrantException(String message, Throwable cause) {
        super(message, cause);
    }
}

