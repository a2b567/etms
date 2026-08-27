package com.etms.dao;

import com.etms.config.DatabaseConfig;
import java.sql.*;
import java.util.*;

public class PlayerPerformanceDAO {

    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public void savePerformance(int playerId, int matchId, int kills, int deaths, int assists, boolean isMvp) throws SQLException {
        String sql = "INSERT INTO player_match_performance (player_id, match_id, kills, deaths, assists, is_mvp) VALUES (?,?,?,?,?,?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.setInt(2, matchId);
            ps.setInt(3, kills);
            ps.setInt(4, deaths);
            ps.setInt(5, assists);
            ps.setBoolean(6, isMvp);
            ps.executeUpdate();
        }
    }

    public List<Map<String, Object>> getPerformanceByPlayer(int playerId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT pmp.*, m.tournament_id, m.round_number, m.match_number " +
                    "FROM player_match_performance pmp " +
                    "JOIN matches m ON pmp.match_id = m.match_id " +
                    "WHERE pmp.player_id = ? ORDER BY m.scheduled_time";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("matchId", rs.getInt("match_id"));
                row.put("kills", rs.getInt("kills"));
                row.put("deaths", rs.getInt("deaths"));
                row.put("assists", rs.getInt("assists"));
                row.put("isMvp", rs.getBoolean("is_mvp"));
                row.put("tournamentId", rs.getInt("tournament_id"));
                row.put("round", rs.getInt("round_number"));
                row.put("matchNumber", rs.getInt("match_number"));
                list.add(row);
            }
        }
        return list;
    }

    public Map<String, Integer> getAggregateStats(int playerId) throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("kills", 0);
        stats.put("deaths", 0);
        stats.put("assists", 0);
        stats.put("mvps", 0);
        String sql = "SELECT COALESCE(SUM(kills), 0) AS totalKills, " +
                     "COALESCE(SUM(deaths), 0) AS totalDeaths, " +
                     "COALESCE(SUM(assists), 0) AS totalAssists, " +
                     "COUNT(CASE WHEN is_mvp = TRUE THEN 1 END) AS totalMvps " +
                     "FROM player_match_performance WHERE player_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stats.put("kills", rs.getInt("totalKills"));
                stats.put("deaths", rs.getInt("totalDeaths"));
                stats.put("assists", rs.getInt("totalAssists"));
                stats.put("mvps", rs.getInt("totalMvps"));
            }
        }
        return stats;
    }
}