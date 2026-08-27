package com.etms.service;

import com.etms.dao.MatchDAO;
import com.etms.dao.TeamDAO;
import com.etms.model.Match;
import java.sql.SQLException;

/**
 * Handles Elo rating calculations and updates for teams after a match.
 * Demonstrates Encapsulation: all rating logic is isolated here.
 */
public class EloService {

    private static final double K_FACTOR = 32.0;
    private final TeamDAO teamDAO = new TeamDAO();
    private final MatchDAO matchDAO = new MatchDAO();

    /**
     * Updates the Elo ratings for the two teams involved in the given match.
     * Only call after a winner has been determined.
     */
    public void updateRatings(int matchId) {
        try {
            Match match = matchDAO.getMatchById(matchId);
            if (match == null || match.getWinnerTeamId() == 0) return; // no winner, no update

            int teamA = match.getTeam1Id();
            int teamB = match.getTeam2Id();
            int winner = match.getWinnerTeamId();
            if (teamA == 0 || teamB == 0) return;

            double ratingA = teamDAO.getEloRating(teamA);
            double ratingB = teamDAO.getEloRating(teamB);

            double expectedA = 1.0 / (1.0 + Math.pow(10, (ratingB - ratingA) / 400.0));
            double expectedB = 1.0 - expectedA;

            double actualA = (winner == teamA) ? 1.0 : 0.0;
            double actualB = (winner == teamB) ? 1.0 : 0.0;

            double newRatingA = ratingA + K_FACTOR * (actualA - expectedA);
            double newRatingB = ratingB + K_FACTOR * (actualB - expectedB);

            teamDAO.updateEloRating(teamA, newRatingA);
            teamDAO.updateEloRating(teamB, newRatingB);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}