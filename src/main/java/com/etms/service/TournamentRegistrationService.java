package com.etms.service;

import com.etms.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

/** Atomically validates and persists an approved tournament team registration. */
public final class TournamentRegistrationService {
    private final DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    public void registerTeam(int tournamentId, int teamId) throws SQLException {
        try (Connection connection = databaseConfig.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                TournamentState tournament = loadTournamentForUpdate(connection, tournamentId);
                ensureRegistrationDeadline(tournament.deadline);
                assertActiveTeam(connection, teamId);
                if (isRegistered(connection, tournamentId, teamId)) {
                    throw new IllegalStateException("This team is already registered for the tournament.");
                }
                int registrations = countRegistrations(connection, tournamentId);
                int rosterSize = countActivePlayers(connection, teamId);
                TournamentRules.validateRegistration(tournament.status, registrations,
                        tournament.maxTeams, rosterSize, tournament.minPlayers);
                insertRegistration(connection, tournamentId, teamId, registrations + 1);
                connection.commit();
            } catch (SQLException | RuntimeException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        }
    }

    private TournamentState loadTournamentForUpdate(Connection connection, int tournamentId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT status, max_teams, min_players_per_team, registration_deadline FROM tournaments WHERE tournament_id = ? FOR UPDATE")) {
            statement.setInt(1, tournamentId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new IllegalArgumentException("Tournament was not found.");
                }
                Date deadline = result.getDate("registration_deadline");
                return new TournamentState(result.getString("status"), result.getInt("max_teams"),
                        result.getInt("min_players_per_team"), deadline == null ? null : deadline.toLocalDate());
            }
        }
    }

    private void assertActiveTeam(Connection connection, int teamId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT status FROM teams WHERE team_id = ?")) {
            statement.setInt(1, teamId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    throw new IllegalArgumentException("Team was not found.");
                }
                if (!"ACTIVE".equals(result.getString("status"))) {
                    throw new IllegalStateException("Inactive teams cannot be registered.");
                }
            }
        }
    }

    private boolean isRegistered(Connection connection, int tournamentId, int teamId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM tournament_teams WHERE tournament_id = ? AND team_id = ?")) {
            statement.setInt(1, tournamentId);
            statement.setInt(2, teamId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    private int countRegistrations(Connection connection, int tournamentId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM tournament_teams WHERE tournament_id = ? AND status IN ('PENDING', 'APPROVED')")) {
            statement.setInt(1, tournamentId);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt(1);
            }
        }
    }

    private int countActivePlayers(Connection connection, int teamId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM players WHERE team_id = ? AND status = 'ACTIVE'")) {
            statement.setInt(1, teamId);
            try (ResultSet result = statement.executeQuery()) {
                result.next();
                return result.getInt(1);
            }
        }
    }

    private void insertRegistration(Connection connection, int tournamentId, int teamId, int seed) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO tournament_teams (tournament_id, team_id, seed_number, status) VALUES (?, ?, ?, 'APPROVED')")) {
            statement.setInt(1, tournamentId);
            statement.setInt(2, teamId);
            statement.setInt(3, seed);
            statement.executeUpdate();
        }
    }

    private void ensureRegistrationDeadline(LocalDate deadline) {
        if (deadline != null && LocalDate.now().isAfter(deadline)) {
            throw new IllegalStateException("The registration deadline has passed.");
        }
    }

    private record TournamentState(String status, int maxTeams, int minPlayers, LocalDate deadline) { }
}
