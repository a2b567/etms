package com.etms.view;

import com.etms.dao.CoachDAO;
import com.etms.model.Coach;
import com.etms.model.Team;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TeamEditDialog extends JDialog {
    private JTextField teamNameField, tagField;
    private JComboBox<String> coachCombo;
    private JComboBox<String> statusCombo;
    private List<Coach> coaches;
    private boolean confirmed = false;
    private Team originalTeam;
    private Team updatedTeam;

    public TeamEditDialog(JFrame parent, Team team) {
        super(parent, team == null ? "Add Team" : "Edit Team", true);
        this.originalTeam = team;
        
        // Create a new team for "Add" mode, or copy existing for "Edit"
        if (team == null) {
            this.updatedTeam = new Team();
            this.updatedTeam.setStatus("ACTIVE"); // default status
        } else {
            this.updatedTeam = new Team();
            copyTeam(team, this.updatedTeam);
        }
        
        loadCoaches();
        initComponents();
        populateFields();
        pack();
        setLocationRelativeTo(getParent());
    }

    private void copyTeam(Team from, Team to) {
        if (from == null) {
            // Should not happen, but safe guard
            return;
        }
        to.setTeamId(from.getTeamId());
        to.setTeamName(from.getTeamName());
        to.setTag(from.getTag());
        to.setCoachId(from.getCoachId());
        to.setCoachName(from.getCoachName());
        to.setStatus(from.getStatus() != null ? from.getStatus() : "ACTIVE");
    }

    private void loadCoaches() {
        try {
            coaches = new CoachDAO().getAllCoaches();
        } catch (SQLException e) {
            e.printStackTrace();
            coaches = List.of();
        }
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 2, fillx, insets 20", "[right][200:300:]", "[]10[]"));

        add(new JLabel("Team Name:"));
        teamNameField = new JTextField(20);
        add(teamNameField, "growx");

        add(new JLabel("Team Tag:"));
        tagField = new JTextField(10);
        add(tagField, "growx");

        add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "BANNED"});
        add(statusCombo, "growx");

        add(new JLabel("Coach:"));
        coachCombo = new JComboBox<>();
        coachCombo.addItem("-- No Coach --");
        for (Coach c : coaches) {
            coachCombo.addItem(c.getFullName() + " (ID:" + c.getCoachId() + ")");
        }
        add(coachCombo, "growx");

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new Color(0, 120, 215));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> saveChanges());
        add(saveBtn, "span 2, split 2, align right");

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        add(cancelBtn);

        setMinimumSize(new Dimension(400, 300));
    }

    private void populateFields() {
        if (originalTeam == null) {
            // New team – default values
            teamNameField.setText("");
            tagField.setText("");
            statusCombo.setSelectedItem("ACTIVE");
            coachCombo.setSelectedIndex(0);
            return;
        }

        // Edit mode – populate from existing team
        teamNameField.setText(originalTeam.getTeamName());
        tagField.setText(originalTeam.getTag());
        
        // Set status
        String status = originalTeam.getStatus();
        if (status != null) {
            statusCombo.setSelectedItem(status);
        } else {
            statusCombo.setSelectedItem("ACTIVE");
        }

        // Set coach
        if (originalTeam.getCoachId() > 0) {
            for (int i = 0; i < coaches.size(); i++) {
                if (coaches.get(i).getCoachId() == originalTeam.getCoachId()) {
                    coachCombo.setSelectedIndex(i + 1); // +1 because first item is "-- No Coach --"
                    break;
                }
            }
        } else {
            coachCombo.setSelectedIndex(0);
        }
    }

    private void saveChanges() {
        String name = teamNameField.getText().trim();
        String tag = tagField.getText().trim().toUpperCase();
        String status = (String) statusCombo.getSelectedItem();

        if (name.isEmpty() || tag.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and tag are required.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        updatedTeam.setTeamName(name);
        updatedTeam.setTag(tag);
        updatedTeam.setStatus(status); // Set the status from dropdown

        int coachIdx = coachCombo.getSelectedIndex();
        if (coachIdx > 0) {
            Coach selected = coaches.get(coachIdx - 1);
            updatedTeam.setCoachId(selected.getCoachId());
            updatedTeam.setCoachName(selected.getFullName());
        } else {
            updatedTeam.setCoachId(0);
            updatedTeam.setCoachName("");
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { 
        return confirmed; 
    }

    public Team getUpdatedTeam() { 
        return updatedTeam; 
    }
}