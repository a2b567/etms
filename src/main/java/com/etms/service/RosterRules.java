package com.etms.service;

/** Stateless roster constraints shared by UI-independent workflow services. */
public final class RosterRules {
    public static final int MAX_ROSTER_SIZE = 10;
    public static final int MAX_STARTERS = 5;

    private RosterRules() {
    }

    public static void validateAssignment(boolean active, int currentRosterSize,
                                          int currentStarterCount, boolean alreadyOnTeam,
                                          boolean wasStarter, boolean starter) {
        if (!active) {
            throw new IllegalStateException("Inactive players cannot be assigned to a roster.");
        }
        if (!alreadyOnTeam && currentRosterSize >= MAX_ROSTER_SIZE) {
            throw new IllegalStateException("The roster is already at its 10-player limit.");
        }
        if (starter && !wasStarter && currentStarterCount >= MAX_STARTERS) {
            throw new IllegalStateException("The roster already has five starters.");
        }
    }

    public static void validateCaptain(boolean playerBelongsToTeam, boolean active) {
        if (!playerBelongsToTeam) {
            throw new IllegalArgumentException("Captain must belong to the selected team.");
        }
        if (!active) {
            throw new IllegalStateException("Inactive players cannot be named captain.");
        }
    }
}
