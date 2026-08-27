package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Match;
import com.etms.model.Tournament;
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

public class TournamentPanel extends JPanel {

    private final DashboardController controller;
    private ETMSTable tournamentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private ETMSButton createButton, refreshButton, deleteButton, viewButton, bracketButton, configButton;

    public TournamentPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][grow][][]"));
        initComponents();
        loadTournaments();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Tournaments", "Manage tournaments, registration periods, schedules, and tournament configurations.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[grow][][]", "[]"));
        toolbar.setOpaque(false);
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search tournaments...");
        searchField.setFont(Typography.BODY);
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { filterTable(); }
        });
        toolbar.add(searchField, "width 200!");

        statusFilter = new JComboBox<>(new String[]{"All Statuses", "UPCOMING", "REGISTRATION", "ONGOING", "COMPLETED", "CANCELLED"});
        statusFilter.setFont(Typography.BODY);
        statusFilter.addActionListener(e -> filterTable());
        toolbar.add(statusFilter, "gapleft " + Spacing.SM);

        refreshButton = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshButton.addActionListener(e -> loadTournaments());
        toolbar.add(refreshButton, "gapleft " + Spacing.SM);

        createButton = new ETMSButton("+ Create Tournament", ETMSButton.Variant.PRIMARY);
        createButton.addActionListener(e -> openWizard());
        toolbar.add(createButton, "gapleft " + Spacing.SM + ", align right");

        add(toolbar, "growx, wrap, gapbottom " + Spacing.LG);

        String[] columns = {"ID", "Tournament", "Game", "Status", "Start Date", "End Date", "Teams", "Prize Pool"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tournamentTable = new ETMSTable(tableModel);
        // Set custom renderer for status column (index 3)
        tournamentTable.getColumnModel().getColumn(3).setCellRenderer(new StatusBadgeRenderer());
        JScrollPane scrollPane = new JScrollPane(tournamentTable);
        scrollPane.getViewport().setBackground(ThemeManager.getSurface());
        add(scrollPane, "grow, wrap, gapbottom " + Spacing.LG);

        JPanel actionPanel = new JPanel(new MigLayout("fillx, insets 0", "[][][][][][]", "[]"));
        actionPanel.setOpaque(false);
        viewButton = new ETMSButton("View Details", ETMSButton.Variant.SECONDARY);
        viewButton.addActionListener(e -> viewTournament());
        actionPanel.add(viewButton);

        configButton = new ETMSButton("Configure", ETMSButton.Variant.SECONDARY);
        configButton.addActionListener(e -> configureTournament());
        actionPanel.add(configButton);

        bracketButton = new ETMSButton("View Bracket", ETMSButton.Variant.SECONDARY);
        bracketButton.addActionListener(e -> viewBracket());
        actionPanel.add(bracketButton);

        ETMSButton genBracketBtn = new ETMSButton("Generate Bracket", ETMSButton.Variant.PRIMARY);
        genBracketBtn.addActionListener(e -> generateBracket());
        actionPanel.add(genBracketBtn);

        deleteButton = new ETMSButton("Delete", ETMSButton.Variant.DANGER);
        deleteButton.addActionListener(e -> deleteTournament());
        actionPanel.add(deleteButton);

        add(actionPanel, "growx");
    }

    private void loadTournaments() {
        tableModel.setRowCount(0);
        List<Tournament> tournaments = controller.getAllTournaments();
        for (Tournament t : tournaments) {
            tableModel.addRow(new Object[]{
                t.getTournamentId(),
                t.getTournamentName(),
                t.getGameTitle(),
                t.getStatus(),
                t.getStartDate(),
                t.getEndDate() != null ? t.getEndDate() : "TBD",
                t.getMaxTeams(),
                "$" + String.format("%.2f", t.getPrizePool())
            });
        }
    }

    private void filterTable() {
        String search = searchField.getText().trim().toLowerCase();
        String status = (String) statusFilter.getSelectedItem();
        tableModel.setRowCount(0);
        for (Tournament t : controller.getAllTournaments()) {
            if (!search.isEmpty() && !t.getTournamentName().toLowerCase().contains(search)) continue;
            if (!"All Statuses".equals(status) && !t.getStatus().equals(status)) continue;
            tableModel.addRow(new Object[]{
                t.getTournamentId(),
                t.getTournamentName(),
                t.getGameTitle(),
                t.getStatus(),
                t.getStartDate(),
                t.getEndDate() != null ? t.getEndDate() : "TBD",
                t.getMaxTeams(),
                "$" + String.format("%.2f", t.getPrizePool())
            });
        }
    }

    private void openWizard() {
        TournamentWizard wizard = new TournamentWizard((JFrame) SwingUtilities.getWindowAncestor(this), controller);
        wizard.setVisible(true);
        loadTournaments();
    }

    private void viewTournament() {
        int row = tournamentTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a tournament first."); return; }
        int tournamentId = (Integer) tableModel.getValueAt(row, 0);
        TournamentDetailDialog dialog = new TournamentDetailDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), controller, tournamentId);
        dialog.setVisible(true);
    }

    private void configureTournament() {
        int row = tournamentTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a tournament to configure."); return; }
        int tournamentId = (Integer) tableModel.getValueAt(row, 0);
        Tournament tournament = controller.getAllTournaments().stream()
            .filter(t -> t.getTournamentId() == tournamentId).findFirst().orElse(null);
        if (tournament == null) return;
        TournamentConfigDialog dialog = new TournamentConfigDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), controller, tournament);
        dialog.setVisible(true);
        loadTournaments();
    }

    private void viewBracket() {
        int row = tournamentTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a tournament first."); return; }
        int tournamentId = (Integer) tableModel.getValueAt(row, 0);
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Bracket", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(new BracketPanel(controller, tournamentId));
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void generateBracket() {
        int row = tournamentTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a tournament first."); return; }
        int tournamentId = (Integer) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Generate bracket for this tournament?") == JOptionPane.YES_OPTION) {
            List<Match> matches = controller.generateBracket(tournamentId);
            if (matches.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Failed to generate bracket. Check teams registration.");
            } else {
                JOptionPane.showMessageDialog(this, "Bracket generated with " + matches.size() + " matches.");
            }
        }
    }

    private void deleteTournament() {
        int row = tournamentTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a tournament to delete."); return; }
        int id = (Integer) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this tournament?") == JOptionPane.YES_OPTION) {
            if (controller.deleteTournament(id)) {
                loadTournaments();
                JOptionPane.showMessageDialog(this, "Tournament deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Deletion failed.");
            }
        }
    }

    // Custom renderer for status column
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
}