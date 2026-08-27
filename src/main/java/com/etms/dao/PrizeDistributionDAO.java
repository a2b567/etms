package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.PrizeDistribution;
import java.sql.*;
import java.util.*;

public class PrizeDistributionDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    // ---------- CREATE ----------
    public boolean createDistribution(PrizeDistribution pd) throws SQLException {
        String sql = "INSERT INTO prize_distribution (tournament_id, position, percentage, amount, awarded) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pd.getTournamentId());
            ps.setInt(2, pd.getPosition());
            ps.setDouble(3, pd.getPercentage());
            ps.setDouble(4, pd.getAmount());
            ps.setBoolean(5, pd.isAwarded());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        pd.setDistributionId(rs.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }

    // ---------- READ ----------
    public List<PrizeDistribution> getDistributionsByTournament(int tournamentId) throws SQLException {
        List<PrizeDistribution> list = new ArrayList<>();
        String sql = "SELECT * FROM prize_distribution WHERE tournament_id = ? ORDER BY position";
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

    public PrizeDistribution getDistributionById(int id) throws SQLException {
        String sql = "SELECT * FROM prize_distribution WHERE distribution_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // ---------- UPDATE ----------
    public boolean updateDistribution(PrizeDistribution pd) throws SQLException {
        String sql = "UPDATE prize_distribution SET position=?, percentage=?, amount=?, awarded=? WHERE distribution_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, pd.getPosition());
            ps.setDouble(2, pd.getPercentage());
            ps.setDouble(3, pd.getAmount());
            ps.setBoolean(4, pd.isAwarded());
            ps.setInt(5, pd.getDistributionId());
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- DELETE ----------
    public boolean deleteDistribution(int id) throws SQLException {
        String sql = "DELETE FROM prize_distribution WHERE distribution_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteDistributionsByTournament(int tournamentId) throws SQLException {
        String sql = "DELETE FROM prize_distribution WHERE tournament_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            return ps.executeUpdate() > 0;
        }
    }

    // ---------- MAP ROW ----------
    private PrizeDistribution mapRow(ResultSet rs) throws SQLException {
        PrizeDistribution pd = new PrizeDistribution();
        pd.setDistributionId(rs.getInt("distribution_id"));
        pd.setTournamentId(rs.getInt("tournament_id"));
        pd.setPosition(rs.getInt("position"));
        pd.setPercentage(rs.getDouble("percentage"));
        pd.setAmount(rs.getDouble("amount"));
        pd.setAwarded(rs.getBoolean("awarded"));
        return pd;
    }
}