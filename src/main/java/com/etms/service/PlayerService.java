package com.etms.service;

import com.etms.config.DatabaseConfig;
import com.etms.model.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/** Creates player profiles atomically so a failed player insert cannot orphan a person row. */
public final class PlayerService {
    private final DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    public Player createPlayer(String firstName, String lastName, String inGameName,
                               String rank, String role) throws SQLException {
        return createPlayer(firstName, lastName, inGameName, rank, role, 0);
    }

    public Player createPlayer(String firstName, String lastName, String inGameName,
                               String rank, String role, int teamId) throws SQLException {
        validateProfile(firstName, lastName, inGameName);
        try (Connection connection = databaseConfig.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                int personId = insertPerson(connection, firstName.trim(), lastName.trim());
                Player player = new Player(firstName.trim(), lastName.trim(), inGameName.trim());
                player.setPersonId(personId);
                player.setGameRank(blankToNull(rank));
                player.setGameRole(blankToNull(role));
                if (teamId > 0) {
                    validateInitialAssignment(connection, teamId);
                    player.setTeamId(teamId);
                }
                insertPlayer(connection, player);
                connection.commit();
                return player;
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        }
    }

    public static void validateProfile(String firstName, String lastName, String inGameName) {
        if (firstName == null || firstName.isBlank() || firstName.trim().length() > 50) {
            throw new IllegalArgumentException("First name is required and must be at most 50 characters.");
        }
        if (lastName == null || lastName.isBlank() || lastName.trim().length() > 50) {
            throw new IllegalArgumentException("Last name is required and must be at most 50 characters.");
        }
        if (inGameName == null || !inGameName.trim().matches("[A-Za-z0-9_.-]{3,50}")) {
            throw new IllegalArgumentException("In-game name must be 3-50 letters, numbers, dots, underscores, or hyphens.");
        }
    }

    private int insertPerson(Connection connection, String firstName, String lastName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO persons (first_name, last_name) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Person profile was not created.");
    }

    private void insertPlayer(Connection connection, Player player) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO players (person_id, in_game_name, game_rank, game_role, team_id, status, is_captain, is_starter, jersey_number) VALUES (?, ?, ?, ?, ?, 'ACTIVE', FALSE, FALSE, 0)",
                Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, player.getPersonId());
            statement.setString(2, player.getInGameName());
            statement.setString(3, player.getGameRank());
            statement.setString(4, player.getGameRole());
            if (player.getTeamId() > 0) {
                statement.setInt(5, player.getTeamId());
            } else {
                statement.setNull(5, Types.INTEGER);
            }
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    player.setPlayerId(keys.getInt(1));
                    return;
                }
            }
        }
        throw new SQLException("Player profile was not created.");
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void validateInitialAssignment(Connection connection, int teamId) throws SQLException {
        try (PreparedStatement teamStatement = connection.prepareStatement(
                "SELECT status FROM teams WHERE team_id = ? FOR UPDATE")) {
            teamStatement.setInt(1, teamId);
            try (ResultSet team = teamStatement.executeQuery()) {
                if (!team.next()) {
                    throw new IllegalArgumentException("Team was not found.");
                }
                if (!"ACTIVE".equals(team.getString("status"))) {
                    throw new IllegalStateException("Inactive teams cannot accept players.");
                }
            }
        }
        try (PreparedStatement countStatement = connection.prepareStatement(
                "SELECT COUNT(*) FROM players WHERE team_id = ?")) {
            countStatement.setInt(1, teamId);
            try (ResultSet count = countStatement.executeQuery()) {
                count.next();
                RosterRules.validateAssignment(true, count.getInt(1), 0, false, false, false);
            }
        }
    }
}
