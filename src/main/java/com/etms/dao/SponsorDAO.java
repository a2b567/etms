package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.Sponsor;
import java.sql.*;
import java.util.*;

public class SponsorDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    public boolean createSponsor(Sponsor sponsor) throws SQLException {
        String sql = "INSERT INTO sponsors (company_name, contact_email, sponsorship_amount, category, status) VALUES (?,?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sponsor.getCompanyName());
            ps.setString(2, sponsor.getContactEmail());
            ps.setDouble(3, sponsor.getSponsorshipAmount());
            ps.setString(4, sponsor.getCategory());
            ps.setString(5, sponsor.getStatus());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) sponsor.setSponsorId(rs.getInt(1));
                    return true;
                }
            }
            return false;
        }
    }

    public List<Sponsor> getAllSponsors() throws SQLException {
        List<Sponsor> list = new ArrayList<>();
        String sql = "SELECT * FROM sponsors ORDER BY company_name";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Sponsor getSponsorById(int id) throws SQLException {
        String sql = "SELECT * FROM sponsors WHERE sponsor_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public boolean updateSponsor(Sponsor sponsor) throws SQLException {
        String sql = "UPDATE sponsors SET company_name=?, contact_email=?, sponsorship_amount=?, category=?, status=? WHERE sponsor_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sponsor.getCompanyName());
            ps.setString(2, sponsor.getContactEmail());
            ps.setDouble(3, sponsor.getSponsorshipAmount());
            ps.setString(4, sponsor.getCategory());
            ps.setString(5, sponsor.getStatus());
            ps.setInt(6, sponsor.getSponsorId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteSponsor(int id) throws SQLException {
        String sql = "DELETE FROM sponsors WHERE sponsor_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ========== NEW METHOD (added to satisfy compilation) ==========
    public List<Sponsor> search(String query) throws SQLException {
        List<Sponsor> list = new ArrayList<>();
        String sql = "SELECT * FROM sponsors WHERE company_name ILIKE ? OR contact_email ILIKE ?";
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

    private Sponsor mapRow(ResultSet rs) throws SQLException {
        Sponsor s = new Sponsor();
        s.setSponsorId(rs.getInt("sponsor_id"));
        s.setCompanyName(rs.getString("company_name"));
        s.setContactEmail(rs.getString("contact_email"));
        s.setSponsorshipAmount(rs.getDouble("sponsorship_amount"));
        s.setCategory(rs.getString("category"));
        s.setStatus(rs.getString("status"));
        return s;
    }
}