package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.Referee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RefereeDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    // CREATE
    public boolean createReferee(Referee r) throws SQLException {
        String sql = "INSERT INTO referees (first_name, last_name, email, phone, qualification, years_experience, status) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getFirstName());
            ps.setString(2, r.getLastName());
            ps.setString(3, r.getEmail());
            ps.setString(4, r.getPhone());
            ps.setString(5, r.getQualification());
            ps.setInt(6, r.getYearsExperience());
            ps.setString(7, r.getStatus());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) r.setRefereeId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    // READ all
    public List<Referee> getAllReferees() throws SQLException {
        List<Referee> list = new ArrayList<>();
        String sql = "SELECT * FROM referees ORDER BY last_name, first_name";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // READ by ID
    public Referee getRefereeById(int id) throws SQLException {
        String sql = "SELECT * FROM referees WHERE referee_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    // UPDATE
    public boolean updateReferee(Referee r) throws SQLException {
        String sql = "UPDATE referees SET first_name=?, last_name=?, email=?, phone=?, qualification=?, years_experience=?, status=? WHERE referee_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getFirstName());
            ps.setString(2, r.getLastName());
            ps.setString(3, r.getEmail());
            ps.setString(4, r.getPhone());
            ps.setString(5, r.getQualification());
            ps.setInt(6, r.getYearsExperience());
            ps.setString(7, r.getStatus());
            ps.setInt(8, r.getRefereeId());
            return ps.executeUpdate() > 0;
        }
    }

    // DELETE
    public boolean deleteReferee(int id) throws SQLException {
        String sql = "DELETE FROM referees WHERE referee_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // Get active referees (for dropdown)
    public List<Referee> getActiveReferees() throws SQLException {
        List<Referee> list = new ArrayList<>();
        String sql = "SELECT * FROM referees WHERE status = 'ACTIVE' ORDER BY last_name";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Referee mapRow(ResultSet rs) throws SQLException {
        Referee r = new Referee();
        r.setRefereeId(rs.getInt("referee_id"));
        r.setFirstName(rs.getString("first_name"));
        r.setLastName(rs.getString("last_name"));
        r.setEmail(rs.getString("email"));
        r.setPhone(rs.getString("phone"));
        r.setQualification(rs.getString("qualification"));
        r.setYearsExperience(rs.getInt("years_experience"));
        r.setStatus(rs.getString("status"));
        return r;
    }
}