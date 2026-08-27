package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Coach;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import com.etms.ui.components.ETMSButton;
import com.etms.ui.components.ETMSPageHeader;
import com.etms.ui.components.ETMSTable;
import com.etms.ui.components.ETMSStatusBadge;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CoachPanel extends JPanel {

    private final DashboardController controller;
    private ETMSTable coachTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ETMSButton addBtn, refreshBtn, deleteBtn, editBtn;

    public CoachPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][grow][]"));
        initComponents();
        loadCoaches();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Coaches", "Manage coaches and their credentials.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        toolbar.setOpaque(false);
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search coaches...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { filterTable(); }
        });
        toolbar.add(searchField, "width 200!");

        refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadCoaches());
        toolbar.add(refreshBtn, "gapleft " + Spacing.SM);

        addBtn = new ETMSButton("+ Add Coach", ETMSButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> openAddDialog());
        toolbar.add(addBtn, "gapleft " + Spacing.SM + ", align right");

        add(toolbar, "growx, wrap, gapbottom " + Spacing.LG);

        String[] columns = {"ID", "Name", "Specialization", "Certification", "Experience", "Team", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        coachTable = new ETMSTable(tableModel);
        coachTable.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());
        JScrollPane scroll = new JScrollPane(coachTable);
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

    private void loadCoaches() {
        tableModel.setRowCount(0);
        List<Coach> coaches = controller.getAllCoaches();
        for (Coach c : coaches) {
            String fullName = c.getFirstName() + " " + c.getLastName();
            tableModel.addRow(new Object[]{
                c.getCoachId(),
                fullName,
                c.getSpecialization() != null ? c.getSpecialization() : "",
                c.getCertification() != null ? c.getCertification() : "",
                c.getExperienceYears(),
                c.getTeamId() > 0 ? String.valueOf(c.getTeamId()) : "None",
                c.getStatus() != null ? c.getStatus() : "ACTIVE"
            });
        }
    }

    private void filterTable() {
        String search = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (Coach c : controller.getAllCoaches()) {
            String fullName = (c.getFirstName() + " " + c.getLastName()).toLowerCase();
            if (!search.isEmpty() && !fullName.contains(search) &&
                (c.getSpecialization() == null || !c.getSpecialization().toLowerCase().contains(search))) continue;
            tableModel.addRow(new Object[]{
                c.getCoachId(),
                c.getFirstName() + " " + c.getLastName(),
                c.getSpecialization() != null ? c.getSpecialization() : "",
                c.getCertification() != null ? c.getCertification() : "",
                c.getExperienceYears(),
                c.getTeamId() > 0 ? String.valueOf(c.getTeamId()) : "None",
                c.getStatus() != null ? c.getStatus() : "ACTIVE"
            });
        }
    }

    private void openAddDialog() {
        CoachEditDialog dialog = new CoachEditDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), controller, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Coach coach = dialog.getUpdatedCoach();
            boolean success = controller.createCoach(coach);
            if (success) {
                JOptionPane.showMessageDialog(this, "Coach added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadCoaches();
            }
        }
    }

    private void editSelected() {
        int row = coachTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a coach to edit.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        Coach coach = controller.getAllCoaches().stream()
            .filter(c -> c.getCoachId() == id)
            .findFirst()
            .orElse(null);
        if (coach != null) {
            CoachEditDialog dialog = new CoachEditDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), controller, coach);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Coach updated = dialog.getUpdatedCoach();
                boolean success = controller.updateCoach(updated);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Coach updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadCoaches();
                }
            }
        }
    }

    private void deleteSelected() {
        int row = coachTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a coach to delete.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this coach?") == JOptionPane.YES_OPTION) {
            if (controller.deleteCoach(id)) {
                loadCoaches();
                JOptionPane.showMessageDialog(this, "Coach deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Deletion failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (value == null) return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value.toString();
            ETMSStatusBadge badge = ETMSStatusBadge.create(status);
            if (isSelected) {
                badge.setBackground(ThemeManager.getAccentSoft());
            }
            return badge;
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

        if (coachTable != null) coachTable.refreshTheme();
        if (refreshBtn != null) refreshBtn.refreshTheme();
        if (addBtn != null) addBtn.refreshTheme();
        if (editBtn != null) editBtn.refreshTheme();
        if (deleteBtn != null) deleteBtn.refreshTheme();

        revalidate();
        repaint();
    }
}