package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Staff;
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

public class StaffPanel extends JPanel {

    private final DashboardController controller;
    private ETMSTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ETMSButton addBtn, refreshBtn, deleteBtn, editBtn;

    public StaffPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][grow][]"));
        initComponents();
        loadStaff();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Staff", "Manage tournament staff members.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        toolbar.setOpaque(false);
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search staff...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { filterTable(); }
        });
        toolbar.add(searchField, "width 200!");

        refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadStaff());
        toolbar.add(refreshBtn, "gapleft " + Spacing.SM);

        addBtn = new ETMSButton("+ Add Staff", ETMSButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> openAddDialog());
        toolbar.add(addBtn, "gapleft " + Spacing.SM + ", align right");

        add(toolbar, "growx, wrap, gapbottom " + Spacing.LG);

        String[] columns = {"ID", "Name", "Role", "Phone", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        staffTable = new ETMSTable(tableModel);
        staffTable.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
        JScrollPane scroll = new JScrollPane(staffTable);
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

    private void loadStaff() {
        tableModel.setRowCount(0);
        List<Staff> staffList = controller.getAllStaff();
        for (Staff s : staffList) {
            String fullName = s.getFirstName() + " " + s.getLastName();
            tableModel.addRow(new Object[]{
                s.getStaffId(),
                fullName,
                s.getStaffRole() != null ? s.getStaffRole() : "",
                s.getPhone() != null ? s.getPhone() : "",
                s.getEmail() != null ? s.getEmail() : "",
                s.getStatus() != null ? s.getStatus() : "ACTIVE"
            });
        }
    }

    private void filterTable() {
        String search = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (Staff s : controller.getAllStaff()) {
            String fullName = (s.getFirstName() + " " + s.getLastName()).toLowerCase();
            if (!search.isEmpty() && !fullName.contains(search) &&
                (s.getStaffRole() == null || !s.getStaffRole().toLowerCase().contains(search))) continue;
            tableModel.addRow(new Object[]{
                s.getStaffId(),
                s.getFirstName() + " " + s.getLastName(),
                s.getStaffRole() != null ? s.getStaffRole() : "",
                s.getPhone() != null ? s.getPhone() : "",
                s.getEmail() != null ? s.getEmail() : "",
                s.getStatus() != null ? s.getStatus() : "ACTIVE"
            });
        }
    }

    private void openAddDialog() {
        StaffEditDialog dialog = new StaffEditDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), controller, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Staff staff = dialog.getUpdatedStaff();
            boolean success = controller.createStaff(staff);
            if (success) {
                JOptionPane.showMessageDialog(this, "Staff added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStaff();
            }
        }
    }

    private void editSelected() {
        int row = staffTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a staff member to edit.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        Staff staff = controller.getAllStaff().stream()
            .filter(s -> s.getStaffId() == id)
            .findFirst()
            .orElse(null);
        if (staff != null) {
            StaffEditDialog dialog = new StaffEditDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), controller, staff);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Staff updated = dialog.getUpdatedStaff();
                boolean success = controller.updateStaff(updated);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Staff updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadStaff();
                }
            }
        }
    }

    private void deleteSelected() {
        int row = staffTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a staff member to delete.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this staff member?") == JOptionPane.YES_OPTION) {
            if (controller.deleteStaff(id)) {
                loadStaff();
                JOptionPane.showMessageDialog(this, "Staff deleted.");
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

        if (staffTable != null) staffTable.refreshTheme();
        if (refreshBtn != null) refreshBtn.refreshTheme();
        if (addBtn != null) addBtn.refreshTheme();
        if (editBtn != null) editBtn.refreshTheme();
        if (deleteBtn != null) deleteBtn.refreshTheme();

        revalidate();
        repaint();
    }
}