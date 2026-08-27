package com.etms.service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;

/** Pure business rules for tournament creation and registration. */
public final class TournamentRules {
    public static final Set<String> SUPPORTED_FORMATS = Set.of("SINGLE_ELIMINATION", "ROUND_ROBIN");

    private TournamentRules() {
    }

    public static void validateTournament(String name, String game, String format,
                                          String startDate, String endDate,
                                          int maxTeams, double prizePool) {
        requireText(name, "Tournament name");
        requireText(game, "Game title");
        if (!SUPPORTED_FORMATS.contains(format)) {
            throw new IllegalArgumentException("Unsupported tournament format: " + format);
        }
        if (maxTeams < 2 || maxTeams > 128) {
            throw new IllegalArgumentException("Tournament capacity must be between 2 and 128 teams.");
        }
        if (!Double.isFinite(prizePool) || prizePool < 0) {
            throw new IllegalArgumentException("Prize pool must be a non-negative amount.");
        }
        LocalDate start = parseDate(startDate, "Start date");
        if (endDate != null && !endDate.isBlank() && parseDate(endDate, "End date").isBefore(start)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
    }

    public static void validateRegistration(String tournamentStatus, int registeredTeams,
                                            int maxTeams, int rosterSize, int minPlayers) {
        if (!"UPCOMING".equals(tournamentStatus) && !"REGISTRATION".equals(tournamentStatus)) {
            throw new IllegalStateException("This tournament is not accepting registrations.");
        }
        if (registeredTeams >= maxTeams) {
            throw new IllegalStateException("This tournament has reached its team capacity.");
        }
        if (rosterSize < minPlayers) {
            throw new IllegalStateException("Team roster does not meet the minimum player requirement.");
        }
    }

    private static LocalDate parseDate(String value, String label) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException(label + " must use YYYY-MM-DD.");
        }
    }

    private static void requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }
}
