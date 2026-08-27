package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Team;
import com.etms.model.Tournament;
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

public class StandingsPanel extends JPanel {

    private final DashboardController controller;
    private JComboBox<String> tournamentCombo;
    private List<Tournament> tournaments;
    private ETMSTable standingsTable;
    private DefaultTableModel tableModel;
    private ETMSButton refreshBtn; // declared

    public StandingsPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        initComponents();
        loadTournaments();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Standings", "Tournament standings and rankings.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel toolbar = new JPanel(new MigLayout("fillx, insets 0", "[][grow]", "[]"));
        toolbar.setOpaque(false);
        toolbar.add(new JLabel("Tournament:"));
        tournamentCombo = new JComboBox<>();
        tournamentCombo.setFont(Typography.BODY);
        tournamentCombo.setBackground(ThemeManager.getSurface());
        tournamentCombo.setForeground(ThemeManager.getText());
        tournamentCombo.addActionListener(e -> loadStandings());
        toolbar.add(tournamentCombo, "growx, width 250!");

        refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadStandings());
        toolbar.add(refreshBtn);

        add(toolbar, "growx, wrap, gapbottom " + Spacing.LG);

        String[] columns = {"Rank", "Team", "Matches", "Wins", "Losses", "Points", "Rating"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        standingsTable = new ETMSTable(tableModel);
        JScrollPane scroll = new JScrollPane(standingsTable);
        add(scroll, "grow");
    }

    private void loadTournaments() {
        tournamentCombo.removeAllItems();
        tournaments = controller.getAllTournaments();
        for (Tournament t : tournaments) {
            tournamentCombo.addItem(t.getTournamentName() + " (ID:" + t.getTournamentId() + ")");
        }
        if (!tournaments.isEmpty()) loadStandings();
    }

    private void loadStandings() {
        tableModel.setRowCount(0);
        int idx = tournamentCombo.getSelectedIndex();
        if (idx < 0 || tournaments.isEmpty()) return;
        int tournamentId = tournaments.get(idx).getTournamentId();
        List<Team> teams = controller.getTeamsForTournament(tournamentId);
        if (teams.isEmpty()) {
            tableModel.addRow(new Object[]{"-", "No teams registered", "-", "-", "-", "-", "-"});
        } else {
            int rank = 1;
            for (Team t : teams) {
                tableModel.addRow(new Object[]{
                    rank++,
                    t.getTeamName(),
                    t.getTotalWins() + t.getTotalLosses(),
                    t.getTotalWins(),
                    t.getTotalLosses(),
                    t.getTournamentPoints(),
                    t.getEloRating()
                });
            }
        }
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getBackground());

        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                ((JPanel) comp).setBackground(ThemeManager.getBackground());
            }
            if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                scroll.setBackground(ThemeManager.getBackground());
                scroll.getViewport().setBackground(ThemeManager.getSurface());
            }
            if (comp instanceof JComboBox) {
                ((JComboBox<?>) comp).setBackground(ThemeManager.getSurface());
                ((JComboBox<?>) comp).setForeground(ThemeManager.getText());
            }
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(ThemeManager.getText());
            }
        }

        if (standingsTable != null) standingsTable.refreshTheme();
        if (refreshBtn != null) refreshBtn.refreshTheme();

        revalidate();
        repaint();
    }
}