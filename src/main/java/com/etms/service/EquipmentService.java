package com.etms.service;

import com.etms.dao.EquipmentDAO;
import com.etms.model.Equipment;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Business logic layer for Equipment management.
 * Handles validation and delegates CRUD operations to EquipmentDAO.
 */
public class EquipmentService {
    
    private final EquipmentDAO dao = new EquipmentDAO();
    
    /**
     * Creates new equipment with validation.
     */
    public boolean createEquipment(Equipment eq) throws SQLException {
        // Validation
        if (eq.getType() == null || eq.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment type is required.");
        }
        if (eq.getBrand() == null || eq.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("Brand is required.");
        }
        if (eq.getModel() == null || eq.getModel().trim().isEmpty()) {
            throw new IllegalArgumentException("Model is required.");
        }
        if (eq.getSerialNumber() == null || eq.getSerialNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Serial number is required.");
        }
        
        // Trim all fields
        eq.setType(eq.getType().trim());
        eq.setBrand(eq.getBrand().trim());
        eq.setModel(eq.getModel().trim());
        eq.setSerialNumber(eq.getSerialNumber().trim());
        
        // Set default status if null
        if (eq.getStatus() == null || eq.getStatus().trim().isEmpty()) {
            eq.setStatus("Available");
        }
        
        return dao.createEquipment(eq);
    }
    
    /**
     * Gets all equipment with venue and tournament names.
     */
    public List<Equipment> getAllEquipment() throws SQLException {
        return dao.getAllEquipment();
    }
    
    /**
     * Updates existing equipment with validation.
     */
    public boolean updateEquipment(Equipment eq) throws SQLException {
        if (eq.getEquipmentId() <= 0) {
            throw new IllegalArgumentException("Valid equipment ID is required.");
        }
        if (eq.getType() == null || eq.getType().trim().isEmpty()) {
            throw new IllegalArgumentException("Equipment type is required.");
        }
        if (eq.getBrand() == null || eq.getBrand().trim().isEmpty()) {
            throw new IllegalArgumentException("Brand is required.");
        }
        if (eq.getModel() == null || eq.getModel().trim().isEmpty()) {
            throw new IllegalArgumentException("Model is required.");
        }
        if (eq.getSerialNumber() == null || eq.getSerialNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Serial number is required.");
        }
        
        // Trim all fields
        eq.setType(eq.getType().trim());
        eq.setBrand(eq.getBrand().trim());
        eq.setModel(eq.getModel().trim());
        eq.setSerialNumber(eq.getSerialNumber().trim());
        
        return dao.updateEquipment(eq);
    }
    
    /**
     * Deletes equipment by ID.
     */
    public boolean deleteEquipment(int equipmentId) throws SQLException {
        if (equipmentId <= 0) {
            throw new IllegalArgumentException("Valid equipment ID is required.");
        }
        return dao.deleteEquipment(equipmentId);
    }
    
    /**
     * Gets equipment status counts (for summary cards).
     */
    public Map<String, Integer> getStatusCounts() throws SQLException {
        return dao.getStatusCounts();
    }
    
    /**
     * Searches equipment by brand, model, or serial number.
     */
    public List<Equipment> searchEquipment(String query) throws SQLException {
        if (query == null || query.trim().isEmpty()) {
            return getAllEquipment();
        }
        return dao.search(query.trim());
    }
    
    /**
     * Gets equipment by status.
     */
    public List<Equipment> getEquipmentByStatus(String status) throws SQLException {
        List<Equipment> all = dao.getAllEquipment();
        List<Equipment> filtered = new java.util.ArrayList<>();
        for (Equipment e : all) {
            if (e.getStatus() != null && e.getStatus().equalsIgnoreCase(status)) {
                filtered.add(e);
            }
        }
        return filtered;
    }
}