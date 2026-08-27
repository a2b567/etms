package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.dao.AuditLogDAO;
import com.etms.model.AuditLog;
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
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AuditPanel extends JPanel {

    private final DashboardController controller;
    private ETMSTable auditTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> actionFilter;
    private AuditLogDAO auditLogDAO = new AuditLogDAO();

    public AuditPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][grow]"));
        initComponents();
        loadAuditLogs();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Audit Trail", "Track system activity and changes.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel filterPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow][][][]", "[]"));
        filterPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search by user or action...");
        searchField.setFont(Typography.BODY);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { filterTable(); }
        });
        filterPanel.add(searchField, "width 200!");

        actionFilter = new JComboBox<>(new String[]{"All Actions", "LOGIN", "TOURNAMENT", "TEAM", "MATCH", "USER", "SETTINGS", "OTHER"});
        actionFilter.setFont(Typography.BODY);
        actionFilter.addActionListener(e -> filterTable());
        filterPanel.add(actionFilter, "gapleft " + Spacing.SM);

        ETMSButton refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadAuditLogs());
        filterPanel.add(refreshBtn, "gapleft " + Spacing.SM);

        add(filterPanel, "growx, wrap, gapbottom " + Spacing.LG);

        String[] columns = {"ID", "User", "Action", "Details", "Timestamp"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        auditTable = new ETMSTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(auditTable);
        scrollPane.getViewport().setBackground(ThemeManager.getSurface());
        add(scrollPane, "grow");
    }

    private void loadAuditLogs() {
        tableModel.setRowCount(0);
        try {
            List<AuditLog> logs = auditLogDAO.getAllLogs();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (AuditLog log : logs) {
                String timestamp = log.getCreatedAt() != null ? log.getCreatedAt().format(formatter) : "";
                tableModel.addRow(new Object[]{
                        log.getLogId(),
                        log.getUsername(),
                        log.getAction(),
                        log.getDetails(),
                        timestamp
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading audit logs: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTable() {
        String search = searchField.getText().trim().toLowerCase();
        String action = (String) actionFilter.getSelectedItem();
        tableModel.setRowCount(0);
        try {
            List<AuditLog> logs = auditLogDAO.getAllLogs();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (AuditLog log : logs) {
                if (!search.isEmpty() &&
                        !log.getUsername().toLowerCase().contains(search) &&
                        !log.getAction().toLowerCase().contains(search)) {
                    continue;
                }
                if (!"All Actions".equals(action) && !log.getAction().equalsIgnoreCase(action)) {
                    continue;
                }
                String timestamp = log.getCreatedAt() != null ? log.getCreatedAt().format(formatter) : "";
                tableModel.addRow(new Object[]{
                        log.getLogId(),
                        log.getUsername(),
                        log.getAction(),
                        log.getDetails(),
                        timestamp
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getBackground());
        revalidate();
        repaint();
    }
}