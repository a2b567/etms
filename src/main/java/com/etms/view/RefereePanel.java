package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Referee;
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

public class RefereePanel extends JPanel {

    private final DashboardController controller;
    private ETMSTable refereeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ETMSButton addBtn, refreshBtn, deleteBtn, editBtn;

    public RefereePanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][grow][]"));
        initComponents();
        loadReferees();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Referees", "Manage referees and their assignments.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        toolbar.setOpaque(false);
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search referees...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { filterTable(); }
        });
        toolbar.add(searchField, "width 200!");

        refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadReferees());
        toolbar.add(refreshBtn, "gapleft " + Spacing.SM);

        addBtn = new ETMSButton("+ Add Referee", ETMSButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> openDialog(null));
        toolbar.add(addBtn, "gapleft " + Spacing.SM + ", align right");

        add(toolbar, "growx, wrap, gapbottom " + Spacing.LG);

        String[] columns = {"ID", "Name", "Email", "Phone", "Qualification", "Experience", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        refereeTable = new ETMSTable(tableModel);
        refereeTable.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());
        JScrollPane scroll = new JScrollPane(refereeTable);
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

    private void loadReferees() {
        tableModel.setRowCount(0);
        List<Referee> referees = controller.getAllReferees();
        for (Referee r : referees) {
            tableModel.addRow(new Object[]{
                r.getRefereeId(),
                r.getFirstName() + " " + r.getLastName(),
                r.getEmail(),
                r.getPhone(),
                r.getQualification(),
                r.getYearsExperience(),
                r.getStatus()
            });
        }
    }

    private void filterTable() {
        String query = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (Referee r : controller.getAllReferees()) {
            String fullName = (r.getFirstName() + " " + r.getLastName()).toLowerCase();
            if (fullName.contains(query) || r.getEmail().toLowerCase().contains(query)) {
                tableModel.addRow(new Object[]{
                    r.getRefereeId(),
                    r.getFirstName() + " " + r.getLastName(),
                    r.getEmail(),
                    r.getPhone(),
                    r.getQualification(),
                    r.getYearsExperience(),
                    r.getStatus()
                });
            }
        }
    }

    private void openDialog(Referee existing) {
        RefereeDialog dialog = new RefereeDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            controller,
            existing
        );
        dialog.setVisible(true);
        loadReferees();
    }

    private void editSelected() {
        int row = refereeTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a referee to edit."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        Referee r = controller.getAllReferees().stream()
            .filter(ref -> ref.getRefereeId() == id)
            .findFirst().orElse(null);
        if (r != null) openDialog(r);
    }

    private void deleteSelected() {
        int row = refereeTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a referee to delete."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this referee?") == JOptionPane.YES_OPTION) {
            boolean success = controller.deleteReferee(id);
            if (success) {
                JOptionPane.showMessageDialog(this, "Referee deleted.");
                loadReferees();
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

        if (refereeTable != null) refereeTable.refreshTheme();
        if (refreshBtn != null) refreshBtn.refreshTheme();
        if (addBtn != null) addBtn.refreshTheme();
        if (editBtn != null) editBtn.refreshTheme();
        if (deleteBtn != null) deleteBtn.refreshTheme();

        revalidate();
        repaint();
    }
}