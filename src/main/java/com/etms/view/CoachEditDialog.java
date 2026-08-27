package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Coach;
import com.etms.theme.ThemeManager;
import com.etms.ui.components.ETMSButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class CoachEditDialog extends JDialog {
    private final DashboardController controller;
    private final Coach originalCoach;
    private Coach updatedCoach;
    private boolean confirmed = false;

    // UI fields
    private JTextField firstNameField, lastNameField, specializationField, certificationField, experienceField, teamIdField;
    private JComboBox<String> statusCombo;

    public CoachEditDialog(JFrame parent, DashboardController controller, Coach coach) {
        super(parent, coach == null ? "Add Coach" : "Edit Coach", true);
        this.controller = controller;
        this.originalCoach = coach;

        if (coach == null) {
            this.updatedCoach = new Coach();
            this.updatedCoach.setStatus("ACTIVE");
        } else {
            this.updatedCoach = new Coach();
            copyCoach(coach, this.updatedCoach);
        }

        initComponents();
        populateFields();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(450, 450));
    }

    private void copyCoach(Coach from, Coach to) {
        if (from == null) return;
        to.setCoachId(from.getCoachId());
        to.setPersonId(from.getPersonId());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setSpecialization(from.getSpecialization());
        to.setCertification(from.getCertification());
        to.setExperienceYears(from.getExperienceYears());
        to.setTeamId(from.getTeamId());
        to.setStatus(from.getStatus() != null ? from.getStatus() : "ACTIVE");
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 2, fillx, insets 20", "[right][200:300:]", "[]10[]"));
        getContentPane().setBackground(ThemeManager.getBackground());

        // First Name
        add(new JLabel("First Name *:"));
        firstNameField = new JTextField(20);
        add(firstNameField, "growx");

        // Last Name
        add(new JLabel("Last Name *:"));
        lastNameField = new JTextField(20);
        add(lastNameField, "growx");

        // Specialization
        add(new JLabel("Specialization:"));
        specializationField = new JTextField(20);
        add(specializationField, "growx");

        // Certification
        add(new JLabel("Certification:"));
        certificationField = new JTextField(20);
        add(certificationField, "growx");

        // Years of Experience
        add(new JLabel("Years of Experience:"));
        experienceField = new JTextField(20);
        add(experienceField, "growx");

        // Team ID
        add(new JLabel("Team ID (Optional):"));
        teamIdField = new JTextField(20);
        add(teamIdField, "growx");

        // Status
        add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "SUSPENDED"});
        add(statusCombo, "growx");

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
        if (originalCoach == null) {
            firstNameField.setText("");
            lastNameField.setText("");
            specializationField.setText("");
            certificationField.setText("");
            experienceField.setText("");
            teamIdField.setText("");
            statusCombo.setSelectedItem("ACTIVE");
            return;
        }

        firstNameField.setText(originalCoach.getFirstName() != null ? originalCoach.getFirstName() : "");
        lastNameField.setText(originalCoach.getLastName() != null ? originalCoach.getLastName() : "");
        specializationField.setText(originalCoach.getSpecialization() != null ? originalCoach.getSpecialization() : "");
        certificationField.setText(originalCoach.getCertification() != null ? originalCoach.getCertification() : "");
        experienceField.setText(String.valueOf(originalCoach.getExperienceYears()));
        teamIdField.setText(originalCoach.getTeamId() > 0 ? String.valueOf(originalCoach.getTeamId()) : "");
        statusCombo.setSelectedItem(originalCoach.getStatus() != null ? originalCoach.getStatus() : "ACTIVE");
    }

    private void saveChanges() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String specialization = specializationField.getText().trim();
        String certification = certificationField.getText().trim();
        String expStr = experienceField.getText().trim();
        String teamIdStr = teamIdField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name and Last Name are required.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int experience = 0;
        if (!expStr.isEmpty()) {
            try {
                experience = Integer.parseInt(expStr);
                if (experience < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Experience must be a non-negative number.", "Validation", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        int teamId = 0;
        if (!teamIdStr.isEmpty()) {
            try {
                teamId = Integer.parseInt(teamIdStr);
                if (teamId < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Team ID must be a positive number.", "Validation", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Update coach object
        updatedCoach.setFirstName(firstName);
        updatedCoach.setLastName(lastName);
        updatedCoach.setSpecialization(specialization.isEmpty() ? null : specialization);
        updatedCoach.setCertification(certification.isEmpty() ? null : certification);
        updatedCoach.setExperienceYears(experience);
        updatedCoach.setTeamId(teamId);
        updatedCoach.setStatus(status);

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public Coach getUpdatedCoach() { return updatedCoach; }
}