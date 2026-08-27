package com.etms.dao;

import com.etms.config.DatabaseConfig;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class RegistrationDAO {

    private final DatabaseConfig dbConfig;

    public RegistrationDAO() {
        this.dbConfig = DatabaseConfig.getInstance();   // safe, no self-reference
    }

    /**
     * Monthly registrations: month label -> count of team registrations
     */
    public Map<String, Integer> getMonthlyRegistrations() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT TO_CHAR(registration_date, 'YYYY-MM') AS month, COUNT(*) AS cnt " +
                     "FROM tournament_teams GROUP BY month ORDER BY month";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("month"), rs.getInt("cnt"));
            }
        }
        return map;
    }

    /**
     * Tournament popularity: tournament name -> number of registered teams
     */
    public Map<String, Integer> getTournamentPopularity(int limit) throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT t.tournament_name, COUNT(tt.team_id) AS teams " +
                     "FROM tournament_teams tt JOIN tournaments t ON tt.tournament_id = t.tournament_id " +
                     "GROUP BY t.tournament_id, t.tournament_name ORDER BY teams DESC LIMIT ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("tournament_name"), rs.getInt("teams"));
                }
            }
        }
        return map;
    }
}