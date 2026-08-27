package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.Coach;
import java.sql.*;
import java.util.*;

public class CoachDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    public boolean createCoach(Coach coach) throws SQLException {
        String sql = "INSERT INTO coaches (person_id, experience_years, specialization, certification, team_id, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, coach.getPersonId());
            ps.setInt(2, coach.getExperienceYears());
            ps.setString(3, coach.getSpecialization());
            ps.setString(4, coach.getCertification());
            if (coach.getTeamId() > 0) ps.setInt(5, coach.getTeamId());
            else ps.setNull(5, Types.INTEGER);
            ps.setString(6, coach.getStatus() != null ? coach.getStatus() : "ACTIVE");
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) coach.setCoachId(rs.getInt(1));
                    return true;
                }
            }
            return false;
        }
    }

    public List<Coach> getAllCoaches() throws SQLException {
        List<Coach> list = new ArrayList<>();
        String sql = "SELECT c.*, p.first_name, p.last_name FROM coaches c JOIN persons p ON c.person_id = p.person_id ORDER BY p.last_name";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Coach getCoachById(int id) throws SQLException {
        String sql = "SELECT c.*, p.first_name, p.last_name FROM coaches c JOIN persons p ON c.person_id = p.person_id WHERE c.coach_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean updateCoach(Coach coach) throws SQLException {
        String sql = "UPDATE coaches SET experience_years=?, specialization=?, certification=?, team_id=?, status=? WHERE coach_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, coach.getExperienceYears());
            ps.setString(2, coach.getSpecialization());
            ps.setString(3, coach.getCertification());
            if (coach.getTeamId() > 0) ps.setInt(4, coach.getTeamId());
            else ps.setNull(4, Types.INTEGER);
            ps.setString(5, coach.getStatus());
            ps.setInt(6, coach.getCoachId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCoach(int id) throws SQLException {
        String sql = "DELETE FROM coaches WHERE coach_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Coach mapRow(ResultSet rs) throws SQLException {
        Coach c = new Coach();
        c.setCoachId(rs.getInt("coach_id"));
        c.setPersonId(rs.getInt("person_id"));
        c.setFirstName(rs.getString("first_name"));
        c.setLastName(rs.getString("last_name"));
        c.setExperienceYears(rs.getInt("experience_years"));
        c.setSpecialization(rs.getString("specialization"));
        c.setCertification(rs.getString("certification"));
        c.setTeamId(rs.getInt("team_id"));
        c.setStatus(rs.getString("status"));
        return c;
    }
}