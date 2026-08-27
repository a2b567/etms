package com.etms.service;

import com.etms.model.Match;

/** Validation for match scheduling and result state transitions. */
public final class MatchRules {
    private MatchRules() {
    }

    public static void validateResult(Match match, int team1Score, int team2Score, int winnerId) {
        if (match == null) {
            throw new IllegalArgumentException("Match was not found.");
        }
        if ("COMPLETED".equals(match.getStatus())) {
            throw new IllegalStateException("A completed match result cannot be recorded again.");
        }
        if (match.getTeam1Id() <= 0 || match.getTeam2Id() <= 0) {
            throw new IllegalStateException("Both teams must be assigned before recording a result.");
        }
        if (team1Score < 0 || team2Score < 0 || team1Score == team2Score) {
            throw new IllegalArgumentException("Scores must be non-negative and cannot end in a tie.");
        }
        int expectedWinner = team1Score > team2Score ? match.getTeam1Id() : match.getTeam2Id();
        if (winnerId != expectedWinner) {
            throw new IllegalArgumentException("Winner must be the team with the higher score.");
        }
    }
}
