package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.PrizeDistribution;
import com.etms.model.Tournament;
import com.etms.theme.ThemeManager;
import com.etms.ui.components.ETMSButton;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PrizeDistributionConfigDialog extends JDialog {

    private final DashboardController controller;
    private final Tournament tournament;
    private boolean confirmed = false;
    private List<PrizeDistribution> generatedDistributions = new ArrayList<>();

    private JTable configTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel, remainingLabel;

    public PrizeDistributionConfigDialog(JFrame parent, DashboardController controller, Tournament tournament) {
        super(parent, "Configure Prize Distributions", true);
        this.controller = controller;
        this.tournament = tournament;
        initComponents();
        populateDefaultConfig();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(500, 400));
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow][][][][][][]"));
        getContentPane().setBackground(ThemeManager.getBackground());

        JLabel title = new JLabel("Configure Prize Distribution for: " + tournament.getTournamentName());
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        add(title, "wrap, gapbottom 10");

        JLabel poolLabel = new JLabel("Total Prize Pool: $" + String.format("%.2f", tournament.getPrizePool()));
        poolLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        poolLabel.setForeground(ThemeManager.getAccent());
        add(poolLabel, "wrap, gapbottom 10");

        // Table: Rank, Percentage, Amount (auto-calculated)
        String[] columns = {"Rank", "Percentage (%)", "Amount ($)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 1; // only percentage is editable
            }
        };
        configTable = new JTable(tableModel);
        configTable.setRowHeight(30);
        configTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        configTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        configTable.getColumnModel().getColumn(2).setPreferredWidth(120);

        // Add listener to recalculate amounts when percentage changes
        tableModel.addTableModelListener(e -> {
            if (e.getColumn() == 1) {
                recalculateAmounts();
            }
        });

        JScrollPane scrollPane = new JScrollPane(configTable);
        add(scrollPane, "grow, wrap, gapbottom 10");

        // Summary labels
        totalLabel = new JLabel("Total: 0%");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(totalLabel, "split 2");

        remainingLabel = new JLabel("Remaining: 0%");
        remainingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        remainingLabel.setForeground(Color.GRAY);
        add(remainingLabel, "wrap");

        // Buttons for adding/removing ranks
        JPanel actionRow = new JPanel(new MigLayout("fillx, insets 0", "[][][]", "[]"));
        actionRow.setOpaque(false);

        ETMSButton addRankBtn = new ETMSButton("+ Add Rank", ETMSButton.Variant.SECONDARY);
        addRankBtn.addActionListener(e -> addRankRow());
        actionRow.add(addRankBtn);

        ETMSButton removeRankBtn = new ETMSButton("- Remove Last", ETMSButton.Variant.DANGER);
        removeRankBtn.addActionListener(e -> removeLastRow());
        actionRow.add(removeRankBtn);

        ETMSButton resetBtn = new ETMSButton("Reset Default", ETMSButton.Variant.GHOST);
        resetBtn.addActionListener(e -> populateDefaultConfig());
        actionRow.add(resetBtn);

        add(actionRow, "growx, wrap, gapbottom 10");

        // Buttons: Generate & Cancel
        JPanel buttonPanel = new JPanel(new MigLayout("align right"));
        buttonPanel.setOpaque(false);

        ETMSButton generateBtn = new ETMSButton("Generate", ETMSButton.Variant.PRIMARY);
        generateBtn.addActionListener(e -> generate());
        buttonPanel.add(generateBtn);

        ETMSButton cancelBtn = new ETMSButton("Cancel", ETMSButton.Variant.SECONDARY);
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn, "gapleft 10");

        add(buttonPanel, "span 2, align right");
    }

    private void populateDefaultConfig() {
        tableModel.setRowCount(0);
        // Default: 3 ranks: 50%, 30%, 20%
        addRow("1st", 50.0);
        addRow("2nd", 30.0);
        addRow("3rd", 20.0);
        recalculateAmounts();
    }

    private void addRankRow() {
        int rowCount = tableModel.getRowCount();
        String rankName = (rowCount + 1) + "th";
        if (rowCount == 0) rankName = "1st";
        else if (rowCount == 1) rankName = "2nd";
        else if (rowCount == 2) rankName = "3rd";
        else rankName = (rowCount + 1) + "th";
        addRow(rankName, 0.0);
        recalculateAmounts();
    }

    private void addRow(String rank, double percentage) {
        tableModel.addRow(new Object[]{rank, percentage, 0.0});
    }

    private void removeLastRow() {
        if (tableModel.getRowCount() > 1) {
            tableModel.removeRow(tableModel.getRowCount() - 1);
            recalculateAmounts();
        } else {
            JOptionPane.showMessageDialog(this, "You need at least one rank.", "Validation", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void recalculateAmounts() {
        double totalPrize = tournament.getPrizePool();
        int rowCount = tableModel.getRowCount();
        double totalPercent = 0.0;

        for (int i = 0; i < rowCount; i++) {
            Object val = tableModel.getValueAt(i, 1);
            double percent = 0.0;
            if (val != null && !val.toString().isEmpty()) {
                try {
                    percent = Double.parseDouble(val.toString());
                } catch (NumberFormatException ignored) {}
            }
            totalPercent += percent;
        }

        // Update amounts based on percentages
        for (int i = 0; i < rowCount; i++) {
            Object val = tableModel.getValueAt(i, 1);
            double percent = 0.0;
            if (val != null && !val.toString().isEmpty()) {
                try {
                    percent = Double.parseDouble(val.toString());
                } catch (NumberFormatException ignored) {}
            }
            double amount = totalPrize * (percent / 100.0);
            tableModel.setValueAt(String.format("%.2f", amount), i, 2);
        }

        // Update summary labels
        totalLabel.setText("Total: " + String.format("%.1f", totalPercent) + "%");
        double remaining = 100.0 - totalPercent;
        remainingLabel.setText("Remaining: " + String.format("%.1f", remaining) + "%");
        if (Math.abs(remaining) < 0.01) {
            remainingLabel.setForeground(new Color(46, 204, 113)); // green
        } else {
            remainingLabel.setForeground(Color.RED);
        }
    }

    private void generate() {
        int rowCount = tableModel.getRowCount();
        double totalPercent = 0.0;
        List<PrizeDistribution> list = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            String rank = (String) tableModel.getValueAt(i, 0);
            Object pVal = tableModel.getValueAt(i, 1);
            Object aVal = tableModel.getValueAt(i, 2);

            double percent = 0.0;
            double amount = 0.0;
            try {
                percent = Double.parseDouble(pVal.toString());
                amount = Double.parseDouble(aVal.toString());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number format at row " + (i+1), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (percent < 0 || percent > 100) {
                JOptionPane.showMessageDialog(this, "Percentage must be between 0 and 100 at row " + (i+1), "Validation", JOptionPane.ERROR_MESSAGE);
                return;
            }
            totalPercent += percent;

            PrizeDistribution pd = new PrizeDistribution();
            pd.setTournamentId(tournament.getTournamentId());
            pd.setPosition(i + 1);
            pd.setPercentage(percent);
            pd.setAmount(amount);
            pd.setAwarded(false);
            list.add(pd);
        }

        // Validate total percentage is 100%
        if (Math.abs(totalPercent - 100.0) > 0.01) {
            JOptionPane.showMessageDialog(this,
                "Total percentage must equal 100%. Current total: " + String.format("%.1f", totalPercent) + "%",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        generatedDistributions = list;
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public List<PrizeDistribution> getGeneratedDistributions() { return generatedDistributions; }
}