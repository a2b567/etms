package com.etms.service;

import com.etms.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/** Transactional roster mutation service. */
public final class RosterService {
    private final DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    public void assignPlayer(int playerId, int teamId, boolean starter, boolean captain) throws SQLException {
        try (Connection connection = databaseConfig.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                PlayerState player = loadPlayerForUpdate(connection, playerId);
                if (teamId <= 0) {
                    clearAssignment(connection, playerId);
                } else {
                    assertActiveTeam(connection, teamId);
                    RosterCount count = getRosterCount(connection, teamId);
                    boolean belongsToTeam = player.teamId == teamId;
                    RosterRules.validateAssignment("ACTIVE".equals(player.status), count.players,
                            count.starters, belongsToTeam, player.starter, starter);
                    if (captain) {
                        clearCaptain(connection, teamId);
                    }
                    updateAssignment(connection, playerId, teamId, starter, captain);
                }
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        }
    }

    public void setCaptain(int teamId, int playerId) throws SQLException {
        try (Connection connection = databaseConfig.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                PlayerState player = loadPlayerForUpdate(connection, playerId);
                RosterRules.validateCaptain(player.teamId == teamId, "ACTIVE".equals(player.status));
                clearCaptain(connection, teamId);
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE players SET is_captain = TRUE WHERE player_id = ? AND team_id = ?")) {
                    statement.setInt(1, playerId);
                    statement.setInt(2, teamId);
                    if (statement.executeUpdate() != 1) {
                        throw new SQLException("Player could not be named captain.");
                    }
                }
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        }
    }

    private PlayerState loadPlayerForUpdate(Connection connection, int playerId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT player_id, team_id, status, is_starter FROM players WHERE player_id = ? FOR UPDATE")) {
            statement.setInt(1, playerId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new IllegalArgumentException("Player was not found.");
                }
                return new PlayerState(result.getInt("team_id"), result.getString("status"),
                        result.getBoolean("is_starter"));
            }
        }
    }

    private void assertActiveTeam(Connection connection, int teamId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT status FROM teams WHERE team_id = ? FOR UPDATE")) {
            statement.setInt(1, teamId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new IllegalArgumentException("Team was not found.");
                }
                if (!"ACTIVE".equals(result.getString("status"))) {
                    throw new IllegalStateException("Inactive teams cannot accept players.");
                }
            }
        }
    }

    private RosterCount getRosterCount(Connection connection, int teamId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) AS players, COUNT(*) FILTER (WHERE is_starter) AS starters FROM players WHERE team_id = ?")) {
            statement.setInt(1, teamId);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return new RosterCount(result.getInt("players"), result.getInt("starters"));
            }
        }
    }

    private void updateAssignment(Connection connection, int playerId, int teamId,
                                  boolean starter, boolean captain) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE players SET team_id = ?, is_starter = ?, is_captain = ? WHERE player_id = ?")) {
            statement.setInt(1, teamId);
            statement.setBoolean(2, starter);
            statement.setBoolean(3, captain);
            statement.setInt(4, playerId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Player assignment was not saved.");
            }
        }
    }

    private void clearAssignment(Connection connection, int playerId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE players SET team_id = ?, is_starter = FALSE, is_captain = FALSE WHERE player_id = ?")) {
            statement.setNull(1, Types.INTEGER);
            statement.setInt(2, playerId);
            if (statement.executeUpdate() != 1) {
                throw new SQLException("Player assignment was not cleared.");
            }
        }
    }

    private void clearCaptain(Connection connection, int teamId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE players SET is_captain = FALSE WHERE team_id = ?")) {
            statement.setInt(1, teamId);
            statement.executeUpdate();
        }
    }

    private record PlayerState(int teamId, String status, boolean starter) { }
    private record RosterCount(int players, int starters) { }
}
