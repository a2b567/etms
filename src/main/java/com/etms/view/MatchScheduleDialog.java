package com.etms.view;

import com.etms.components.AppButton;
import com.etms.controller.DashboardController;
import com.etms.dao.TeamDAO;
import com.etms.model.Match;
import com.etms.model.Referee;
import com.etms.model.Team;
import com.etms.theme.ColorPalette;
import com.etms.theme.Typography;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Dialog for creating or editing a match with referee assignment and score entry.
 */
public class MatchScheduleDialog extends JDialog {
    private final DashboardController controller;
    private final Match match;
    private boolean confirmed = false;

    // UI Components
    private JSpinner roundSpinner;
    private JSpinner matchNumberSpinner;
    private JComboBox<Team> team1Combo;
    private JComboBox<Team> team2Combo;
    private JTextField timeField;
    private JComboBox<String> statusCombo;
    private JComboBox<Referee> refereeCombo;
    // New score fields
    private JSpinner team1ScoreSpinner;
    private JSpinner team2ScoreSpinner;
    private JComboBox<Team> winnerCombo;

    public MatchScheduleDialog(JFrame parent, DashboardController controller, Match match) {
        super(parent, match.getMatchId() == 0 ? "Create Match" : "Edit Match", true);
        this.controller = controller;
        this.match = match;
        setSize(550, 650);
        setLocationRelativeTo(parent);
        initComponents();
        populateData();
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[right][grow]", "[]10[]"));
        setBackground(ColorPalette.LIGHT_BG);

        // Round
        add(new JLabel("Round:"));
        roundSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        add(roundSpinner, "growx, wrap");

        // Match Number
        add(new JLabel("Match Number:"));
        matchNumberSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        add(matchNumberSpinner, "growx, wrap");

        // Team 1
        add(new JLabel("Team 1:"));
        team1Combo = new JComboBox<>();
        add(team1Combo, "growx, wrap");

        // Team 2
        add(new JLabel("Team 2:"));
        team2Combo = new JComboBox<>();
        add(team2Combo, "growx, wrap");

        // Scheduled Time
        add(new JLabel("Scheduled Time (YYYY-MM-DD HH:MM):"));
        timeField = new JTextField(20);
        timeField.setToolTipText("e.g., 2026-08-15 14:30");
        add(timeField, "growx, wrap");

        // Status
        add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"SCHEDULED", "LIVE", "COMPLETED", "CANCELLED"});
        add(statusCombo, "growx, wrap");

        // Referee
        add(new JLabel("Referee:"));
        refereeCombo = new JComboBox<>();
        add(refereeCombo, "growx, wrap");

        // Score 1
        add(new JLabel("Team 1 Score:"));
        team1ScoreSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        add(team1ScoreSpinner, "growx, wrap");

        // Score 2
        add(new JLabel("Team 2 Score:"));
        team2ScoreSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
        add(team2ScoreSpinner, "growx, wrap");

        // Winner
        add(new JLabel("Winner:"));
        winnerCombo = new JComboBox<>();
        winnerCombo.addItem(null); // "None"
        add(winnerCombo, "growx, wrap");

        // Buttons
        JPanel buttons = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow]", "[]"));
        JButton saveBtn = new AppButton("Save", AppButton.ButtonType.PRIMARY);
        saveBtn.addActionListener(e -> save());
        buttons.add(saveBtn, "growx");
        JButton cancelBtn = new AppButton("Cancel", AppButton.ButtonType.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());
        buttons.add(cancelBtn, "growx");
        add(buttons, "span 2, growx");
    }

    private void populateData() {
        // Load teams
        try {
            List<Team> teams = new TeamDAO().getAllTeams();
            team1Combo.removeAllItems();
            team2Combo.removeAllItems();
            winnerCombo.removeAllItems();
            winnerCombo.addItem(null); // "None"
            for (Team t : teams) {
                team1Combo.addItem(t);
                team2Combo.addItem(t);
                winnerCombo.addItem(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load teams: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Load referees
        refereeCombo.removeAllItems();
        refereeCombo.addItem(null);
        List<Referee> referees = controller.getActiveReferees();
        for (Referee r : referees) {
            refereeCombo.addItem(r);
        }

        // If editing, populate fields
        if (match.getMatchId() != 0) {
            roundSpinner.setValue(match.getRoundNumber());
            matchNumberSpinner.setValue(match.getMatchNumber());
            selectTeam(team1Combo, match.getTeam1Id());
            selectTeam(team2Combo, match.getTeam2Id());
            timeField.setText(match.getScheduledTime() != null ? match.getScheduledTime() : "");
            statusCombo.setSelectedItem(match.getStatus());
            team1ScoreSpinner.setValue(match.getTeam1Score());
            team2ScoreSpinner.setValue(match.getTeam2Score());
            selectTeam(winnerCombo, match.getWinnerTeamId());

            // Select referee
            if (match.getRefereeId() != 0) {
                for (int i = 0; i < refereeCombo.getItemCount(); i++) {
                    Referee r = refereeCombo.getItemAt(i);
                    if (r != null && r.getRefereeId() == match.getRefereeId()) {
                        refereeCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } else {
            // Default values
            roundSpinner.setValue(1);
            matchNumberSpinner.setValue(1);
            statusCombo.setSelectedItem("SCHEDULED");
            team1ScoreSpinner.setValue(0);
            team2ScoreSpinner.setValue(0);
            winnerCombo.setSelectedIndex(0);
        }
    }

    private void selectTeam(JComboBox<Team> combo, int teamId) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            Team t = combo.getItemAt(i);
            if (t != null && t.getTeamId() == teamId) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void save() {
        int round = (Integer) roundSpinner.getValue();
        int matchNumber = (Integer) matchNumberSpinner.getValue();
        Team t1 = (Team) team1Combo.getSelectedItem();
        Team t2 = (Team) team2Combo.getSelectedItem();
        String time = timeField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();
        Referee referee = (Referee) refereeCombo.getSelectedItem();
        int score1 = (Integer) team1ScoreSpinner.getValue();
        int score2 = (Integer) team2ScoreSpinner.getValue();
        Team winner = (Team) winnerCombo.getSelectedItem();

        // Validation
        if (t1 == null || t2 == null) {
            JOptionPane.showMessageDialog(this, "Please select both teams.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (t1.getTeamId() == t2.getTeamId()) {
            JOptionPane.showMessageDialog(this, "Team 1 and Team 2 cannot be the same.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (time.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Scheduled time is required.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!time.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Time must be in format YYYY-MM-DD HH:MM (e.g., 2026-08-15 14:30).", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update match object
        match.setRoundNumber(round);
        match.setMatchNumber(matchNumber);
        match.setTeam1Id(t1.getTeamId());
        match.setTeam2Id(t2.getTeamId());
        match.setTeam1Name(t1.getTeamName());
        match.setTeam2Name(t2.getTeamName());
        match.setScheduledTime(time);
        match.setStatus(status);
        match.setRefereeId(referee != null ? referee.getRefereeId() : 0);
        match.setTeam1Score(score1);
        match.setTeam2Score(score2);
        match.setWinnerTeamId(winner != null ? winner.getTeamId() : 0);
        if (winner != null) {
            match.setWinnerTeamName(winner.getTeamName());
        } else {
            match.setWinnerTeamName(null);
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public Match getUpdatedMatch() { return match; }
}