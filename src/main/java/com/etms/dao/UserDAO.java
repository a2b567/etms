package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final DatabaseConfig dbConfig;
    
    public UserDAO() {
        this.dbConfig = DatabaseConfig.getInstance();
    }
    
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }
    
    public boolean createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, email, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getRole());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUserId(rs.getInt(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
    }
    
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET email = ?, role = ?, is_active = ? WHERE user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getRole());
            pstmt.setBoolean(3, user.isActive());
            pstmt.setInt(4, user.getUserId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    public void updateLastLogin(int userId) throws SQLException {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }
    
    public int getTotalUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setActive(rs.getBoolean("is_active"));
        
        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toString());
        }
        
        return user;
    }
}