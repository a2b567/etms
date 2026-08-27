package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Equipment;
import com.etms.model.Tournament;
import com.etms.model.Venue;
import com.etms.theme.ThemeManager;
import com.etms.ui.components.ETMSButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class EquipmentDialog extends JDialog {

    private final DashboardController controller;
    private final Equipment originalEquipment;
    private Equipment updatedEquipment;
    private boolean confirmed = false;

    // UI Components
    private JComboBox<String> typeCombo;
    private JTextField brandField, modelField, serialField, notesField;
    private JComboBox<String> statusCombo;
    private JComboBox<String> venueCombo;
    private JComboBox<String> tournamentCombo;
    private List<Venue> venues;
    private List<Tournament> tournaments;

    public EquipmentDialog(JFrame parent, DashboardController controller, Equipment equipment) {
        super(parent, equipment == null ? "Add Equipment" : "Edit Equipment", true);
        this.controller = controller;
        this.originalEquipment = equipment;

        if (equipment == null) {
            this.updatedEquipment = new Equipment();
            this.updatedEquipment.setStatus("Available");
        } else {
            this.updatedEquipment = new Equipment();
            copyEquipment(equipment, this.updatedEquipment);
        }

        loadData();
        initComponents();
        populateFields();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(500, 500));
    }

    private void copyEquipment(Equipment from, Equipment to) {
        if (from == null) return;
        to.setEquipmentId(from.getEquipmentId());
        to.setType(from.getType());
        to.setBrand(from.getBrand());
        to.setModel(from.getModel());
        to.setSerialNumber(from.getSerialNumber());
        to.setStatus(from.getStatus());
        to.setVenueId(from.getVenueId());
        to.setTournamentId(from.getTournamentId());
        to.setNotes(from.getNotes());
    }

    private void loadData() {
        try {
            venues = controller.getAllVenues();
            tournaments = controller.getAllTournaments();
        } catch (Exception e) {
            e.printStackTrace();
            venues = List.of();
            tournaments = List.of();
        }
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 2, fillx, insets 20", "[right][200:300:]", "[]10[]"));
        getContentPane().setBackground(ThemeManager.getBackground());

        // Type
        add(new JLabel("Type *:"));
        typeCombo = new JComboBox<>(new String[]{"Monitor", "Keyboard", "Mouse", "Headset", "Console", "PC", "Projector", "Other"});
        add(typeCombo, "growx");

        // Brand
        add(new JLabel("Brand:"));
        brandField = new JTextField(20);
        add(brandField, "growx");

        // Model
        add(new JLabel("Model:"));
        modelField = new JTextField(20);
        add(modelField, "growx");

        // Serial Number
        add(new JLabel("Serial Number:"));
        serialField = new JTextField(20);
        add(serialField, "growx");

        // Status
        add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"Available", "In Use", "Maintenance", "Retired"});
        add(statusCombo, "growx");

        // Venue
        add(new JLabel("Assign to Venue:"));
        venueCombo = new JComboBox<>();
        venueCombo.addItem("None");
        for (Venue v : venues) {
            venueCombo.addItem(v.getName() + " (ID:" + v.getVenueId() + ")");
        }
        add(venueCombo, "growx");

        // Tournament
        add(new JLabel("Assign to Tournament:"));
        tournamentCombo = new JComboBox<>();
        tournamentCombo.addItem("None");
        for (Tournament t : tournaments) {
            tournamentCombo.addItem(t.getTournamentName() + " (ID:" + t.getTournamentId() + ")");
        }
        add(tournamentCombo, "growx");

        // Notes
        add(new JLabel("Notes:"));
        notesField = new JTextField(20);
        add(notesField, "growx");

        // Buttons
        JPanel buttonPanel = new JPanel(new MigLayout("align right"));
        buttonPanel.setOpaque(false);

        ETMSButton saveBtn = new ETMSButton("Save", ETMSButton.Variant.PRIMARY);
        saveBtn.addActionListener(e -> saveChanges());
        buttonPanel.add(saveBtn);

        ETMSButton cancelBtn = new ETMSButton("Cancel", ETMSButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn, "gapleft 10");

        add(buttonPanel, "span 2, align right, gaptop 10");
    }

    private void populateFields() {
        if (originalEquipment == null) {
            typeCombo.setSelectedIndex(0);
            brandField.setText("");
            modelField.setText("");
            serialField.setText("");
            statusCombo.setSelectedItem("Available");
            venueCombo.setSelectedIndex(0);
            tournamentCombo.setSelectedIndex(0);
            notesField.setText("");
            return;
        }

        // Type
        typeCombo.setSelectedItem(originalEquipment.getType());

        brandField.setText(originalEquipment.getBrand() != null ? originalEquipment.getBrand() : "");
        modelField.setText(originalEquipment.getModel() != null ? originalEquipment.getModel() : "");
        serialField.setText(originalEquipment.getSerialNumber() != null ? originalEquipment.getSerialNumber() : "");
        statusCombo.setSelectedItem(originalEquipment.getStatus() != null ? originalEquipment.getStatus() : "Available");
        notesField.setText(originalEquipment.getNotes() != null ? originalEquipment.getNotes() : "");

        // Venue
        if (originalEquipment.getVenueId() > 0) {
            for (int i = 0; i < venues.size(); i++) {
                if (venues.get(i).getVenueId() == originalEquipment.getVenueId()) {
                    venueCombo.setSelectedIndex(i + 1);
                    break;
                }
            }
        } else {
            venueCombo.setSelectedIndex(0);
        }

        // Tournament
        if (originalEquipment.getTournamentId() > 0) {
            for (int i = 0; i < tournaments.size(); i++) {
                if (tournaments.get(i).getTournamentId() == originalEquipment.getTournamentId()) {
                    tournamentCombo.setSelectedIndex(i + 1);
                    break;
                }
            }
        } else {
            tournamentCombo.setSelectedIndex(0);
        }
    }

    private void saveChanges() {
        String type = (String) typeCombo.getSelectedItem();
        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();
        String serial = serialField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();
        String notes = notesField.getText().trim();

        // Validation
        if (type == null || type.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Type is required.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get venue ID
        int venueIdx = venueCombo.getSelectedIndex();
        int venueId = 0;
        if (venueIdx > 0) {
            String venueEntry = (String) venueCombo.getSelectedItem();
            venueId = Integer.parseInt(venueEntry.substring(venueEntry.lastIndexOf(":") + 1, venueEntry.lastIndexOf(")")));
        }

        // Get tournament ID
        int tournIdx = tournamentCombo.getSelectedIndex();
        int tournamentId = 0;
        if (tournIdx > 0) {
            String tournEntry = (String) tournamentCombo.getSelectedItem();
            tournamentId = Integer.parseInt(tournEntry.substring(tournEntry.lastIndexOf(":") + 1, tournEntry.lastIndexOf(")")));
        }

        // Update equipment object
        updatedEquipment.setType(type);
        updatedEquipment.setBrand(brand.isEmpty() ? null : brand);
        updatedEquipment.setModel(model.isEmpty() ? null : model);
        updatedEquipment.setSerialNumber(serial.isEmpty() ? null : serial);
        updatedEquipment.setStatus(status);
        updatedEquipment.setVenueId(venueId);
        updatedEquipment.setTournamentId(tournamentId);
        updatedEquipment.setNotes(notes.isEmpty() ? null : notes);

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public Equipment getUpdatedEquipment() { return updatedEquipment; }
}