package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.Equipment;
import java.sql.*;
import java.util.*;

public class EquipmentDAO {

    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    // ------------------ Create ------------------
    public boolean createEquipment(Equipment eq) throws SQLException {
        String sql = "INSERT INTO equipment (type, brand, model, serial_number, status, venue_id, tournament_id, notes) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, eq.getType());
            ps.setString(2, eq.getBrand());
            ps.setString(3, eq.getModel());
            ps.setString(4, eq.getSerialNumber());
            ps.setString(5, eq.getStatus() != null ? eq.getStatus() : "Available");
            if (eq.getVenueId() > 0) ps.setInt(6, eq.getVenueId()); else ps.setNull(6, Types.INTEGER);
            if (eq.getTournamentId() > 0) ps.setInt(7, eq.getTournamentId()); else ps.setNull(7, Types.INTEGER);
            ps.setString(8, eq.getNotes());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    eq.setEquipmentId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    // ------------------ Read ------------------
    public List<Equipment> getAllEquipment() throws SQLException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT e.*, v.name AS venue_name, t.tournament_name " +
                     "FROM equipment e " +
                     "LEFT JOIN venues v ON e.venue_id = v.venue_id " +
                     "LEFT JOIN tournaments t ON e.tournament_id = t.tournament_id " +
                     "ORDER BY e.type, e.brand";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Equipment eq = new Equipment();
                eq.setEquipmentId(rs.getInt("equipment_id"));
                eq.setType(rs.getString("type"));
                eq.setBrand(rs.getString("brand"));
                eq.setModel(rs.getString("model"));
                eq.setSerialNumber(rs.getString("serial_number"));
                eq.setStatus(rs.getString("status"));
                eq.setVenueId(rs.getInt("venue_id"));
                eq.setVenueName(rs.getString("venue_name"));
                eq.setTournamentId(rs.getInt("tournament_id"));
                eq.setTournamentName(rs.getString("tournament_name"));
                eq.setNotes(rs.getString("notes"));
                list.add(eq);
            }
        }
        return list;
    }

    // ------------------ Update ------------------
    public boolean updateEquipment(Equipment eq) throws SQLException {
        String sql = "UPDATE equipment SET type=?, brand=?, model=?, serial_number=?, status=?, venue_id=?, tournament_id=?, notes=? WHERE equipment_id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, eq.getType());
            ps.setString(2, eq.getBrand());
            ps.setString(3, eq.getModel());
            ps.setString(4, eq.getSerialNumber());
            ps.setString(5, eq.getStatus());
            if (eq.getVenueId() > 0) ps.setInt(6, eq.getVenueId()); else ps.setNull(6, Types.INTEGER);
            if (eq.getTournamentId() > 0) ps.setInt(7, eq.getTournamentId()); else ps.setNull(7, Types.INTEGER);
            ps.setString(8, eq.getNotes());
            ps.setInt(9, eq.getEquipmentId());
            return ps.executeUpdate() > 0;
        }
    }

    // ------------------ Delete ------------------
    public boolean deleteEquipment(int equipmentId) throws SQLException {
        String sql = "DELETE FROM equipment WHERE equipment_id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, equipmentId);
            return ps.executeUpdate() > 0;
        }
    }

    // ------------------ Summary counts by status ------------------
    public Map<String, Integer> getStatusCounts() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT status, COUNT(*) AS cnt FROM equipment GROUP BY status";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("status"), rs.getInt("cnt"));
            }
        }
        return map;
    }

    // ------------------ Search ------------------
    public List<Equipment> search(String query) throws SQLException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT * FROM equipment WHERE brand ILIKE ? OR model ILIKE ? OR serial_number ILIKE ? ORDER BY brand";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");
            ps.setString(3, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Equipment eq = new Equipment();
                eq.setEquipmentId(rs.getInt("equipment_id"));
                eq.setType(rs.getString("type"));
                eq.setBrand(rs.getString("brand"));
                eq.setModel(rs.getString("model"));
                eq.setSerialNumber(rs.getString("serial_number"));
                eq.setStatus(rs.getString("status"));
                eq.setVenueId(rs.getInt("venue_id"));
                eq.setTournamentId(rs.getInt("tournament_id"));
                eq.setNotes(rs.getString("notes"));
                list.add(eq);
            }
        }
        return list;
    }
}