package com.etms.view;

import com.etms.components.AppButton;
import com.etms.controller.DashboardController;
import com.etms.dao.VenueDAO;
import com.etms.model.Tournament;
import com.etms.model.Venue;
import com.etms.theme.ColorPalette;
import com.etms.theme.Typography;
import com.etms.util.ValidationUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TournamentConfigDialog extends JDialog {

    private final DashboardController controller;
    private final Tournament tournament;

    private JTextField nameField;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> statusCombo;
    private JSpinner maxTeamsSpinner;
    private JSpinner prizePoolSpinner;
    private JComboBox<String> venueCombo;
    private JTextField registrationDeadlineField;
    private JSpinner minPlayersSpinner;

    public TournamentConfigDialog(JFrame parent, DashboardController controller, Tournament tournament) {
        super(parent, "Configure Tournament", true);
        this.controller = controller;
        this.tournament = tournament;
        setSize(500, 650);
        setLocationRelativeTo(parent);
        setBackground(ColorPalette.LIGHT_BG);
        initComponents();
        populateFields();
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow][]"));

        JLabel headerLabel = new JLabel("Tournament Configuration");
        headerLabel.setFont(Typography.PAGE_TITLE);
        headerLabel.setForeground(ColorPalette.LIGHT_TEXT);
        add(headerLabel, "wrap, align center, gapbottom 15");

        JPanel formPanel = new JPanel(new MigLayout("wrap 2, fillx", "[right][grow]", "[]10[]"));
        formPanel.setBorder(BorderFactory.createTitledBorder("Edit Tournament Details"));
        formPanel.setBackground(ColorPalette.LIGHT_BG);

        formPanel.add(new JLabel("Tournament Name:"));
        nameField = new JTextField();
        formPanel.add(nameField, "growx");

        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        startDateField = new JTextField();
        formPanel.add(startDateField, "growx");

        formPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        endDateField = new JTextField();
        formPanel.add(endDateField, "growx");

        formPanel.add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"UPCOMING", "REGISTRATION", "ONGOING", "COMPLETED", "CANCELLED"});
        formPanel.add(statusCombo, "growx");

        formPanel.add(new JLabel("Max Teams:"));
        maxTeamsSpinner = new JSpinner(new SpinnerNumberModel(16, 2, 128, 2));
        formPanel.add(maxTeamsSpinner, "growx");

        formPanel.add(new JLabel("Prize Pool ($):"));
        // Use Double model
        prizePoolSpinner = new JSpinner(new SpinnerNumberModel(1000.0, 0.0, 1000000.0, 100.0));
        formPanel.add(prizePoolSpinner, "growx");

        formPanel.add(new JLabel("Venue:"));
        venueCombo = new JComboBox<>();
        venueCombo.addItem("None");
        try {
            VenueDAO venueDAO = new VenueDAO();
            List<Venue> venues = venueDAO.getAllVenues();
            for (Venue v : venues) {
                venueCombo.addItem(v.getName() + " (ID:" + v.getVenueId() + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        formPanel.add(venueCombo, "growx");

        formPanel.add(new JLabel("Registration Deadline (YYYY-MM-DD):"));
        registrationDeadlineField = new JTextField();
        formPanel.add(registrationDeadlineField, "growx");

        formPanel.add(new JLabel("Min Players per Team:"));
        minPlayersSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        formPanel.add(minPlayersSpinner, "growx");

        add(formPanel, "grow, wrap, gapbottom 10");

        JPanel buttonPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow]", "[]"));
        JButton saveBtn = new AppButton("Save Changes", AppButton.ButtonType.PRIMARY);
        saveBtn.addActionListener(e -> saveChanges());
        buttonPanel.add(saveBtn, "growx");

        JButton cancelBtn = new AppButton("Cancel", AppButton.ButtonType.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn, "growx");

        add(buttonPanel, "growx");
    }

    private void populateFields() {
        nameField.setText(tournament.getTournamentName());
        startDateField.setText(tournament.getStartDate());
        endDateField.setText(tournament.getEndDate() != null ? tournament.getEndDate() : "");
        statusCombo.setSelectedItem(tournament.getStatus());
        maxTeamsSpinner.setValue(tournament.getMaxTeams());

        // FIX: Do not cast to int – keep as double
        prizePoolSpinner.setValue(tournament.getPrizePool());

        int venueId = tournament.getVenueId();
        for (int i = 0; i < venueCombo.getItemCount(); i++) {
            String item = venueCombo.getItemAt(i);
            if (item.endsWith("ID:" + venueId + ")")) {
                venueCombo.setSelectedIndex(i);
                break;
            }
        }

        registrationDeadlineField.setText(tournament.getRegistrationDeadline() != null ? tournament.getRegistrationDeadline() : "");
        minPlayersSpinner.setValue(tournament.getMinPlayersPerTeam());
    }

    private void saveChanges() {
        System.out.println(">>> Save button clicked, tournament ID: " + tournament.getTournamentId());

        String name = nameField.getText().trim();
        String start = startDateField.getText().trim();
        String end = endDateField.getText().trim();
        String deadline = registrationDeadlineField.getText().trim();

        // Validation
        if (name.isEmpty() || start.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and start date are required.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!ValidationUtil.isDateYYYYMMDD(start)) {
            JOptionPane.showMessageDialog(this, "Invalid start date (YYYY-MM-DD).", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!end.isEmpty() && !ValidationUtil.isDateYYYYMMDD(end)) {
            JOptionPane.showMessageDialog(this, "Invalid end date (YYYY-MM-DD).", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!deadline.isEmpty() && !ValidationUtil.isDateYYYYMMDD(deadline)) {
            JOptionPane.showMessageDialog(this, "Invalid registration deadline (YYYY-MM-DD).", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update tournament object
        tournament.setTournamentName(name);
        tournament.setStartDate(start);
        tournament.setEndDate(end.isEmpty() ? null : end);
        tournament.setStatus((String) statusCombo.getSelectedItem());
        tournament.setMaxTeams((Integer) maxTeamsSpinner.getValue());

        // FIX: Cast Number to double
        tournament.setPrizePool(((Number) prizePoolSpinner.getValue()).doubleValue());

        int venueIdx = venueCombo.getSelectedIndex();
        int venueId = 0;
        if (venueIdx > 0) {
            String entry = (String) venueCombo.getSelectedItem();
            venueId = Integer.parseInt(entry.substring(entry.lastIndexOf(":") + 1, entry.lastIndexOf(")")));
        }
        tournament.setVenueId(venueId);
        tournament.setRegistrationDeadline(deadline.isEmpty() ? null : deadline);
        tournament.setMinPlayersPerTeam((Integer) minPlayersSpinner.getValue());

        // Save via controller
        try {
            boolean success = controller.updateTournament(tournament);
            if (success) {
                JOptionPane.showMessageDialog(this, "Tournament updated successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update tournament. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage() + "\nCheck if RLS policies allow updates.",
                "Update Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}