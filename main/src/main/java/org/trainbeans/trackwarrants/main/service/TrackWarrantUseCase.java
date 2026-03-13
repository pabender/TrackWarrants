package org.trainbeans.trackwarrants.main.service;

/**
 * Combined use case interface for track warrant management.
 * Extends both query and command interfaces for backward compatibility.
 *
 * Clients can depend on either:
 * - TrackWarrantQueryUseCase (read-only operations)
 * - TrackWarrantCommandUseCase (write operations)
 * - TrackWarrantUseCase (full access - backward compatible)
 */
public interface TrackWarrantUseCase extends TrackWarrantQueryUseCase, TrackWarrantCommandUseCase {
    // Inherits all methods from both query and command interfaces
}

