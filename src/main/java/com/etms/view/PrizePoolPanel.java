package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.PrizeDistribution;
import com.etms.model.Tournament;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import com.etms.ui.components.ETMSButton;
import com.etms.ui.components.ETMSPageHeader;
import com.etms.ui.components.ETMSTable;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PrizePoolPanel extends JPanel {

    private final DashboardController controller;
    private ETMSTable prizeTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> tournamentCombo;
    private List<Tournament> tournaments;
    private ETMSButton addBtn, refreshBtn, deleteBtn, editBtn, generateBtn;
    private JLabel totalPrizeLabel;

    public PrizePoolPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][][grow][]"));
        initComponents();
        loadTournaments();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Prize Pools", "Manage tournament prize distributions.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        // Tournament selector
        JPanel filterPanel = new JPanel(new MigLayout("fillx, insets 0", "[][grow]", "[]"));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Tournament:"));
        tournamentCombo = new JComboBox<>();
        tournamentCombo.setFont(Typography.BODY);
        tournamentCombo.addActionListener(e -> loadPrizeDistributions());
        filterPanel.add(tournamentCombo, "growx, width 250!");

        refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadPrizeDistributions());
        filterPanel.add(refreshBtn, "gapleft " + Spacing.SM);

        generateBtn = new ETMSButton("Configure & Generate", ETMSButton.Variant.PRIMARY);
        generateBtn.addActionListener(e -> openConfigDialog());
        filterPanel.add(generateBtn, "gapleft " + Spacing.SM);

        addBtn = new ETMSButton("+ Add Prize", ETMSButton.Variant.SECONDARY);
        addBtn.addActionListener(e -> openAddDialog());
        filterPanel.add(addBtn, "gapleft " + Spacing.SM);

        add(filterPanel, "growx, wrap, gapbottom " + Spacing.LG);

        // Total prize label
        totalPrizeLabel = new JLabel("Total Prize Pool: $0.00 | Distributed: $0.00 | Remaining: $0.00");
        totalPrizeLabel.setFont(Typography.BODY_BOLD);
        totalPrizeLabel.setForeground(ThemeManager.getAccent());
        add(totalPrizeLabel, "wrap, gapbottom " + Spacing.SM);

        String[] columns = {"Rank", "Prize Amount", "Percentage"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        prizeTable = new ETMSTable(tableModel);
        JScrollPane scroll = new JScrollPane(prizeTable);
        add(scroll, "grow, wrap, gapbottom " + Spacing.LG);

        JPanel actionPanel = new JPanel(new MigLayout("fillx, insets 0", "[][][]", "[]"));
        actionPanel.setOpaque(false);
        editBtn = new ETMSButton("Edit", ETMSButton.Variant.SECONDARY);
        editBtn.addActionListener(e -> editSelected());
        actionPanel.add(editBtn);

        deleteBtn = new ETMSButton("Delete", ETMSButton.Variant.DANGER);
        deleteBtn.addActionListener(e -> deleteSelected());
        actionPanel.add(deleteBtn);

        add(actionPanel, "growx");
    }

    private void loadTournaments() {
        tournamentCombo.removeAllItems();
        tournaments = controller.getAllTournaments();
        for (Tournament t : tournaments) {
            tournamentCombo.addItem(t.getTournamentName() + " (ID:" + t.getTournamentId() + ")");
        }
        if (!tournaments.isEmpty()) {
            loadPrizeDistributions();
        }
    }

    private void loadPrizeDistributions() {
        tableModel.setRowCount(0);
        int idx = tournamentCombo.getSelectedIndex();
        if (idx < 0 || tournaments.isEmpty()) return;

        int tournamentId = tournaments.get(idx).getTournamentId();
        Tournament tournament = tournaments.get(idx);
        double totalPrize = tournament.getPrizePool();

        List<PrizeDistribution> distributions = controller.getPrizeDistributions(tournamentId);
        if (distributions.isEmpty()) {
            totalPrizeLabel.setText("Total Prize Pool: $" + String.format("%.2f", totalPrize) + " (No distributions set)");
            return;
        }

        double totalDistributed = 0;
        for (PrizeDistribution pd : distributions) {
            String rank = pd.getPosition() == 1 ? "1st" :
                          pd.getPosition() == 2 ? "2nd" :
                          pd.getPosition() == 3 ? "3rd" :
                          pd.getPosition() + "th";
            tableModel.addRow(new Object[]{
                rank,
                "$" + String.format("%.2f", pd.getAmount()),
                String.format("%.1f%%", pd.getPercentage())
            });
            totalDistributed += pd.getAmount();
        }

        totalPrizeLabel.setText(String.format("Total Prize Pool: $%.2f | Distributed: $%.2f | Remaining: $%.2f",
                totalPrize, totalDistributed, totalPrize - totalDistributed));
    }

    private void openConfigDialog() {
        int idx = tournamentCombo.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Please select a tournament first.");
            return;
        }
        Tournament tournament = tournaments.get(idx);

        PrizeDistributionConfigDialog dialog = new PrizeDistributionConfigDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), controller, tournament);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            List<PrizeDistribution> distributions = dialog.getGeneratedDistributions();
            boolean success = controller.generatePrizeDistributions(tournament.getTournamentId(), distributions);
            if (success) {
                JOptionPane.showMessageDialog(this, "Prize distributions generated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPrizeDistributions();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to generate prize distributions.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openAddDialog() {
        int idx = tournamentCombo.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Please select a tournament first.");
            return;
        }
        int tournamentId = tournaments.get(idx).getTournamentId();
        Tournament tournament = tournaments.get(idx);

        PrizeDistributionDialog dialog = new PrizeDistributionDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), controller, null, tournament);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            PrizeDistribution pd = dialog.getUpdatedDistribution();
            if (controller.createPrizeDistribution(pd)) {
                JOptionPane.showMessageDialog(this, "Prize distribution added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadPrizeDistributions();
            }
        }
    }

    private void editSelected() {
        int row = prizeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a prize distribution to edit.");
            return;
        }

        int idx = tournamentCombo.getSelectedIndex();
        if (idx < 0) return;
        int tournamentId = tournaments.get(idx).getTournamentId();

        List<PrizeDistribution> distributions = controller.getPrizeDistributions(tournamentId);
        if (row >= distributions.size()) return;

        PrizeDistribution selected = distributions.get(row);
        if (selected != null) {
            Tournament tournament = tournaments.get(idx);
            PrizeDistributionDialog dialog = new PrizeDistributionDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), controller, selected, tournament);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                PrizeDistribution updated = dialog.getUpdatedDistribution();
                if (controller.updatePrizeDistribution(updated)) {
                    JOptionPane.showMessageDialog(this, "Prize distribution updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadPrizeDistributions();
                }
            }
        }
    }

    private void deleteSelected() {
        int row = prizeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a prize distribution to delete.");
            return;
        }

        int idx = tournamentCombo.getSelectedIndex();
        if (idx < 0) return;
        int tournamentId = tournaments.get(idx).getTournamentId();

        List<PrizeDistribution> distributions = controller.getPrizeDistributions(tournamentId);
        if (row >= distributions.size()) return;

        PrizeDistribution selected = distributions.get(row);
        if (JOptionPane.showConfirmDialog(this, "Delete this prize distribution?") == JOptionPane.YES_OPTION) {
            if (controller.deletePrizeDistribution(selected.getDistributionId())) {
                loadPrizeDistributions();
                JOptionPane.showMessageDialog(this, "Prize distribution deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Deletion failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getBackground());

        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(ThemeManager.getBackground());
            }
            if (comp instanceof JScrollPane) {
                comp.setBackground(ThemeManager.getBackground());
                ((JScrollPane) comp).getViewport().setBackground(ThemeManager.getSurface());
            }
        }

        if (prizeTable != null) prizeTable.refreshTheme();
        if (refreshBtn != null) refreshBtn.refreshTheme();
        if (addBtn != null) addBtn.refreshTheme();
        if (editBtn != null) editBtn.refreshTheme();
        if (deleteBtn != null) deleteBtn.refreshTheme();
        if (generateBtn != null) generateBtn.refreshTheme();

        revalidate();
        repaint();
    }
}