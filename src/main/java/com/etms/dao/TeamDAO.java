package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.Team;
import java.sql.*;
import java.util.*;

/**
 * Data Access Object for Team entities.
 * Uses PostgreSQL with Supabase.
 * All methods throw SQLException to let the controller handle errors.
 */
public class TeamDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    // ---------- CREATE ----------
    public boolean createTeam(Team team) throws SQLException {
        String sql = "INSERT INTO teams (team_name, tag, coach_id, status, elo_rating) VALUES (?, ?, ?, ?, ?)";
        System.out.println("[TeamDAO] createTeam SQL: " + sql);
        System.out.println("[TeamDAO] team_name: " + team.getTeamName());
        System.out.println("[TeamDAO] tag: " + team.getTag());
        System.out.println("[TeamDAO] coach_id: " + team.getCoachId());
        System.out.println("[TeamDAO] status: " + team.getStatus());

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, team.getTeamName());
            ps.setString(2, team.getTag());
            if (team.getCoachId() > 0) {
                ps.setInt(3, team.getCoachId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, team.getStatus() != null ? team.getStatus() : "ACTIVE");
            ps.setDouble(5, team.getEloRating() > 0 ? team.getEloRating() : 1200.0);

            int rows = ps.executeUpdate();
            System.out.println("[TeamDAO] rows affected: " + rows);

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        team.setTeamId(rs.getInt(1));
                        System.out.println("[TeamDAO] Generated team_id: " + team.getTeamId());
                        return true;
                    }
                }
            }
            return false;
        } catch (SQLException e) {
            System.err.println("[TeamDAO] SQL Error in createTeam: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // ---------- READ ----------
    public List<Team> getAllTeams() throws SQLException {
        List<Team> list = new ArrayList<>();
        String sql = "SELECT t.*, " +
                     "c.coach_id, " +
                     "p.first_name || ' ' || p.last_name AS coach_name " +
                     "FROM teams t " +
                     "LEFT JOIN coaches c ON t.coach_id = c.coach_id " +
                     "LEFT JOIN persons p ON c.person_id = p.person_id " +
                     "ORDER BY t.team_name";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    public Team getTeamById(int id) throws SQLException {
        String sql = "SELECT t.*, " +
                     "c.coach_id, " +
                     "p.first_name || ' ' || p.last_name AS coach_name " +
                     "FROM teams t " +
                     "LEFT JOIN coaches c ON t.coach_id = c.coach_id " +
                     "LEFT JOIN persons p ON c.person_id = p.person_id " +
                     "WHERE t.team_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<Team> getTeamsByTournament(int tournamentId) throws SQLException {
        List<Team> list = new ArrayList<>();
        String sql = "SELECT t.*, " +
                     "c.coach_id, " +
                     "p.first_name || ' ' || p.last_name AS coach_name " +
                     "FROM teams t " +
                     "LEFT JOIN coaches c ON t.coach_id = c.coach_id " +
                     "LEFT JOIN persons p ON c.person_id = p.person_id " +
                     "JOIN tournament_teams tt ON t.team_id = tt.team_id " +
                     "WHERE tt.tournament_id = ? AND tt.status = 'APPROVED' " +
                     "ORDER BY tt.seed_number";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    // ---------- UPDATE ----------
    public boolean updateTeam(Team team) throws SQLException {
        String sql = "UPDATE teams SET team_name=?, tag=?, coach_id=?, status=?, elo_rating=? WHERE team_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, team.getTeamName());
            ps.setString(2, team.getTag());
            if (team.getCoachId() > 0) {
                ps.setInt(3, team.getCoachId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, team.getStatus());
            ps.setDouble(5, team.getEloRating());
            ps.setInt(6, team.getTeamId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateCaptain(int teamId, int playerId) throws SQLException {
        String sql = "UPDATE teams SET captain_id = ? WHERE team_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setInt(2, teamId);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- DELETE ----------
    public boolean deleteTeam(int id) throws SQLException {
        String sql = "DELETE FROM teams WHERE team_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- STATS ----------
    public int getTotalTeams() throws SQLException {
        String sql = "SELECT COUNT(*) FROM teams WHERE status = 'ACTIVE'";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public Map<String, Integer> getMonthlyTeamCount() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT TO_CHAR(date_created, 'YYYY-MM') AS month, COUNT(*) AS cnt " +
                     "FROM teams GROUP BY month ORDER BY month";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("month"), rs.getInt("cnt"));
            }
        }
        return map;
    }

    // ---------- ELO RATING ----------
    public double getEloRating(int teamId) throws SQLException {
        String sql = "SELECT elo_rating FROM teams WHERE team_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble("elo_rating");
            }
        }
        return 1200.0;
    }

    public boolean updateEloRating(int teamId, double rating) throws SQLException {
        String sql = "UPDATE teams SET elo_rating = ? WHERE team_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, rating);
            ps.setInt(2, teamId);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- RECENT WIN RATE ----------
    public double getRecentWinRate(int teamId, int matches) throws SQLException {
        String sql = "SELECT COUNT(*) FILTER (WHERE winner_team_id = ?) AS wins, COUNT(*) AS total " +
                     "FROM (SELECT winner_team_id FROM matches WHERE team1_id = ? OR team2_id = ? " +
                     "ORDER BY scheduled_time DESC LIMIT ?) AS recent";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ps.setInt(2, teamId);
            ps.setInt(3, teamId);
            ps.setInt(4, matches);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    if (total == 0) return 0.5;
                    return (double) rs.getInt("wins") / total;
                }
            }
        }
        return 0.5;
    }

    // ---------- SEARCH ----------
    public List<Team> search(String query) throws SQLException {
        List<Team> list = new ArrayList<>();
        String sql = "SELECT t.*, " +
                     "c.coach_id, " +
                     "p.first_name || ' ' || p.last_name AS coach_name " +
                     "FROM teams t " +
                     "LEFT JOIN coaches c ON t.coach_id = c.coach_id " +
                     "LEFT JOIN persons p ON c.person_id = p.person_id " +
                     "WHERE t.team_name ILIKE ? OR t.tag ILIKE ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + query + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    // ========== MAP ROW – FIXED WITH COACH NAME ==========
    private Team mapRow(ResultSet rs) throws SQLException {
        Team team = new Team();

        team.setTeamId(rs.getInt("team_id"));
        team.setTeamName(rs.getString("team_name"));
        team.setTag(rs.getString("tag"));
        team.setCoachId(rs.getInt("coach_id"));
        
        // Set coach name from JOIN
        String coachName = rs.getString("coach_name");
        team.setCoachName(coachName != null ? coachName : null);

        // date_created – safe read
        String created = null;
        try {
            created = rs.getString("date_created");
        } catch (SQLException ignored) {}
        team.setDateCreated(created != null ? created : "");

        // Stats
        team.setTotalWins(rs.getInt("total_wins"));
        team.setTotalLosses(rs.getInt("total_losses"));
        team.setTournamentPoints(rs.getInt("tournament_points"));
        team.setRanking(rs.getInt("ranking"));

        // Status – normalize
        String status = rs.getString("status");
        if (status != null) {
            status = status.trim().toUpperCase();
            if (!status.equals("ACTIVE") && !status.equals("INACTIVE") && !status.equals("BANNED")) {
                status = "ACTIVE";
            }
        } else {
            status = "ACTIVE";
        }
        team.setStatus(status);

        team.setEloRating(rs.getDouble("elo_rating"));

        return team;
    }
}