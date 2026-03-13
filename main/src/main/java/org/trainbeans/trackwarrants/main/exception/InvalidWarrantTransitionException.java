package org.trainbeans.trackwarrants.main.exception;

import org.trainbeans.trackwarrants.main.entity.TrackWarrant;

/**
 * Thrown when a status transition is not allowed.
 */
public class InvalidWarrantTransitionException extends WarrantException {
    public InvalidWarrantTransitionException(TrackWarrant.WarrantStatus from, TrackWarrant.WarrantStatus to) {
        super("Invalid status transition: " + from + " -> " + to);
    }
}

