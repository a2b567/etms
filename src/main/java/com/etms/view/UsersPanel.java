package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.User;
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

public class UsersPanel extends JPanel {

    private final DashboardController controller;
    private ETMSTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ETMSButton addBtn, refreshBtn, deleteBtn, editBtn;

    public UsersPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][grow][]"));
        initComponents();
        loadUsers();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Users", "Manage system users and roles.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        toolbar.setOpaque(false);
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search users...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { filterTable(); }
        });
        toolbar.add(searchField, "width 200!");

        refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadUsers());
        toolbar.add(refreshBtn, "gapleft " + Spacing.SM);

        addBtn = new ETMSButton("+ Add User", ETMSButton.Variant.PRIMARY);
        addBtn.addActionListener(e -> openAddDialog());
        toolbar.add(addBtn, "gapleft " + Spacing.SM + ", align right");

        add(toolbar, "growx, wrap, gapbottom " + Spacing.LG);

        String[] columns = {"ID", "Username", "Email", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        userTable = new ETMSTable(tableModel);
        JScrollPane scroll = new JScrollPane(userTable);
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

    private void loadUsers() {
        tableModel.setRowCount(0);
        try {
            List<User> users = new com.etms.dao.UserDAO().getAllUsers();
            for (User u : users) {
                tableModel.addRow(new Object[]{
                    u.getUserId(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getRole(),
                    u.isActive() ? "Active" : "Inactive"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void filterTable() {
        String query = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        try {
            List<User> users = new com.etms.dao.UserDAO().getAllUsers();
            for (User u : users) {
                if (u.getUsername().toLowerCase().contains(query) || u.getEmail().toLowerCase().contains(query)) {
                    tableModel.addRow(new Object[]{
                        u.getUserId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRole(),
                        u.isActive() ? "Active" : "Inactive"
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAddDialog() {
        JOptionPane.showMessageDialog(this, "Add User functionality should be implemented.");
    }

    private void editSelected() {
        int row = userTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user to edit."); return; }
        JOptionPane.showMessageDialog(this, "Edit User functionality should be implemented.");
    }

    private void deleteSelected() {
        int row = userTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a user to delete."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this user?") == JOptionPane.YES_OPTION) {
            try {
                if (new com.etms.dao.UserDAO().deleteUser(id)) {
                    loadUsers();
                    JOptionPane.showMessageDialog(this, "User deleted.");
                } else {
                    JOptionPane.showMessageDialog(this, "Deletion failed.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}