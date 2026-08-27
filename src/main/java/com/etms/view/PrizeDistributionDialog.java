package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.PrizeDistribution;
import com.etms.model.Tournament;
import com.etms.theme.ThemeManager;
import com.etms.ui.components.ETMSButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class PrizeDistributionDialog extends JDialog {
    private final DashboardController controller;
    private final Tournament tournament;
    private final PrizeDistribution originalDistribution;
    private PrizeDistribution updatedDistribution;
    private boolean confirmed = false;

    private JComboBox<Integer> positionCombo;
    private JTextField percentageField;
    private JTextField amountField;
    private JLabel totalLabel;

    public PrizeDistributionDialog(JFrame parent, DashboardController controller,
                                   PrizeDistribution distribution, Tournament tournament) {
        super(parent, distribution == null ? "Add Prize Distribution" : "Edit Prize Distribution", true);
        this.controller = controller;
        this.tournament = tournament;
        this.originalDistribution = distribution;

        if (distribution == null) {
            this.updatedDistribution = new PrizeDistribution();
            this.updatedDistribution.setTournamentId(tournament.getTournamentId());
            this.updatedDistribution.setPosition(1);
            this.updatedDistribution.setPercentage(0.0);
            this.updatedDistribution.setAmount(0.0);
        } else {
            this.updatedDistribution = new PrizeDistribution();
            copyDistribution(distribution, this.updatedDistribution);
        }

        initComponents();
        populateFields();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(400, 280));
    }

    private void copyDistribution(PrizeDistribution from, PrizeDistribution to) {
        to.setDistributionId(from.getDistributionId());
        to.setTournamentId(from.getTournamentId());
        to.setPosition(from.getPosition());
        to.setPercentage(from.getPercentage());
        to.setAmount(from.getAmount());
        to.setAwarded(from.isAwarded());
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 2, fillx, insets 20", "[right][200:300:]", "[]10[]"));
        getContentPane().setBackground(ThemeManager.getBackground());

        // Position
        add(new JLabel("Rank/Position:"));
        positionCombo = new JComboBox<>();
        for (int i = 1; i <= 8; i++) {
            positionCombo.addItem(i);
        }
        positionCombo.addActionListener(e -> updateAmount());
        add(positionCombo, "growx");

        // Percentage
        add(new JLabel("Percentage (%):"));
        percentageField = new JTextField(20);
        percentageField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateAmount(); }
            @Override public void removeUpdate(DocumentEvent e) { updateAmount(); }
            @Override public void changedUpdate(DocumentEvent e) { updateAmount(); }
        });
        add(percentageField, "growx");

        // Amount (auto-calculated)
        add(new JLabel("Amount ($):"));
        amountField = new JTextField(20);
        amountField.setEditable(false);
        amountField.setBackground(ThemeManager.getSurface());
        add(amountField, "growx");

        // Total prize pool info
        totalLabel = new JLabel("Total Prize Pool: $" + String.format("%.2f", tournament.getPrizePool()));
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        totalLabel.setForeground(Color.GRAY);
        add(totalLabel, "span 2, align center, gaptop 5, gapbottom 5");

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
        if (originalDistribution == null) {
            positionCombo.setSelectedIndex(0);
            percentageField.setText("");
            amountField.setText("");
            return;
        }

        positionCombo.setSelectedItem(originalDistribution.getPosition());
        percentageField.setText(String.valueOf(originalDistribution.getPercentage()));
        amountField.setText(String.format("%.2f", originalDistribution.getAmount()));
    }

    private void updateAmount() {
        try {
            String percentText = percentageField.getText().trim();
            if (percentText.isEmpty()) {
                amountField.setText("");
                return;
            }
            double percent = Double.parseDouble(percentText);
            double totalPrize = tournament.getPrizePool();
            double amount = totalPrize * (percent / 100.0);
            amountField.setText(String.format("%.2f", amount));
        } catch (NumberFormatException e) {
            amountField.setText("");
        }
    }

    private void saveChanges() {
        int position = (Integer) positionCombo.getSelectedItem();
        String percentStr = percentageField.getText().trim();

        if (percentStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Percentage is required.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double percentage;
        double amount;
        try {
            percentage = Double.parseDouble(percentStr);
            if (percentage < 0 || percentage > 100) {
                JOptionPane.showMessageDialog(this, "Percentage must be between 0 and 100.", "Validation", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Recalculate amount to be safe
            amount = tournament.getPrizePool() * (percentage / 100.0);
            if (amount < 0) {
                JOptionPane.showMessageDialog(this, "Amount cannot be negative.", "Validation", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for percentage.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        updatedDistribution.setPosition(position);
        updatedDistribution.setPercentage(percentage);
        updatedDistribution.setAmount(amount);

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public PrizeDistribution getUpdatedDistribution() { return updatedDistribution; }
}