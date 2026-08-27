package com.etms.view;

import com.etms.model.Venue;
import com.etms.theme.ThemeManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class VenueEditDialog extends JDialog {

    private JTextField nameField, locationField, capacityField, internetSpeedField, descriptionField;
    private JComboBox<String> statusCombo;
    private boolean confirmed = false;
    private Venue originalVenue;
    private Venue updatedVenue;

    public VenueEditDialog(JFrame parent, Venue venue) {
        super(parent, venue == null ? "Add Venue" : "Edit Venue", true);
        this.originalVenue = venue;

        if (venue == null) {
            this.updatedVenue = new Venue();
            this.updatedVenue.setStatus("ACTIVE");
        } else {
            this.updatedVenue = new Venue();
            copyVenue(venue, this.updatedVenue);
        }

        initComponents();
        populateFields();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(450, 400));
    }

    private void copyVenue(Venue from, Venue to) {
        if (from == null) return;
        to.setVenueId(from.getVenueId());
        to.setName(from.getName());
        to.setLocation(from.getLocation());
        to.setCapacity(from.getCapacity());
        to.setInternetSpeed(from.getInternetSpeed());
        to.setDescription(from.getDescription());
        to.setStatus(from.getStatus() != null ? from.getStatus() : "ACTIVE");
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 2, fillx, insets 20", "[right][200:300:]", "[]10[]"));
        getContentPane().setBackground(ThemeManager.getBackground());

        add(new JLabel("Venue Name *:"));
        nameField = new JTextField(20);
        add(nameField, "growx");

        add(new JLabel("Location *:"));
        locationField = new JTextField(20);
        add(locationField, "growx");

        add(new JLabel("Capacity *:"));
        capacityField = new JTextField(20);
        add(capacityField, "growx");

        add(new JLabel("Internet Speed:"));
        internetSpeedField = new JTextField(20);
        add(internetSpeedField, "growx");

        add(new JLabel("Description:"));
        descriptionField = new JTextField(20);
        add(descriptionField, "growx");

        add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "MAINTENANCE"});
        add(statusCombo, "growx");

        JPanel buttonPanel = new JPanel(new MigLayout("align right"));
        buttonPanel.setOpaque(false);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new Color(0, 120, 215));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> saveChanges());
        buttonPanel.add(saveBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn, "gapleft 10");

        add(buttonPanel, "span 2, align right, gaptop 10");
    }

    private void populateFields() {
        if (originalVenue == null) {
            nameField.setText("");
            locationField.setText("");
            capacityField.setText("");
            internetSpeedField.setText("");
            descriptionField.setText("");
            statusCombo.setSelectedItem("ACTIVE");
            return;
        }

        nameField.setText(originalVenue.getName() != null ? originalVenue.getName() : "");
        locationField.setText(originalVenue.getLocation() != null ? originalVenue.getLocation() : "");
        capacityField.setText(String.valueOf(originalVenue.getCapacity()));
        internetSpeedField.setText(originalVenue.getInternetSpeed() != null ? originalVenue.getInternetSpeed() : "");
        descriptionField.setText(originalVenue.getDescription() != null ? originalVenue.getDescription() : "");
        statusCombo.setSelectedItem(originalVenue.getStatus() != null ? originalVenue.getStatus() : "ACTIVE");
    }

    private void saveChanges() {
        String name = nameField.getText().trim();
        String location = locationField.getText().trim();
        String capacityStr = capacityField.getText().trim();

        if (name.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Location are required.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
            if (capacity < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Capacity must be a positive integer.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        updatedVenue.setName(name);
        updatedVenue.setLocation(location);
        updatedVenue.setCapacity(capacity);
        updatedVenue.setInternetSpeed(internetSpeedField.getText().trim());
        updatedVenue.setDescription(descriptionField.getText().trim());
        updatedVenue.setStatus((String) statusCombo.getSelectedItem());

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public Venue getUpdatedVenue() { return updatedVenue; }
}