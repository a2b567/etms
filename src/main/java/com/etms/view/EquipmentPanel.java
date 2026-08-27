package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Equipment;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import com.etms.util.IconHelper;
import com.etms.ui.components.ETMSButton;
import com.etms.ui.components.ETMSPageHeader;
import com.etms.ui.components.ETMSTable;
import com.etms.ui.components.ETMSStatusBadge;
import com.etms.ui.components.ETMSMetricCard;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class EquipmentPanel extends JPanel {

    private final DashboardController controller;
    private ETMSTable equipmentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ETMSMetricCard totalMetric, availableMetric, inUseMetric, maintenanceMetric;
    private ETMSButton addBtn, refreshBtn, deleteBtn, editBtn, statusBtn;

    public EquipmentPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][][grow][]"));
        initComponents();
        loadEquipment();
        updateSummary();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Equipment", "Manage tournament equipment inventory.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel summaryPanel = new JPanel(new MigLayout("fillx", "[grow][grow][grow][grow]", "[]"));
        summaryPanel.setOpaque(false);

        totalMetric = new ETMSMetricCard("Total", "0", new Color(41,128,185),
                IconHelper.getEquipmentIcon(new Color(41,128,185)), "All equipment");
        summaryPanel.add(totalMetric, "grow");

        availableMetric = new ETMSMetricCard("Available", "0", new Color(39,174,96),
                IconHelper.getEquipmentIcon(new Color(39,174,96)), "Ready to use");
        summaryPanel.add(availableMetric, "grow");

        inUseMetric = new ETMSMetricCard("In Use", "0", new Color(243,156,18),
                IconHelper.getEquipmentIcon(new Color(243,156,18)), "Currently assigned");
        summaryPanel.add(inUseMetric, "grow");

        maintenanceMetric = new ETMSMetricCard("Maintenance", "0", new Color(231,76,60),
                IconHelper.getEquipmentIcon(new Color(231,76,60)), "Under repair");
        summaryPanel.add(maintenanceMetric, "grow");

        add(summaryPanel, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        toolbar.setOpaque(false);
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search by brand, model, or serial...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { searchEquipment(); }
        });
        toolbar.add(searchField, "width 250!");

        refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> {
            loadEquipment();
            updateSummary();
        });
        toolbar.add(refreshBtn, "gapleft " + Spacing.SM);

        addBtn = new ETMSButton("+ Add Equipment", ETMSButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> openDialog(null));
        toolbar.add(addBtn, "gapleft " + Spacing.SM + ", align right");

        add(toolbar, "growx, wrap, gapbottom " + Spacing.LG);

        String[] cols = {"ID", "Type", "Brand", "Model", "Serial", "Status", "Venue", "Tournament", "Notes"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        equipmentTable = new ETMSTable(tableModel);
        equipmentTable.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
        JScrollPane scrollPane = new JScrollPane(equipmentTable);
        add(scrollPane, "grow, wrap, gapbottom " + Spacing.LG);

        JPanel actionPanel = new JPanel(new MigLayout("fillx, insets 0", "[][][]", "[]"));
        actionPanel.setOpaque(false);
        editBtn = new ETMSButton("Edit", ETMSButton.Variant.SECONDARY);
        editBtn.addActionListener(e -> editSelected());
        actionPanel.add(editBtn);

        deleteBtn = new ETMSButton("Delete", ETMSButton.Variant.DANGER);
        deleteBtn.addActionListener(e -> deleteSelected());
        actionPanel.add(deleteBtn);

        statusBtn = new ETMSButton("Change Status", ETMSButton.Variant.GHOST);
        statusBtn.addActionListener(e -> changeStatus());
        actionPanel.add(statusBtn);

        add(actionPanel, "growx");
    }

    private void loadEquipment() {
        tableModel.setRowCount(0);
        try {
            List<Equipment> list = controller.getAllEquipment();
            for (Equipment e : list) {
                tableModel.addRow(new Object[]{
                    e.getEquipmentId(),
                    e.getType(),
                    e.getBrand(),
                    e.getModel(),
                    e.getSerialNumber(),
                    e.getStatus(),
                    e.getVenueName() != null ? e.getVenueName() : "None",
                    e.getTournamentName() != null ? e.getTournamentName() : "None",
                    e.getNotes()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load equipment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchEquipment() {
        String query = searchField.getText().trim();
        tableModel.setRowCount(0);
        try {
            List<Equipment> list = controller.searchEquipment(query);
            for (Equipment e : list) {
                tableModel.addRow(new Object[]{
                    e.getEquipmentId(),
                    e.getType(),
                    e.getBrand(),
                    e.getModel(),
                    e.getSerialNumber(),
                    e.getStatus(),
                    e.getVenueName() != null ? e.getVenueName() : "None",
                    e.getTournamentName() != null ? e.getTournamentName() : "None",
                    e.getNotes()
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSummary() {
        try {
            Map<String, Integer> counts = controller.getEquipmentStatusCounts();
            int total = counts.values().stream().mapToInt(Integer::intValue).sum();
            totalMetric.setValue(String.valueOf(total));
            availableMetric.setValue(String.valueOf(counts.getOrDefault("Available", 0)));
            inUseMetric.setValue(String.valueOf(counts.getOrDefault("In Use", 0)));
            maintenanceMetric.setValue(String.valueOf(counts.getOrDefault("Maintenance", 0)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openDialog(Equipment existing) {
        EquipmentDialog dialog = new EquipmentDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            controller,
            existing
        );
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            loadEquipment();
            updateSummary();
        }
    }

    private void editSelected() {
        int row = equipmentTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select equipment to edit."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        Equipment eq = controller.getAllEquipment().stream()
            .filter(e -> e.getEquipmentId() == id)
            .findFirst().orElse(null);
        if (eq != null) openDialog(eq);
    }

    private void deleteSelected() {
        int row = equipmentTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select equipment to delete."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this equipment?") == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteEquipment(id);
            if (success) {
                JOptionPane.showMessageDialog(this, "Equipment deleted.");
                loadEquipment();
                updateSummary();
            } else {
                JOptionPane.showMessageDialog(this, "Deletion failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void changeStatus() {
        int row = equipmentTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select equipment to change status."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String currentStatus = (String) tableModel.getValueAt(row, 5);
        String[] statuses = {"Available", "In Use", "Maintenance", "Retired"};
        String newStatus = (String) JOptionPane.showInputDialog(
            this,
            "Select new status:",
            "Change Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statuses,
            currentStatus
        );
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            Equipment eq = controller.getAllEquipment().stream()
                .filter(e -> e.getEquipmentId() == id)
                .findFirst().orElse(null);
            if (eq != null) {
                eq.setStatus(newStatus);
                boolean success = controller.updateEquipment(eq);
                if (success) {
                    loadEquipment();
                    updateSummary();
                    JOptionPane.showMessageDialog(this, "Status updated.");
                } else {
                    JOptionPane.showMessageDialog(this, "Status update failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
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

        if (totalMetric != null) totalMetric.refreshTheme();
        if (availableMetric != null) availableMetric.refreshTheme();
        if (inUseMetric != null) inUseMetric.refreshTheme();
        if (maintenanceMetric != null) maintenanceMetric.refreshTheme();

        if (equipmentTable != null) equipmentTable.refreshTheme();
        if (refreshBtn != null) refreshBtn.refreshTheme();
        if (addBtn != null) addBtn.refreshTheme();
        if (editBtn != null) editBtn.refreshTheme();
        if (deleteBtn != null) deleteBtn.refreshTheme();
        if (statusBtn != null) statusBtn.refreshTheme();

        revalidate();
        repaint();
    }
}