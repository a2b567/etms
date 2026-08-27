package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Match;
import com.etms.model.Tournament;
import com.etms.service.MatchPredictionService;
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
import java.sql.SQLException;
import java.util.List;

public class MatchPanel extends JPanel {

    private final DashboardController controller;
    private ETMSTable matchTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> tournamentCombo;
    private List<Tournament> tournaments;
    private JLabel predictionLabel;
    private JTextField searchField;
    private ETMSButton scheduleBtn, liveBtn, deleteBtn, bracketBtn;

    public MatchPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][grow][][grow]"));
        initComponents();
        loadTournaments();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Matches", "Schedule matches, record results, and monitor live events.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel filterPanel = new JPanel(new MigLayout("fillx, insets 0", "[][grow][]", "[]"));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Tournament:"));
        tournamentCombo = new JComboBox<>();
        tournamentCombo.setFont(Typography.BODY);
        tournamentCombo.addActionListener(e -> loadMatches());
        filterPanel.add(tournamentCombo, "growx, width 250!");

        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search matches...");
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { filterTable(); }
        });
        filterPanel.add(searchField, "growx, width 200!");

        ETMSButton refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadMatches());
        filterPanel.add(refreshBtn);

        add(filterPanel, "growx, wrap, gapbottom " + Spacing.LG);

        String[] columns = {"ID", "Round", "Match", "Team 1", "Score 1", "Score 2", "Team 2", "Winner", "Scheduled", "Status", "Referee"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        matchTable = new ETMSTable(tableModel);
        matchTable.getColumnModel().getColumn(9).setCellRenderer(new StatusBadgeRenderer());
        JScrollPane scroll = new JScrollPane(matchTable);
        add(scroll, "grow, wrap, gapbottom " + Spacing.LG);

        JPanel actionPanel = new JPanel(new MigLayout("fillx, insets 0", "[][][][][]", "[]"));
        actionPanel.setOpaque(false);

        scheduleBtn = new ETMSButton("Edit / Schedule", ETMSButton.Variant.SECONDARY);
        scheduleBtn.addActionListener(e -> editSelectedMatch());
        actionPanel.add(scheduleBtn);

        ETMSButton recordBtn = new ETMSButton("Record Score", ETMSButton.Variant.SECONDARY);
        recordBtn.addActionListener(e -> editSelectedMatch());
        actionPanel.add(recordBtn);

        liveBtn = new ETMSButton("Start Live Match", ETMSButton.Variant.PRIMARY);
        liveBtn.addActionListener(e -> openLiveMatch());
        actionPanel.add(liveBtn);

        deleteBtn = new ETMSButton("Delete", ETMSButton.Variant.DANGER);
        deleteBtn.addActionListener(e -> deleteSelectedMatch());
        actionPanel.add(deleteBtn);

        bracketBtn = new ETMSButton("View Bracket", ETMSButton.Variant.SECONDARY);
        bracketBtn.addActionListener(e -> viewBracket());
        actionPanel.add(bracketBtn);

        add(actionPanel, "growx, wrap, gapbottom " + Spacing.SM);

        JPanel predictionPanel = new JPanel(new MigLayout("fillx, insets 10", "[grow]", "[]"));
        predictionPanel.setBackground(ThemeManager.getSurface());
        predictionPanel.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
        predictionLabel = new JLabel("Select a match to see prediction.");
        predictionLabel.setFont(Typography.BODY);
        predictionLabel.setForeground(ThemeManager.getTextSecondary());
        predictionPanel.add(predictionLabel, "growx");
        add(predictionPanel, "growx");

        matchTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) showPrediction();
        });
    }

    private void loadTournaments() {
        tournamentCombo.removeAllItems();
        tournaments = controller.getAllTournaments();
        for (Tournament t : tournaments) {
            tournamentCombo.addItem(t.getTournamentName() + " (ID:" + t.getTournamentId() + ")");
        }
        if (!tournaments.isEmpty()) loadMatches();
    }

    private void loadMatches() {
        tableModel.setRowCount(0);
        int idx = tournamentCombo.getSelectedIndex();
        if (idx < 0 || tournaments.isEmpty()) return;
        int tournamentId = tournaments.get(idx).getTournamentId();
        List<Match> matches = controller.getMatchesByTournament(tournamentId);
        for (Match m : matches) {
            tableModel.addRow(new Object[]{
                m.getMatchId(), m.getRoundNumber(), m.getMatchNumber(),
                m.getTeam1Name() != null ? m.getTeam1Name() : "TBD",
                m.getTeam1Score(), m.getTeam2Score(),
                m.getTeam2Name() != null ? m.getTeam2Name() : "TBD",
                m.getWinnerTeamName() != null ? m.getWinnerTeamName() : "-",
                m.getScheduledTime() != null ? m.getScheduledTime() : "Not scheduled",
                m.getStatus(),
                m.getRefereeName() != null ? m.getRefereeName() : "None"
            });
        }
    }

    private void filterTable() {
        String search = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        int idx = tournamentCombo.getSelectedIndex();
        if (idx < 0 || tournaments.isEmpty()) return;
        int tournamentId = tournaments.get(idx).getTournamentId();
        for (Match m : controller.getMatchesByTournament(tournamentId)) {
            String combined = (m.getTeam1Name() + " " + m.getTeam2Name()).toLowerCase();
            if (!search.isEmpty() && !combined.contains(search)) continue;
            tableModel.addRow(new Object[]{
                m.getMatchId(), m.getRoundNumber(), m.getMatchNumber(),
                m.getTeam1Name() != null ? m.getTeam1Name() : "TBD",
                m.getTeam1Score(), m.getTeam2Score(),
                m.getTeam2Name() != null ? m.getTeam2Name() : "TBD",
                m.getWinnerTeamName() != null ? m.getWinnerTeamName() : "-",
                m.getScheduledTime() != null ? m.getScheduledTime() : "Not scheduled",
                m.getStatus(),
                m.getRefereeName() != null ? m.getRefereeName() : "None"
            });
        }
    }

    private void openNewMatchDialog() {
        int idx = tournamentCombo.getSelectedIndex();
        if (idx < 0) { JOptionPane.showMessageDialog(this, "Select a tournament first."); return; }
        int tournamentId = tournaments.get(idx).getTournamentId();
        Match newMatch = new Match();
        newMatch.setTournamentId(tournamentId);
        newMatch.setRoundNumber(1);
        newMatch.setMatchNumber(1);
        MatchScheduleDialog dialog = new MatchScheduleDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), controller, newMatch);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Match m = dialog.getUpdatedMatch();
            if (controller.createMatch(m)) {
                loadMatches();
                JOptionPane.showMessageDialog(this, "Match created.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create match.");
            }
        }
    }

    private void editSelectedMatch() {
        int row = matchTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a match first."); return; }
        int matchId = (Integer) tableModel.getValueAt(row, 0);
        Match match = getMatchById(matchId);
        if (match == null) { JOptionPane.showMessageDialog(this, "Match not found."); return; }
        MatchScheduleDialog dialog = new MatchScheduleDialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), controller, match);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            Match updated = dialog.getUpdatedMatch();
            // Update schedule
            controller.scheduleMatch(updated.getMatchId(), updated.getScheduledTime());
            // If scores or winner were set, record result
            if (updated.getTeam1Score() != 0 || updated.getTeam2Score() != 0 || updated.getWinnerTeamId() != 0) {
                boolean ok = controller.processMatchResult(updated.getMatchId(),
                        updated.getTeam1Score(), updated.getTeam2Score(), updated.getWinnerTeamId());
                if (ok) {
                    controller.advanceBracket(updated.getMatchId());
                    controller.notifyAdmins("Match result recorded: " +
                            updated.getTeam1Name() + " vs " + updated.getTeam2Name());
                }
            }
            // Assign referee
            controller.assignRefereeToMatch(updated.getMatchId(), updated.getRefereeId());
            loadMatches();
        }
    }

    private void deleteSelectedMatch() {
        int row = matchTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a match to delete."); return; }
        int matchId = (Integer) tableModel.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this match?") == JOptionPane.YES_OPTION) {
            try {
                if (new com.etms.dao.MatchDAO().deleteMatch(matchId)) {
                    loadMatches();
                    JOptionPane.showMessageDialog(this, "Match deleted.");
                } else {
                    JOptionPane.showMessageDialog(this, "Deletion failed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error.");
            }
        }
    }

    private void openLiveMatch() {
        int row = matchTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a match first."); return; }
        int matchId = (Integer) tableModel.getValueAt(row, 0);
        Match match = getMatchById(matchId);
        if (match == null) return;
        JDialog liveDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Live Match Control", true);
        liveDialog.setLayout(new BorderLayout());
        liveDialog.add(new LiveMatchPanel(controller, match));
        liveDialog.setSize(900, 700);
        liveDialog.setLocationRelativeTo(this);
        liveDialog.setVisible(true);
        loadMatches();
    }

    private void viewBracket() {
        int idx = tournamentCombo.getSelectedIndex();
        if (idx < 0) { JOptionPane.showMessageDialog(this, "Select a tournament first."); return; }
        int tournamentId = tournaments.get(idx).getTournamentId();
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Bracket", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(new BracketPanel(controller, tournamentId));
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showPrediction() {
        int row = matchTable.getSelectedRow();
        if (row == -1) { predictionLabel.setText("Select a match to see prediction."); return; }
        int matchId = (Integer) tableModel.getValueAt(row, 0);
        MatchPredictionService.Prediction pred = controller.getPrediction(matchId);
        if (pred.getPredictedWinnerId() == 0) {
            predictionLabel.setText("Prediction: " + pred.getExplanation());
        } else {
            String winnerName = getTeamName(pred.getPredictedWinnerId());
            predictionLabel.setText(String.format(
                "Predicted Winner: %s (Confidence: %.0f%%)   [%s]",
                winnerName, pred.getConfidence() * 100, pred.getExplanation()
            ));
        }
    }

    private String getTeamName(int teamId) {
        try {
            return new com.etms.dao.TeamDAO().getTeamById(teamId).getTeamName();
        } catch (Exception e) {
            return "Team " + teamId;
        }
    }

    private Match getMatchById(int matchId) {
        try {
            return new com.etms.dao.MatchDAO().getMatchById(matchId);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
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
}