package com.etms.service;

import com.etms.dao.RefereeDAO;
import com.etms.model.Referee;
import java.sql.SQLException;
import java.util.List;

public class RefereeService {
    private final RefereeDAO dao = new RefereeDAO();

    public boolean createReferee(Referee r) throws SQLException {
        // Validation
        if (r.getFirstName() == null || r.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (r.getLastName() == null || r.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        String email = r.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (!email.trim().contains("@") || !email.trim().contains(".")) {
            throw new IllegalArgumentException("Email must be a valid email address (e.g., name@domain.com).");
        }
        r.setEmail(email.trim());
        return dao.createReferee(r);
    }

    public List<Referee> getAllReferees() throws SQLException {
        return dao.getAllReferees();
    }

    public Referee getRefereeById(int id) throws SQLException {
        return dao.getRefereeById(id);
    }

    public boolean updateReferee(Referee r) throws SQLException {
        if (r.getFirstName() == null || r.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required.");
        }
        if (r.getLastName() == null || r.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }
        String email = r.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (!email.trim().contains("@") || !email.trim().contains(".")) {
            throw new IllegalArgumentException("Email must be a valid email address (e.g., name@domain.com).");
        }
        r.setEmail(email.trim());
        return dao.updateReferee(r);
    }

    public boolean deleteReferee(int id) throws SQLException {
        return dao.deleteReferee(id);
    }

    public List<Referee> getActiveReferees() throws SQLException {
        return dao.getActiveReferees();
    }
}