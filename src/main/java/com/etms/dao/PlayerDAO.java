package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.Player;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    public boolean createPlayer(Player player) throws SQLException {
        String sql = "INSERT INTO players (person_id, team_id, in_game_name, game_rank, game_role, is_captain, is_starter, jersey_number, status) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, player.getPersonId());
            if (player.getTeamId() > 0) ps.setInt(2, player.getTeamId());
            else ps.setNull(2, Types.INTEGER);
            ps.setString(3, player.getInGameName());
            ps.setString(4, player.getGameRank());
            ps.setString(5, player.getGameRole());
            ps.setBoolean(6, player.isCaptain());
            ps.setBoolean(7, player.isStarter());
            ps.setInt(8, player.getJerseyNumber());
            ps.setString(9, player.getStatus() != null ? player.getStatus() : "ACTIVE");
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) player.setPlayerId(rs.getInt(1));
                    return true;
                }
            }
            return false;
        }
    }

    public List<Player> getAllPlayers() throws SQLException {
        List<Player> list = new ArrayList<>();
        String sql = "SELECT p.*, pe.first_name, pe.last_name, t.team_name FROM players p " +
                     "JOIN persons pe ON p.person_id = pe.person_id " +
                     "LEFT JOIN teams t ON p.team_id = t.team_id " +
                     "ORDER BY pe.last_name, pe.first_name";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Player getPlayerById(int id) throws SQLException {
        String sql = "SELECT p.*, pe.first_name, pe.last_name, t.team_name FROM players p " +
                     "JOIN persons pe ON p.person_id = pe.person_id " +
                     "LEFT JOIN teams t ON p.team_id = t.team_id " +
                     "WHERE p.player_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Player> getPlayersByTeam(int teamId) throws SQLException {
        List<Player> list = new ArrayList<>();
        String sql = "SELECT p.*, pe.first_name, pe.last_name, t.team_name FROM players p " +
                     "JOIN persons pe ON p.person_id = pe.person_id " +
                     "LEFT JOIN teams t ON p.team_id = t.team_id " +
                     "WHERE p.team_id = ? ORDER BY pe.last_name";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean updatePlayer(Player player) throws SQLException {
        String sql = "UPDATE players SET team_id=?, in_game_name=?, game_rank=?, game_role=?, is_captain=?, is_starter=?, jersey_number=?, status=? WHERE player_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (player.getTeamId() > 0) ps.setInt(1, player.getTeamId());
            else ps.setNull(1, Types.INTEGER);
            ps.setString(2, player.getInGameName());
            ps.setString(3, player.getGameRank());
            ps.setString(4, player.getGameRole());
            ps.setBoolean(5, player.isCaptain());
            ps.setBoolean(6, player.isStarter());
            ps.setInt(7, player.getJerseyNumber());
            ps.setString(8, player.getStatus());
            ps.setInt(9, player.getPlayerId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deletePlayer(int playerId) throws SQLException {
        String sql = "DELETE FROM players WHERE player_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            return ps.executeUpdate() > 0;
        }
    }

    public int getTotalPlayers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM players";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public boolean updatePlayerAssignment(int playerId, int teamId, boolean isStarter, boolean isCaptain) throws SQLException {
        String sql = "UPDATE players SET team_id=?, is_starter=?, is_captain=? WHERE player_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (teamId > 0) ps.setInt(1, teamId);
            else ps.setNull(1, Types.INTEGER);
            ps.setBoolean(2, isStarter);
            ps.setBoolean(3, isCaptain);
            ps.setInt(4, playerId);
            return ps.executeUpdate() > 0;
        }
    }

    // ========== NEW METHOD (added to satisfy compilation) ==========
    public List<Player> search(String query) throws SQLException {
        List<Player> list = new ArrayList<>();
        String sql = "SELECT p.*, pe.first_name, pe.last_name, t.team_name FROM players p " +
                     "JOIN persons pe ON p.person_id = pe.person_id " +
                     "LEFT JOIN teams t ON p.team_id = t.team_id " +
                     "WHERE pe.first_name ILIKE ? OR pe.last_name ILIKE ? OR p.in_game_name ILIKE ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + query + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Player mapRow(ResultSet rs) throws SQLException {
        Player p = new Player();
        p.setPlayerId(rs.getInt("player_id"));
        p.setPersonId(rs.getInt("person_id"));
        p.setFirstName(rs.getString("first_name"));
        p.setLastName(rs.getString("last_name"));
        p.setTeamId(rs.getInt("team_id"));
        p.setTeamName(rs.getString("team_name"));
        p.setInGameName(rs.getString("in_game_name"));
        p.setGameRank(rs.getString("game_rank"));
        p.setGameRole(rs.getString("game_role"));
        p.setCaptain(rs.getBoolean("is_captain"));
        p.setStarter(rs.getBoolean("is_starter"));
        p.setJerseyNumber(rs.getInt("jersey_number"));
        p.setStatus(rs.getString("status"));
        p.setTotalMatches(rs.getInt("total_matches"));
        p.setWins(rs.getInt("wins"));
        p.setLosses(rs.getInt("losses"));
        p.setMvpCount(rs.getInt("mvp_count"));
        return p;
    }
}