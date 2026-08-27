package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Team;
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

public class TeamPanel extends JPanel {

    private final DashboardController controller;
    private ETMSTable teamTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ETMSButton addBtn, refreshBtn, deleteBtn, editBtn;

    public TeamPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][grow][]"));
        initComponents();
        loadTeams();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Teams", "Manage registered teams.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        toolbar.setOpaque(false);
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search teams...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { filterTable(); }
        });
        toolbar.add(searchField, "width 200!");

        refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadTeams());
        toolbar.add(refreshBtn, "gapleft " + Spacing.SM);

        addBtn = new ETMSButton("+ Add Team", ETMSButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> openAddDialog());
        toolbar.add(addBtn, "gapleft " + Spacing.SM + ", align right");

        add(toolbar, "growx, wrap, gapbottom " + Spacing.LG);

        String[] columns = {"ID", "Name", "Tag", "Coach", "Wins", "Losses", "Rating"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        teamTable = new ETMSTable(tableModel);
        JScrollPane scroll = new JScrollPane(teamTable);
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

    private void loadTeams() {
        tableModel.setRowCount(0);
        List<Team> teams = controller.getAllTeams();
        for (Team t : teams) {
            tableModel.addRow(new Object[]{
                t.getTeamId(), t.getTeamName(), t.getTag(),
                t.getCoachName() != null ? t.getCoachName() : "None",
                t.getTotalWins(), t.getTotalLosses(), t.getEloRating()
            });
        }
    }

    private void filterTable() {
        String search = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (Team t : controller.getAllTeams()) {
            if (!search.isEmpty() && !t.getTeamName().toLowerCase().contains(search)) continue;
            tableModel.addRow(new Object[]{
                t.getTeamId(), t.getTeamName(), t.getTag(),
                t.getCoachName() != null ? t.getCoachName() : "None",
                t.getTotalWins(), t.getTotalLosses(), t.getEloRating()
            });
        }
    }

    private void openAddDialog() {
        TeamEditDialog dialog = new TeamEditDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Team team = dialog.getUpdatedTeam();
            boolean success = controller.addTeam(team);
            if (success) {
                JOptionPane.showMessageDialog(this, "Team added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadTeams();
            } else {
                // Error message is already shown by controller, but we could add a generic fallback.
                // The controller shows a dialog, so we don't need to duplicate.
                // We'll just reload to reflect any partial changes (though none should be saved).
                loadTeams();
            }
        }
    }

    private void editSelected() {
        int row = teamTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a team to edit.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        Team team = controller.getAllTeams().stream()
            .filter(t -> t.getTeamId() == id)
            .findFirst()
            .orElse(null);
        if (team != null) {
            TeamEditDialog dialog = new TeamEditDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), team);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Team updated = dialog.getUpdatedTeam();
                // updated already has the teamId set from copy
                boolean success = controller.updateTeam(updated); // we need a method for updating
                if (success) {
                    JOptionPane.showMessageDialog(this, "Team updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTeams();
                } else {
                    // error handled by controller
                    loadTeams();
                }
            }
        }
    }

    private void deleteSelected() {
        int row = teamTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a team to delete.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this team?") == JOptionPane.YES_OPTION) {
            if (controller.deleteTeam(id)) {
                loadTeams();
                JOptionPane.showMessageDialog(this, "Team deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Deletion failed.");
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

        if (teamTable != null) teamTable.refreshTheme();
        if (refreshBtn != null) refreshBtn.refreshTheme();
        if (addBtn != null) addBtn.refreshTheme();
        if (editBtn != null) editBtn.refreshTheme();
        if (deleteBtn != null) deleteBtn.refreshTheme();

        revalidate();
        repaint();
    }
}