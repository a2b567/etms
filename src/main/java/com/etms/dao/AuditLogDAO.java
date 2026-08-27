package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.AuditLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class AuditLogDAO {

    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    // ---- Insert a new audit log ----
    public void insertLog(Integer userId, String username, String action, String details) throws SQLException {
        String sql = "INSERT INTO audit_logs (user_id, username, action, details) VALUES (?,?,?,?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null) ps.setInt(1, userId); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, username);
            ps.setString(3, action);
            ps.setString(4, details);
            ps.executeUpdate();
        }
    }

    // ---- Advanced search with filters ----
    public List<Map<String, Object>> getLogs(String userFilter, String actionFilter, LocalDateTime from, LocalDateTime to) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM audit_logs WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (userFilter != null && !userFilter.isEmpty()) {
            sql.append(" AND username ILIKE ?");
            params.add("%" + userFilter + "%");
        }
        if (actionFilter != null && !actionFilter.isEmpty()) {
            sql.append(" AND action = ?");
            params.add(actionFilter);
        }
        if (from != null) {
            sql.append(" AND created_at >= ?");
            params.add(Timestamp.valueOf(from));
        }
        if (to != null) {
            sql.append(" AND created_at <= ?");
            params.add(Timestamp.valueOf(to));
        }
        sql.append(" ORDER BY created_at DESC");

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", rs.getInt("log_id"));
                row.put("userId", rs.getObject("user_id"));
                row.put("username", rs.getString("username"));
                row.put("action", rs.getString("action"));
                row.put("details", rs.getString("details"));
                Timestamp ts = rs.getTimestamp("created_at");
                row.put("createdAt", ts != null ? ts.toLocalDateTime() : null);
                list.add(row);
            }
        }
        return list;
    }

    // ---- Get distinct actions for dropdown ----
    public List<String> getDistinctActions() throws SQLException {
        List<String> actions = new ArrayList<>();
        String sql = "SELECT DISTINCT action FROM audit_logs ORDER BY action";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                actions.add(rs.getString("action"));
            }
        }
        return actions;
    }

    // ---- 🆕 NEW: Get all audit logs as AuditLog objects ----
    public List<AuditLog> getAllLogs() throws SQLException {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT log_id, user_id, username, action, details, created_at FROM audit_logs ORDER BY created_at DESC";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                AuditLog log = new AuditLog();
                log.setLogId(rs.getInt("log_id"));
                int userId = rs.getInt("user_id");
                log.setUserId(rs.wasNull() ? null : userId);
                log.setUsername(rs.getString("username"));
                log.setAction(rs.getString("action"));
                log.setDetails(rs.getString("details"));
                Timestamp ts = rs.getTimestamp("created_at");
                log.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                logs.add(log);
            }
        }
        return logs;
    }
}