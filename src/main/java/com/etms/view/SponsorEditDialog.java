package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Sponsor;
import com.etms.theme.ThemeManager;
import com.etms.ui.components.ETMSButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class SponsorEditDialog extends JDialog {
    private final DashboardController controller;
    private final Sponsor originalSponsor;
    private Sponsor updatedSponsor;
    private boolean confirmed = false;

    // UI fields
    private JTextField companyField, emailField, amountField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> statusCombo;

    public SponsorEditDialog(JFrame parent, DashboardController controller, Sponsor sponsor) {
        super(parent, sponsor == null ? "Add Sponsor" : "Edit Sponsor", true);
        this.controller = controller;
        this.originalSponsor = sponsor;

        if (sponsor == null) {
            this.updatedSponsor = new Sponsor();
            this.updatedSponsor.setStatus("ACTIVE");
            this.updatedSponsor.setCategory("BRONZE");
        } else {
            this.updatedSponsor = new Sponsor();
            copySponsor(sponsor, this.updatedSponsor);
        }

        initComponents();
        populateFields();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(450, 400));
    }

    private void copySponsor(Sponsor from, Sponsor to) {
        if (from == null) return;
        to.setSponsorId(from.getSponsorId());
        to.setCompanyName(from.getCompanyName());
        to.setCategory(from.getCategory());
        to.setSponsorshipAmount(from.getSponsorshipAmount());
        to.setContactEmail(from.getContactEmail());
        to.setStatus(from.getStatus() != null ? from.getStatus() : "ACTIVE");
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 2, fillx, insets 20", "[right][200:300:]", "[]10[]"));
        getContentPane().setBackground(ThemeManager.getBackground());

        // Company Name
        add(new JLabel("Company Name *:"));
        companyField = new JTextField(20);
        add(companyField, "growx");

        // Category
        add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>(new String[]{"BRONZE", "SILVER", "GOLD", "PLATINUM", "PARTNER"});
        add(categoryCombo, "growx");

        // Sponsorship Amount
        add(new JLabel("Amount ($):"));
        amountField = new JTextField(20);
        add(amountField, "growx");

        // Contact Email
        add(new JLabel("Contact Email:"));
        emailField = new JTextField(20);
        add(emailField, "growx");

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
        if (originalSponsor == null) {
            companyField.setText("");
            categoryCombo.setSelectedItem("BRONZE");
            amountField.setText("");
            emailField.setText("");
            statusCombo.setSelectedItem("ACTIVE");
            return;
        }

        companyField.setText(originalSponsor.getCompanyName() != null ? originalSponsor.getCompanyName() : "");
        categoryCombo.setSelectedItem(originalSponsor.getCategory() != null ? originalSponsor.getCategory() : "BRONZE");
        amountField.setText(String.valueOf(originalSponsor.getSponsorshipAmount()));
        emailField.setText(originalSponsor.getContactEmail() != null ? originalSponsor.getContactEmail() : "");
        statusCombo.setSelectedItem(originalSponsor.getStatus() != null ? originalSponsor.getStatus() : "ACTIVE");
    }

    private void saveChanges() {
        String company = companyField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();
        String amountStr = amountField.getText().trim();
        String email = emailField.getText().trim();
        String status = (String) statusCombo.getSelectedItem();

        // Validation
        if (company.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Company Name is required.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Email validation (optional)
        if (!email.isEmpty() && (!email.contains("@") || !email.contains("."))) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double amount = 0.0;
        if (!amountStr.isEmpty()) {
            try {
                amount = Double.parseDouble(amountStr);
                if (amount < 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Amount must be a positive number.", "Validation", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Update sponsor object
        updatedSponsor.setCompanyName(company);
        updatedSponsor.setCategory(category);
        updatedSponsor.setSponsorshipAmount(amount);
        updatedSponsor.setContactEmail(email.isEmpty() ? null : email);
        updatedSponsor.setStatus(status);

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public Sponsor getUpdatedSponsor() { return updatedSponsor; }
}