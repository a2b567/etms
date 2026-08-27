package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.*;
import java.sql.*;
import java.util.*;

public class TournamentDAO {

    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    // ------------------ Create ------------------
    public boolean createTournament(Tournament t) throws SQLException {
        String sql = "INSERT INTO tournaments (tournament_name, game_title, tournament_type, start_date, end_date, max_teams, prize_pool, organizer_id, status, venue_id, registration_deadline, min_players_per_team) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getTournamentName());
            ps.setString(2, t.getGameTitle());
            ps.setString(3, t.getTournamentType());

            String startStr = t.getStartDate();
            if (startStr != null && !startStr.isEmpty()) {
                ps.setDate(4, java.sql.Date.valueOf(startStr));
            } else {
                ps.setNull(4, Types.DATE);
            }

            String endStr = t.getEndDate();
            if (endStr != null && !endStr.isEmpty()) {
                ps.setDate(5, java.sql.Date.valueOf(endStr));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setInt(6, t.getMaxTeams());
            ps.setDouble(7, t.getPrizePool());
            ps.setInt(8, t.getOrganizerId());
            // Use Types.OTHER for PostgreSQL ENUM
            ps.setObject(9, t.getStatus(), Types.OTHER);
            if (t.getVenueId() > 0) {
                ps.setInt(10, t.getVenueId());
            } else {
                ps.setNull(10, Types.INTEGER);
            }
            if (t.getRegistrationDeadline() != null && !t.getRegistrationDeadline().isBlank()) {
                ps.setDate(11, java.sql.Date.valueOf(t.getRegistrationDeadline()));
            } else {
                ps.setNull(11, Types.DATE);
            }
            ps.setInt(12, t.getMinPlayersPerTeam());

            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    t.setTournamentId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    // ------------------ Read ------------------
    public List<Tournament> getAllTournaments() throws SQLException {
        List<Tournament> list = new ArrayList<>();
        String sql = "SELECT * FROM tournaments ORDER BY created_at DESC";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Tournament t = createTournamentByType(rs.getString("tournament_type"));
                mapTournament(t, rs);
                list.add(t);
            }
        }
        return list;
    }

    public Tournament getTournamentById(int id) throws SQLException {
        String sql = "SELECT * FROM tournaments WHERE tournament_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Tournament t = createTournamentByType(rs.getString("tournament_type"));
                mapTournament(t, rs);
                return t;
            }
        }
        return null;
    }

    // ------------------ Update ------------------
    public boolean updateTournament(Tournament t) throws SQLException {
        String sql = "UPDATE tournaments SET tournament_name=?, game_title=?, tournament_type=?, start_date=?, end_date=?, max_teams=?, prize_pool=?, status=?, venue_id=?, registration_deadline=?, min_players_per_team=? WHERE tournament_id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTournamentName());
            ps.setString(2, t.getGameTitle());
            ps.setString(3, t.getTournamentType());

            String startStr = t.getStartDate();
            if (startStr != null && !startStr.isEmpty()) {
                ps.setDate(4, java.sql.Date.valueOf(startStr));
            } else {
                ps.setNull(4, Types.DATE);
            }

            String endStr = t.getEndDate();
            if (endStr != null && !endStr.isEmpty()) {
                ps.setDate(5, java.sql.Date.valueOf(endStr));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setInt(6, t.getMaxTeams());
            ps.setDouble(7, t.getPrizePool());

            // 🔥 FIX: Use Types.OTHER for PostgreSQL ENUM
            ps.setObject(8, t.getStatus(), Types.OTHER);

            if (t.getVenueId() > 0) {
                ps.setInt(9, t.getVenueId());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            if (t.getRegistrationDeadline() != null && !t.getRegistrationDeadline().isBlank()) {
                ps.setDate(10, java.sql.Date.valueOf(t.getRegistrationDeadline()));
            } else {
                ps.setNull(10, Types.DATE);
            }
            ps.setInt(11, t.getMinPlayersPerTeam());
            ps.setInt(12, t.getTournamentId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException(
                    "Update failed: 0 rows affected. Tournament ID " + t.getTournamentId() +
                    " may not exist or RLS policies are blocking the update."
                );
            }
            return true;
        }
    }

    // ------------------ Delete ------------------
    public boolean deleteTournament(int id) throws SQLException {
        String sql = "DELETE FROM tournaments WHERE tournament_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ------------------ Stats / helpers ------------------
    public int getActiveTournaments() throws SQLException {
        String sql = "SELECT COUNT(*) FROM tournaments WHERE status IN ('UPCOMING','REGISTRATION','ONGOING')";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    /**
     * Dedicated method to update only the status.
     * Uses Types.OTHER for PostgreSQL ENUM.
     */
    public boolean updateTournamentStatus(int id, String status) throws SQLException {
        String sql = "UPDATE tournaments SET status=? WHERE tournament_id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, status, Types.OTHER);
            ps.setInt(2, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException(
                    "Status update failed: 0 rows affected. Tournament ID " + id +
                    " may not exist or RLS policies are blocking the update."
                );
            }
            return true;
        }
    }

    public Map<String, Integer> getMonthlyTournamentCount() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT TO_CHAR(created_at, 'YYYY-MM') AS month, COUNT(*) AS cnt " +
                     "FROM tournaments GROUP BY month ORDER BY month";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("month"), rs.getInt("cnt"));
            }
        }
        return map;
    }

    public List<Tournament> getRecentTournaments(int limit) throws SQLException {
        List<Tournament> list = new ArrayList<>();
        String sql = "SELECT * FROM tournaments ORDER BY created_at DESC LIMIT ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Tournament t = createTournamentByType(rs.getString("tournament_type"));
                mapTournament(t, rs);
                list.add(t);
            }
        }
        return list;
    }

    public void updateVenueId(int tournamentId, int venueId) throws SQLException {
        String sql = "UPDATE tournaments SET venue_id = ? WHERE tournament_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (venueId > 0) ps.setInt(1, venueId);
            else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, tournamentId);
            ps.executeUpdate();
        }
    }

    public List<Tournament> search(String query) throws SQLException {
        List<Tournament> list = new ArrayList<>();
        String sql = "SELECT * FROM tournaments WHERE tournament_name ILIKE ? ORDER BY created_at DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Tournament t = createTournamentByType(rs.getString("tournament_type"));
                mapTournament(t, rs);
                list.add(t);
            }
        }
        return list;
    }

    // ------------------ Register a team for a tournament ------------------
    public boolean registerTeamForTournament(int tournamentId, int teamId, int seed) throws SQLException {
        String sql = "INSERT INTO tournament_teams (tournament_id, team_id, seed_number, status) VALUES (?,?,?,'APPROVED') " +
                     "ON CONFLICT (tournament_id, team_id) DO NOTHING";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ps.setInt(2, teamId);
            ps.setInt(3, seed);
            return ps.executeUpdate() == 1;
        }
    }

    // Helper: get teams registered for a tournament
    public List<Team> getTeamsByTournament(int tournamentId) throws SQLException {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT t.*, p.first_name, p.last_name FROM teams t " +
                    "JOIN tournament_teams tt ON t.team_id = tt.team_id " +
                    "LEFT JOIN persons p ON t.coach_id = p.person_id " +
                    "WHERE tt.tournament_id = ? AND tt.status = 'APPROVED' " +
                    "ORDER BY tt.seed_number";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Team team = new Team();
                team.setTeamId(rs.getInt("team_id"));
                team.setTeamName(rs.getString("team_name"));
                team.setTag(rs.getString("tag"));
                team.setCoachId(rs.getInt("coach_id"));
                String fn = rs.getString("first_name");
                String ln = rs.getString("last_name");
                if (fn != null && ln != null) team.setCoachName(fn + " " + ln);
                team.setDateCreated(rs.getString("date_created"));
                team.setTotalWins(rs.getInt("total_wins"));
                team.setTotalLosses(rs.getInt("total_losses"));
                team.setTournamentPoints(rs.getInt("tournament_points"));
                team.setRanking(rs.getInt("ranking"));
                team.setStatus(rs.getString("status"));
                team.setEloRating(rs.getDouble("elo_rating"));
                teams.add(team);
            }
        }
        return teams;
    }

    // ------------------ Private Helpers (Polymorphism Factory) ------------------
    public Tournament createTournamentByType(String type) {
        if (type == null) return new SingleEliminationTournament();
        switch (type.toUpperCase()) {
            case "DOUBLE_ELIMINATION":
                return new DoubleEliminationTournament();
            case "ROUND_ROBIN":
                return new RoundRobinTournament();
            case "SWISS":
                return new SwissTournament();
            case "SINGLE_ELIMINATION":
            default:
                return new SingleEliminationTournament();
        }
    }

    private void mapTournament(Tournament t, ResultSet rs) throws SQLException {
        t.setTournamentId(rs.getInt("tournament_id"));
        t.setTournamentName(rs.getString("tournament_name"));
        t.setGameTitle(rs.getString("game_title"));
        t.setTournamentType(rs.getString("tournament_type"));
        java.sql.Date startDate = rs.getDate("start_date");
        t.setStartDate(startDate != null ? startDate.toString() : null);
        java.sql.Date endDate = rs.getDate("end_date");
        t.setEndDate(endDate != null ? endDate.toString() : null);
        t.setMaxTeams(rs.getInt("max_teams"));
        t.setPrizePool(rs.getDouble("prize_pool"));
        t.setOrganizerId(rs.getInt("organizer_id"));
        t.setStatus(rs.getString("status"));
        t.setVenueId(rs.getInt("venue_id"));
        java.sql.Date registrationDeadline = rs.getDate("registration_deadline");
        t.setRegistrationDeadline(registrationDeadline != null ? registrationDeadline.toString() : null);
        t.setMinPlayersPerTeam(rs.getInt("min_players_per_team"));
    }
}