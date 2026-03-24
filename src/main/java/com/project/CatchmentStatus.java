package com.project;

/**
 * Represents the suitability of the current hour for spontaneous fermentation
 * catchment at Copper & Moss Meadery.
 *
 * <ul>
 *     <li>{@link #OPTIMAL} - both temperature and humidity are within target ranges.</li>
 *     <li>{@link #SUB_OPTIMAL} - only one condition is met (temperature OR humidity, not both).</li>
 *     <li>{@link #CLOSED} - neither condition is met; catchment is not recommended</li>
 * </ul>
 */
public enum CatchmentStatus {

    /** Both temperature and humidity thresholds satisfied. */
    OPTIMAL,

    /** Exactly one of temperature or humidity thresholds satisfied. */
    SUB_OPTIMAL,

    /** Neither threshold satisfied. */
    CLOSED;

    public String label() {
        return switch (this) {
            case OPTIMAL -> "Optimal";
            case SUB_OPTIMAL -> "Sub-optimal";
            case CLOSED -> "Closed";
        };
    }

}
