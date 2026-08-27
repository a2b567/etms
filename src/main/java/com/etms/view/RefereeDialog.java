package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Referee;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.ui.components.ETMSButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class RefereeDialog extends JDialog {
    private final DashboardController controller;
    private final Referee referee; // null for create, existing for edit

    private JTextField firstNameField, lastNameField, emailField, phoneField, qualificationField, experienceField;
    private JComboBox<String> statusCombo;

    public RefereeDialog(JFrame parent, DashboardController controller, Referee existing) {
        super(parent, existing == null ? "Add Referee" : "Edit Referee", true);
        this.controller = controller;
        this.referee = existing;
        setSize(400, 450);
        setLocationRelativeTo(parent);
        initComponents();
        if (existing != null) populateFields();
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[right][grow]", "[]10[]"));
        getContentPane().setBackground(ThemeManager.getBackground());

        // First Name
        add(new JLabel("First Name *:"));
        firstNameField = new JTextField();
        add(firstNameField, "growx, wrap");

        // Last Name
        add(new JLabel("Last Name *:"));
        lastNameField = new JTextField();
        add(lastNameField, "growx, wrap");

        // Email
        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField, "growx, wrap");

        // Phone
        add(new JLabel("Phone:"));
        phoneField = new JTextField();
        add(phoneField, "growx, wrap");

        // Qualification
        add(new JLabel("Qualification:"));
        qualificationField = new JTextField();
        add(qualificationField, "growx, wrap");

        // Years of Experience
        add(new JLabel("Years of Experience:"));
        experienceField = new JTextField();
        add(experienceField, "growx, wrap");

        // Status
        add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE", "SUSPENDED"});
        add(statusCombo, "growx, wrap");

        // Buttons
        JPanel buttonPanel = new JPanel(new MigLayout("align right"));
        buttonPanel.setOpaque(false);

        ETMSButton saveBtn = new ETMSButton("Save", ETMSButton.Variant.PRIMARY);
        saveBtn.addActionListener(e -> save());
        buttonPanel.add(saveBtn);

        ETMSButton cancelBtn = new ETMSButton("Cancel", ETMSButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn, "gapleft 10");

        add(buttonPanel, "span 2, align right, gaptop 10");
    }

    private void populateFields() {
        firstNameField.setText(referee.getFirstName());
        lastNameField.setText(referee.getLastName());
        emailField.setText(referee.getEmail() != null ? referee.getEmail() : "");
        phoneField.setText(referee.getPhone() != null ? referee.getPhone() : "");
        qualificationField.setText(referee.getQualification() != null ? referee.getQualification() : "");
        experienceField.setText(String.valueOf(referee.getYearsExperience()));
        statusCombo.setSelectedItem(referee.getStatus());
    }

    private void save() {
        // Trim all inputs
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String qualification = qualificationField.getText().trim();
        String expText = experienceField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        // Validation: only first and last name are required
        if (firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name and Last Name are required.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Email validation (optional but if provided, must be valid)
        if (!email.isEmpty() && (!email.contains("@") || !email.contains("."))) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address (e.g., name@domain.com).", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int years = 0;
        if (!expText.isEmpty()) {
            try {
                years = Integer.parseInt(expText);
                if (years < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Years of experience must be a non-negative integer.", "Validation", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Create or update referee object
        Referee r = (referee != null) ? referee : new Referee();
        r.setFirstName(firstName);
        r.setLastName(lastName);
        r.setEmail(email.isEmpty() ? null : email);
        r.setPhone(phone.isEmpty() ? null : phone);
        r.setQualification(qualification.isEmpty() ? null : qualification);
        r.setYearsExperience(years);
        r.setStatus(status);

        boolean success;
        try {
            if (referee == null) {
                success = controller.createReferee(r);
            } else {
                success = controller.updateReferee(r);
            }
            if (success) {
                JOptionPane.showMessageDialog(this, "Referee saved successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save referee. Check console for details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}