package com.etms.service;

import com.etms.dao.MatchDAO;
import com.etms.dao.TeamDAO;
import com.etms.model.Match;
import com.etms.model.Team;
import java.sql.SQLException;
import java.util.Map;

/**
 * Predicts match outcomes based on Elo ratings, head‑to‑head history,
 * and recent form.
 */
public class MatchPredictionService {

    private final MatchDAO matchDAO = new MatchDAO();
    private final TeamDAO teamDAO = new TeamDAO();
    private final EloService eloService = new EloService();

    // Weight factors (adjustable)
    private static final double ELO_WEIGHT = 0.5;
    private static final double HEAD_TO_HEAD_WEIGHT = 0.3;
    private static final double RECENT_FORM_WEIGHT = 0.2;

    /**
     * Data holder for prediction result.
     */
    public static class Prediction {
        private int predictedWinnerId;
        private double confidence;          // 0.0 – 1.0
        private String explanation;

        public Prediction(int predictedWinnerId, double confidence, String explanation) {
            this.predictedWinnerId = predictedWinnerId;
            this.confidence = confidence;
            this.explanation = explanation;
        }

        public int getPredictedWinnerId() { return predictedWinnerId; }
        public double getConfidence() { return confidence; }
        public String getExplanation() { return explanation; }
    }

    /**
     * Predict the outcome of the given match.
     * @param match the match to predict (must have team1Id, team2Id)
     * @return Prediction object
     */
    public Prediction predict(Match match) {
        try {
            Team team1 = teamDAO.getTeamById(match.getTeam1Id());
            Team team2 = teamDAO.getTeamById(match.getTeam2Id());

            if (team1 == null || team2 == null) {
                return new Prediction(0, 0.0, "Both teams must be assigned.");
            }

            // 1. Elo probability
            double eloProbA = eloProbability(team1.getEloRating(), team2.getEloRating());

            // 2. Head‑to‑head record
            double h2hFactor = headToHeadFactor(match.getTeam1Id(), match.getTeam2Id());

            // 3. Recent win rate
            double recentA = teamDAO.getRecentWinRate(match.getTeam1Id(), 10);
            double recentB = teamDAO.getRecentWinRate(match.getTeam2Id(), 10);
            double formFactor = (recentA - recentB + 1.0) / 2.0; // normalise to 0–1

            // Weighted average
            double weightedProbA = ELO_WEIGHT * eloProbA +
                                   HEAD_TO_HEAD_WEIGHT * h2hFactor +
                                   RECENT_FORM_WEIGHT * formFactor;

            int predictedWinner = weightedProbA >= 0.5 ? match.getTeam1Id() : match.getTeam2Id();
            double confidence = Math.abs(weightedProbA - 0.5) * 2.0; // scale to 0–1

            String explanation = String.format(
                "Elo: %.0f%% (A), H2H: %.0f%% (A), Form: %.0f%% (A)",
                eloProbA * 100, h2hFactor * 100, formFactor * 100
            );

            return new Prediction(predictedWinner, Math.min(confidence, 1.0), explanation);
        } catch (SQLException e) {
            e.printStackTrace();
            return new Prediction(0, 0.0, "Error computing prediction.");
        }
    }

    /** Elo expected win probability for team A */
    private double eloProbability(double eloA, double eloB) {
        return 1.0 / (1.0 + Math.pow(10, (eloB - eloA) / 400.0));
    }

    /** Head‑to‑head factor: fraction of wins by team1 against team2 (0.5 if no data) */
    private double headToHeadFactor(int team1Id, int team2Id) throws SQLException {
        Map<String, Integer> h2h = matchDAO.getHeadToHeadRecord(team1Id, team2Id);
        int winsA = h2h.getOrDefault("team1Wins", 0);
        int winsB = h2h.getOrDefault("team2Wins", 0);
        int total = winsA + winsB;
        if (total == 0) return 0.5; // neutral
        return (double) winsA / total;
    }
}