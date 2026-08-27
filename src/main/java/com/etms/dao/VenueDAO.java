package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.Venue;
import java.sql.*;
import java.util.*;

public class VenueDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    public boolean createVenue(Venue venue) throws SQLException {
        String sql = "INSERT INTO venues (name, location, capacity, internet_speed, description, status) VALUES (?,?,?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, venue.getName());
            ps.setString(2, venue.getLocation());
            ps.setInt(3, venue.getCapacity());
            ps.setString(4, venue.getInternetSpeed());
            ps.setString(5, venue.getDescription());
            ps.setString(6, venue.getStatus());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) venue.setVenueId(rs.getInt(1));
                    return true;
                }
            }
            return false;
        }
    }

    public List<Venue> getAllVenues() throws SQLException {
        List<Venue> list = new ArrayList<>();
        String sql = "SELECT * FROM venues ORDER BY name";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Venue getVenueById(int id) throws SQLException {
        String sql = "SELECT * FROM venues WHERE venue_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean updateVenue(Venue venue) throws SQLException {
        String sql = "UPDATE venues SET name=?, location=?, capacity=?, internet_speed=?, description=?, status=? WHERE venue_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, venue.getName());
            ps.setString(2, venue.getLocation());
            ps.setInt(3, venue.getCapacity());
            ps.setString(4, venue.getInternetSpeed());
            ps.setString(5, venue.getDescription());
            ps.setString(6, venue.getStatus());
            ps.setInt(7, venue.getVenueId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteVenue(int id) throws SQLException {
        String sql = "DELETE FROM venues WHERE venue_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ========== NEW METHOD (added to satisfy compilation) ==========
    public List<Venue> search(String query) throws SQLException {
        List<Venue> list = new ArrayList<>();
        String sql = "SELECT * FROM venues WHERE name ILIKE ? OR location ILIKE ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + query + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    private Venue mapRow(ResultSet rs) throws SQLException {
        Venue v = new Venue();
        v.setVenueId(rs.getInt("venue_id"));
        v.setName(rs.getString("name"));
        v.setLocation(rs.getString("location"));
        v.setCapacity(rs.getInt("capacity"));
        v.setInternetSpeed(rs.getString("internet_speed"));
        v.setDescription(rs.getString("description"));
        v.setStatus(rs.getString("status"));
        return v;
    }
}