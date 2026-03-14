package org.trainbeans.trackwarrants.main.service;

import org.springframework.stereotype.Component;
import org.trainbeans.trackwarrants.main.entity.TrackWarrant;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Defines allowed warrant status transitions.
 */
@Component
public class WarrantStatusTransitionPolicy {

    private final Map<TrackWarrant.WarrantStatus, Set<TrackWarrant.WarrantStatus>> transitions;

    public WarrantStatusTransitionPolicy() {
        transitions = new EnumMap<>(TrackWarrant.WarrantStatus.class);
        transitions.put(TrackWarrant.WarrantStatus.ACTIVE,
            EnumSet.of(
                TrackWarrant.WarrantStatus.VOID,
                TrackWarrant.WarrantStatus.EXPIRED
            ));
        transitions.put(TrackWarrant.WarrantStatus.VOID, EnumSet.noneOf(TrackWarrant.WarrantStatus.class));
        transitions.put(TrackWarrant.WarrantStatus.EXPIRED, EnumSet.noneOf(TrackWarrant.WarrantStatus.class));
    }

    public boolean isAllowed(TrackWarrant.WarrantStatus from, TrackWarrant.WarrantStatus to) {
        if (from == null || to == null) {
            return false;
        }
        if (from == to) {
            return true;
        }
        return transitions.getOrDefault(from, EnumSet.noneOf(TrackWarrant.WarrantStatus.class)).contains(to);
    }
}

