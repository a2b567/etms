package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Staff;
import com.etms.model.Team;
import com.etms.theme.ThemeManager;
import com.etms.ui.components.ETMSButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StaffEditDialog extends JDialog {
    private final DashboardController controller;
    private final Staff originalStaff;
    private Staff updatedStaff;
    private boolean confirmed = false;

    // UI fields
    private JTextField firstNameField, lastNameField, emailField, phoneField, roleField;
    private JComboBox<Team> teamCombo;  // replaced text field with combo
    private JComboBox<String> statusCombo;

    public StaffEditDialog(JFrame parent, DashboardController controller, Staff staff) {
        super(parent, staff == null ? "Add Staff" : "Edit Staff", true);
        this.controller = controller;
        this.originalStaff = staff;

        if (staff == null) {
            this.updatedStaff = new Staff();
            this.updatedStaff.setStatus("ACTIVE");
        } else {
            this.updatedStaff = new Staff();
            copyStaff(staff, this.updatedStaff);
        }

        initComponents();
        populateFields();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(450, 450));
    }

    private void copyStaff(Staff from, Staff to) {
        if (from == null) return;
        to.setStaffId(from.getStaffId());
        to.setPersonId(from.getPersonId());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setEmail(from.getEmail());
        to.setPhone(from.getPhone());
        to.setStaffRole(from.getStaffRole());
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

        // Email
        add(new JLabel("Email:"));
        emailField = new JTextField(20);
        add(emailField, "growx");

        // Phone
        add(new JLabel("Phone:"));
        phoneField = new JTextField(20);
        add(phoneField, "growx");

        // Role
        add(new JLabel("Role *:"));
        roleField = new JTextField(20);
        add(roleField, "growx");

        // Team – Combo Box instead of text field
        add(new JLabel("Team:"));
        teamCombo = new JComboBox<>();
        teamCombo.addItem(null); // "None" option
        List<Team> teams = controller.getAllTeams();
        for (Team t : teams) {
            teamCombo.addItem(t);
        }
        add(teamCombo, "growx");

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
        if (originalStaff == null) {
            firstNameField.setText("");
            lastNameField.setText("");
            emailField.setText("");
            phoneField.setText("");
            roleField.setText("");
            teamCombo.setSelectedIndex(0); // None
            statusCombo.setSelectedItem("ACTIVE");
            return;
        }

        firstNameField.setText(originalStaff.getFirstName() != null ? originalStaff.getFirstName() : "");
        lastNameField.setText(originalStaff.getLastName() != null ? originalStaff.getLastName() : "");
        emailField.setText(originalStaff.getEmail() != null ? originalStaff.getEmail() : "");
        phoneField.setText(originalStaff.getPhone() != null ? originalStaff.getPhone() : "");
        roleField.setText(originalStaff.getStaffRole() != null ? originalStaff.getStaffRole() : "");

        // Select the team if one is assigned
        if (originalStaff.getTeamId() > 0) {
            for (int i = 0; i < teamCombo.getItemCount(); i++) {
                Team t = teamCombo.getItemAt(i);
                if (t != null && t.getTeamId() == originalStaff.getTeamId()) {
                    teamCombo.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            teamCombo.setSelectedIndex(0); // None
        }

        statusCombo.setSelectedItem(originalStaff.getStatus() != null ? originalStaff.getStatus() : "ACTIVE");
    }

    private void saveChanges() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = roleField.getText().trim();
        Team selectedTeam = (Team) teamCombo.getSelectedItem();
        String status = (String) statusCombo.getSelectedItem();

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || role.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name, Last Name, and Role are required.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Email validation (optional)
        if (!email.isEmpty() && (!email.contains("@") || !email.contains("."))) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update staff object
        updatedStaff.setFirstName(firstName);
        updatedStaff.setLastName(lastName);
        updatedStaff.setEmail(email.isEmpty() ? null : email);
        updatedStaff.setPhone(phone.isEmpty() ? null : phone);
        updatedStaff.setStaffRole(role);
        updatedStaff.setTeamId(selectedTeam != null ? selectedTeam.getTeamId() : 0);
        updatedStaff.setStatus(status);

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public Staff getUpdatedStaff() { return updatedStaff; }
}