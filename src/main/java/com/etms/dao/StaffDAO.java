package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.Staff;
import java.sql.*;
import java.util.*;

public class StaffDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    public boolean createStaff(Staff staff) throws SQLException {
        String sql = "INSERT INTO staff (person_id, role, team_id, status) VALUES (?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, staff.getPersonId());
            ps.setString(2, staff.getStaffRole());
            if (staff.getTeamId() > 0) ps.setInt(3, staff.getTeamId());
            else ps.setNull(3, Types.INTEGER);
            ps.setString(4, staff.getStatus());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) staff.setStaffId(rs.getInt(1));
                    return true;
                }
            }
            return false;
        }
    }

    public List<Staff> getAllStaff() throws SQLException {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT s.*, p.first_name, p.last_name, p.phone, p.email FROM staff s JOIN persons p ON s.person_id = p.person_id ORDER BY p.last_name";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Staff getStaffById(int id) throws SQLException {
        String sql = "SELECT s.*, p.first_name, p.last_name, p.phone, p.email FROM staff s JOIN persons p ON s.person_id = p.person_id WHERE s.staff_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean updateStaff(Staff staff) throws SQLException {
        String sql = "UPDATE staff SET role=?, team_id=?, status=? WHERE staff_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, staff.getStaffRole());
            if (staff.getTeamId() > 0) ps.setInt(2, staff.getTeamId());
            else ps.setNull(2, Types.INTEGER);
            ps.setString(3, staff.getStatus());
            ps.setInt(4, staff.getStaffId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteStaff(int id) throws SQLException {
        String sql = "DELETE FROM staff WHERE staff_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Staff mapRow(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setStaffId(rs.getInt("staff_id"));
        s.setPersonId(rs.getInt("person_id"));
        s.setFirstName(rs.getString("first_name"));
        s.setLastName(rs.getString("last_name"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email")); // Now set from persons table
        s.setStaffRole(rs.getString("role"));
        s.setTeamId(rs.getInt("team_id"));
        s.setStatus(rs.getString("status"));
        return s;
    }
}