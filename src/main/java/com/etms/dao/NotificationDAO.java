package com.etms.dao;

import com.etms.config.DatabaseConfig;
import java.sql.*;
import java.util.*;

public class NotificationDAO {

    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    public int getUnreadCount(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<Map<String, Object>> getNotificationsForUser(int userId) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT notification_id, message, is_read, created_at FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("id", rs.getInt("notification_id"));
                map.put("message", rs.getString("message"));
                map.put("isRead", rs.getBoolean("is_read"));
                map.put("createdAt", rs.getTimestamp("created_at").toLocalDateTime());
                list.add(map);
            }
        }
        return list;
    }

    public void markAsRead(int notificationId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.executeUpdate();
        }
    }

    public void markAllAsRead(int userId) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public void createNotification(int userId, String message) throws SQLException {
        String sql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, message);
            ps.executeUpdate();
        }
    }
}