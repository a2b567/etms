package com.etms.view;

import com.etms.components.AppButton;
import com.etms.controller.DashboardController;
import com.etms.model.Player;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RosterDialog extends JDialog {

    private final DashboardController controller;
    private final int teamId;
    private JTable rosterTable;
    private DefaultTableModel tableModel;

    public RosterDialog(JFrame parent, DashboardController controller, int teamId) {
        super(parent, "Team Roster", true);
        this.controller = controller;
        this.teamId = teamId;
        setSize(700, 500);
        setLocationRelativeTo(parent);
        initComponents();
        loadRoster();
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow][]"));

        add(new JLabel("Roster"), "wrap, align center");

        tableModel = new DefaultTableModel(new String[]{"ID", "IGN", "Name", "Role", "Starter", "Captain", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        rosterTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(rosterTable);
        add(scrollPane, "grow");

        JPanel buttonPanel = new JPanel(new MigLayout("fillx, insets 0", "[][][][][][]", "[]"));
        JButton addBtn = new AppButton("Add Player", AppButton.ButtonType.PRIMARY);
        addBtn.addActionListener(e -> addPlayer());
        buttonPanel.add(addBtn);
        JButton starterBtn = new AppButton("Toggle Starter", AppButton.ButtonType.SECONDARY);
        starterBtn.addActionListener(e -> toggleStarter());
        buttonPanel.add(starterBtn);
        JButton captainBtn = new AppButton("Set Captain", AppButton.ButtonType.SECONDARY);
        captainBtn.addActionListener(e -> setCaptain());
        buttonPanel.add(captainBtn);
        JButton removeBtn = new AppButton("Remove", AppButton.ButtonType.DANGER);
        removeBtn.addActionListener(e -> removePlayer());
        buttonPanel.add(removeBtn);
        JButton refreshBtn = new AppButton("Refresh", AppButton.ButtonType.SECONDARY);
        refreshBtn.addActionListener(e -> loadRoster());
        buttonPanel.add(refreshBtn);
        JButton closeBtn = new AppButton("Close", AppButton.ButtonType.SECONDARY);
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);
        add(buttonPanel, "growx");
    }

    private void loadRoster() {
        tableModel.setRowCount(0);
        List<Player> players = controller.getTeamRoster(teamId);
        for (Player p : players) {
            tableModel.addRow(new Object[]{
                p.getPlayerId(), p.getInGameName(), p.getFullName(),
                p.getGameRole() == null ? "-" : p.getGameRole(),
                p.isStarter() ? "Yes" : "No", p.isCaptain() ? "Yes" : "No",
                p.getStatus()
            });
        }
    }

    private void addPlayer() {
        List<Player> freeAgents = controller.getAllPlayers().stream()
                .filter(player -> player.getTeamId() <= 0 && "ACTIVE".equals(player.getStatus()))
                .toList();
        if (freeAgents.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No active free agents are available.");
            return;
        }
        JComboBox<Player> players = new JComboBox<>(freeAgents.toArray(Player[]::new));
        int result = JOptionPane.showConfirmDialog(this, players, "Add Player to Roster", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Player selected = (Player) players.getSelectedItem();
            if (selected != null && controller.assignPlayerToTeam(selected.getPlayerId(), teamId, false, false)) {
                loadRoster();
            }
        }
    }

    private void toggleStarter() {
        int row = selectedRow();
        if (row < 0) return;
        int playerId = (Integer) tableModel.getValueAt(row, 0);
        boolean starter = "Yes".equals(tableModel.getValueAt(row, 4));
        boolean captain = "Yes".equals(tableModel.getValueAt(row, 5));
        if (controller.assignPlayerToTeam(playerId, teamId, !starter, captain)) {
            loadRoster();
        }
    }

    private void setCaptain() {
        int row = selectedRow();
        if (row < 0) return;
        int playerId = (Integer) tableModel.getValueAt(row, 0);
        if (controller.setCaptain(teamId, playerId)) {
            loadRoster();
        }
    }

    private void removePlayer() {
        int row = selectedRow();
        if (row < 0) return;
        int playerId = (Integer) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Remove this player from the roster?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION
                && controller.removePlayerFromTeam(playerId)) {
            loadRoster();
        }
    }

    private int selectedRow() {
        int row = rosterTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a player first.");
        }
        return row;
    }
}
