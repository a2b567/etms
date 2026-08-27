package com.etms.service;

import com.etms.config.DatabaseConfig;
import com.etms.model.Match;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Records a result, standings counters, and Elo ratings as one transaction. */
public final class MatchResultService {
    private static final double K_FACTOR = 32.0;
    private final DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    public void recordResult(int matchId, int team1Score, int team2Score, int winnerId) throws SQLException {
        try (Connection connection = databaseConfig.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                Match match = loadMatchForUpdate(connection, matchId);
                MatchRules.validateResult(match, team1Score, team2Score, winnerId);
                updateMatch(connection, matchId, team1Score, team2Score, winnerId);
                updateTeamRecord(connection, winnerId, true);
                updateTeamRecord(connection, winnerId == match.getTeam1Id() ? match.getTeam2Id() : match.getTeam1Id(), false);
                updateElo(connection, match.getTeam1Id(), match.getTeam2Id(), winnerId);
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        }
    }

    private Match loadMatchForUpdate(Connection connection, int matchId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT match_id, tournament_id, round_number, match_number, team1_id, team2_id, winner_team_id, team1_score, team2_score, status FROM matches WHERE match_id = ? FOR UPDATE")) {
            statement.setInt(1, matchId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new IllegalArgumentException("Match was not found.");
                }
                Match match = new Match();
                match.setMatchId(result.getInt("match_id"));
                match.setTournamentId(result.getInt("tournament_id"));
                match.setRoundNumber(result.getInt("round_number"));
                match.setMatchNumber(result.getInt("match_number"));
                match.setTeam1Id(result.getInt("team1_id"));
                match.setTeam2Id(result.getInt("team2_id"));
                match.setWinnerTeamId(result.getInt("winner_team_id"));
                match.setTeam1Score(result.getInt("team1_score"));
                match.setTeam2Score(result.getInt("team2_score"));
                match.setStatus(result.getString("status"));
                return match;
            }
        }
    }

    private void updateMatch(Connection connection, int matchId, int team1Score,
                             int team2Score, int winnerId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE matches SET team1_score = ?, team2_score = ?, winner_team_id = ?, status = 'COMPLETED' WHERE match_id = ?")) {
            statement.setInt(1, team1Score);
            statement.setInt(2, team2Score);
            statement.setInt(3, winnerId);
            statement.setInt(4, matchId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Match result was not saved.");
            }
        }
    }

    private void updateTeamRecord(Connection connection, int teamId, boolean won) throws SQLException {
        String column = won ? "total_wins" : "total_losses";
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE teams SET " + column + " = " + column + " + 1 WHERE team_id = ?")) {
            statement.setInt(1, teamId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Team record could not be updated.");
            }
        }
    }

    private void updateElo(Connection connection, int team1Id, int team2Id, int winnerId) throws SQLException {
        double rating1 = loadEloForUpdate(connection, team1Id);
        double rating2 = loadEloForUpdate(connection, team2Id);
        double expected1 = 1.0 / (1.0 + Math.pow(10, (rating2 - rating1) / 400.0));
        double expected2 = 1.0 - expected1;
        double score1 = winnerId == team1Id ? 1.0 : 0.0;
        updateElo(connection, team1Id, rating1 + K_FACTOR * (score1 - expected1));
        updateElo(connection, team2Id, rating2 + K_FACTOR * ((1.0 - score1) - expected2));
    }

    private double loadEloForUpdate(Connection connection, int teamId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT elo_rating FROM teams WHERE team_id = ? FOR UPDATE")) {
            statement.setInt(1, teamId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new SQLException("Match references a missing team.");
                }
                return result.getDouble(1);
            }
        }
    }

    private void updateElo(Connection connection, int teamId, double rating) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE teams SET elo_rating = ?, elo_rating_updated = CURRENT_TIMESTAMP WHERE team_id = ?")) {
            statement.setDouble(1, rating);
            statement.setInt(2, teamId);
            statement.executeUpdate();
        }
    }
}
