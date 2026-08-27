package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Venue;
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

public class VenuePanel extends JPanel {

    private final DashboardController controller;
    private ETMSTable venueTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ETMSButton addBtn, refreshBtn, deleteBtn, editBtn;

    public VenuePanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][grow][]"));
        initComponents();
        loadVenues();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Venues", "Manage tournament venues.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        toolbar.setOpaque(false);
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search venues...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { filterTable(); }
        });
        toolbar.add(searchField, "width 200!");

        refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadVenues());
        toolbar.add(refreshBtn, "gapleft " + Spacing.SM);

        addBtn = new ETMSButton("+ Add Venue", ETMSButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> openAddDialog());
        toolbar.add(addBtn, "gapleft " + Spacing.SM + ", align right");

        add(toolbar, "growx, wrap, gapbottom " + Spacing.LG);

        String[] columns = {"ID", "Name", "Location", "Capacity", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        venueTable = new ETMSTable(tableModel);
        venueTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        JScrollPane scroll = new JScrollPane(venueTable);
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

    private void loadVenues() {
        tableModel.setRowCount(0);
        List<Venue> venues = controller.getAllVenues();
        for (Venue v : venues) {
            tableModel.addRow(new Object[]{
                v.getVenueId(),
                v.getName(),
                v.getLocation(),
                v.getCapacity(),
                v.getStatus()
            });
        }
    }

    private void filterTable() {
        String search = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (Venue v : controller.getAllVenues()) {
            if (!search.isEmpty() && !v.getName().toLowerCase().contains(search)) continue;
            tableModel.addRow(new Object[]{
                v.getVenueId(),
                v.getName(),
                v.getLocation(),
                v.getCapacity(),
                v.getStatus()
            });
        }
    }

    private void openAddDialog() {
        VenueEditDialog dialog = new VenueEditDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Venue venue = dialog.getUpdatedVenue();
            boolean success = controller.addVenue(venue);
            if (success) {
                JOptionPane.showMessageDialog(this, "Venue added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadVenues();
            } else {
                loadVenues();
            }
        }
    }

    private void editSelected() {
        int row = venueTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a venue to edit.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        Venue venue = controller.getAllVenues().stream()
            .filter(v -> v.getVenueId() == id)
            .findFirst()
            .orElse(null);
        if (venue != null) {
            VenueEditDialog dialog = new VenueEditDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), venue);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Venue updated = dialog.getUpdatedVenue();
                boolean success = controller.updateVenue(updated);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Venue updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadVenues();
                } else {
                    loadVenues();
                }
            }
        }
    }

    private void deleteSelected() {
        int row = venueTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a venue to delete.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this venue?") == JOptionPane.YES_OPTION) {
            if (controller.deleteVenue(id)) {
                loadVenues();
                JOptionPane.showMessageDialog(this, "Venue deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Deletion failed.");
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

        if (venueTable != null) venueTable.refreshTheme();
        if (refreshBtn != null) refreshBtn.refreshTheme();
        if (addBtn != null) addBtn.refreshTheme();
        if (editBtn != null) editBtn.refreshTheme();
        if (deleteBtn != null) deleteBtn.refreshTheme();

        revalidate();
        repaint();
    }
}