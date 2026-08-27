package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Sponsor;
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

public class SponsorPanel extends JPanel {

    private final DashboardController controller;
    private ETMSTable sponsorTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ETMSButton addBtn, refreshBtn, deleteBtn, editBtn;

    public SponsorPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][grow][]"));
        initComponents();
        loadSponsors();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Sponsors", "Manage tournament sponsors.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        toolbar.setOpaque(false);
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search sponsors...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { filterTable(); }
        });
        toolbar.add(searchField, "width 200!");

        refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadSponsors());
        toolbar.add(refreshBtn, "gapleft " + Spacing.SM);

        addBtn = new ETMSButton("+ Add Sponsor", ETMSButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> openAddDialog());
        toolbar.add(addBtn, "gapleft " + Spacing.SM + ", align right");

        add(toolbar, "growx, wrap, gapbottom " + Spacing.LG);

        String[] columns = {"ID", "Company", "Category", "Amount", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        sponsorTable = new ETMSTable(tableModel);
        sponsorTable.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
        JScrollPane scroll = new JScrollPane(sponsorTable);
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

    private void loadSponsors() {
        tableModel.setRowCount(0);
        List<Sponsor> sponsors = controller.getAllSponsors();
        for (Sponsor s : sponsors) {
            tableModel.addRow(new Object[]{
                s.getSponsorId(),
                s.getCompanyName() != null ? s.getCompanyName() : "",
                s.getCategory() != null ? s.getCategory() : "",
                String.format("%.2f", s.getSponsorshipAmount()),
                s.getContactEmail() != null ? s.getContactEmail() : "",
                s.getStatus() != null ? s.getStatus() : "ACTIVE"
            });
        }
    }

    private void filterTable() {
        String search = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (Sponsor s : controller.getAllSponsors()) {
            String company = (s.getCompanyName() != null ? s.getCompanyName() : "").toLowerCase();
            if (!search.isEmpty() && !company.contains(search)) continue;
            tableModel.addRow(new Object[]{
                s.getSponsorId(),
                s.getCompanyName() != null ? s.getCompanyName() : "",
                s.getCategory() != null ? s.getCategory() : "",
                String.format("%.2f", s.getSponsorshipAmount()),
                s.getContactEmail() != null ? s.getContactEmail() : "",
                s.getStatus() != null ? s.getStatus() : "ACTIVE"
            });
        }
    }

    private void openAddDialog() {
        SponsorEditDialog dialog = new SponsorEditDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), controller, null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Sponsor sponsor = dialog.getUpdatedSponsor();
            boolean success = controller.createSponsor(sponsor);
            if (success) {
                JOptionPane.showMessageDialog(this, "Sponsor added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSponsors();
            }
        }
    }

    private void editSelected() {
        int row = sponsorTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a sponsor to edit.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        Sponsor sponsor = controller.getAllSponsors().stream()
            .filter(s -> s.getSponsorId() == id)
            .findFirst()
            .orElse(null);
        if (sponsor != null) {
            SponsorEditDialog dialog = new SponsorEditDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this), controller, sponsor);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                Sponsor updated = dialog.getUpdatedSponsor();
                boolean success = controller.updateSponsor(updated);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Sponsor updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadSponsors();
                }
            }
        }
    }

    private void deleteSelected() {
        int row = sponsorTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a sponsor to delete.");
            return;
        }
        int id = (Integer) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this sponsor?") == JOptionPane.YES_OPTION) {
            if (controller.deleteSponsor(id)) {
                loadSponsors();
                JOptionPane.showMessageDialog(this, "Sponsor deleted.");
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

        if (sponsorTable != null) sponsorTable.refreshTheme();
        if (refreshBtn != null) refreshBtn.refreshTheme();
        if (addBtn != null) addBtn.refreshTheme();
        if (editBtn != null) editBtn.refreshTheme();
        if (deleteBtn != null) deleteBtn.refreshTheme();

        revalidate();
        repaint();
    }
}