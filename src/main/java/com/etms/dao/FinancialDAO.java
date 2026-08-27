package com.etms.dao;

import com.etms.config.DatabaseConfig;
import java.sql.*;
import java.util.*;

public class FinancialDAO {

    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    // ------------------ Create ------------------
    public void addTransaction(String type, double amount, String description, Integer tournamentId) throws SQLException {
        String sql = "INSERT INTO financial_transactions (type, amount, description, tournament_id, transaction_date) VALUES (?,?,?,?,CURRENT_DATE)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setDouble(2, amount);
            ps.setString(3, description);
            if (tournamentId != null) ps.setInt(4, tournamentId);
            else ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
        }
    }

    // ------------------ Update ------------------
    public void updateTransaction(int id, String type, double amount, String description, Integer tournamentId) throws SQLException {
        String sql = "UPDATE financial_transactions SET type=?, amount=?, description=?, tournament_id=? WHERE transaction_id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setDouble(2, amount);
            ps.setString(3, description);
            if (tournamentId != null) ps.setInt(4, tournamentId);
            else ps.setNull(4, Types.INTEGER);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }

    // ------------------ Delete ------------------
    public void deleteTransaction(int id) throws SQLException {
        String sql = "DELETE FROM financial_transactions WHERE transaction_id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ------------------ Read ------------------
    public List<Map<String, Object>> getAllTransactions() throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT * FROM financial_transactions ORDER BY transaction_date DESC, created_at DESC";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", rs.getInt("transaction_id"));
                map.put("type", rs.getString("type"));
                map.put("amount", rs.getDouble("amount"));
                map.put("description", rs.getString("description"));
                map.put("tournamentId", rs.getObject("tournament_id"));
                map.put("date", rs.getDate("transaction_date").toLocalDate());
                list.add(map);
            }
        }
        return list;
    }

    // ------------------ Summary ------------------
    public double getTotalRevenue() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM financial_transactions WHERE type IN ('REGISTRATION_FEE','SPONSORSHIP')";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }

    public double getTotalExpenses() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM financial_transactions WHERE type IN ('PRIZE_PAYOUT','EXPENSE')";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        }
        return 0.0;
    }

    // ------------------ Analytics (monthly) ------------------
    public Map<String, Double[]> getMonthlyFinancials() throws SQLException {
        Map<String, Double[]> map = new LinkedHashMap<>();
        String sql = "SELECT TO_CHAR(transaction_date, 'YYYY-MM') AS month, " +
                     "SUM(CASE WHEN type IN ('REGISTRATION_FEE','SPONSORSHIP') THEN amount ELSE 0 END) AS revenue, " +
                     "SUM(CASE WHEN type IN ('PRIZE_PAYOUT','EXPENSE') THEN amount ELSE 0 END) AS expense " +
                     "FROM financial_transactions GROUP BY month ORDER BY month";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String month = rs.getString("month");
                double rev = rs.getDouble("revenue");
                double exp = rs.getDouble("expense");
                map.put(month, new Double[]{rev, exp});
            }
        }
        return map;
    }
}