package com.etms.view;

import com.etms.components.AppButton;
import com.etms.controller.DashboardController;
import com.etms.model.Tournament;
import com.etms.model.Team;
import com.etms.theme.ColorPalette;
import com.etms.theme.Typography;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TournamentDetailDialog extends JDialog {

    private final DashboardController controller;
    private final int tournamentId;

    public TournamentDetailDialog(JFrame parent, DashboardController controller, int tournamentId) {
        super(parent, "Tournament Details", true);
        this.controller = controller;
        this.tournamentId = tournamentId;
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setBackground(ColorPalette.LIGHT_BG);
        initComponents();
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow][][grow][]"));

        // Header
        Tournament t = findTournament();
        if (t == null) {
            add(new JLabel("Tournament not found."), "wrap");
            return;
        }

        JLabel name = new JLabel(t.getTournamentName());
        name.setFont(Typography.PAGE_TITLE);
        name.setForeground(ColorPalette.LIGHT_TEXT);
        add(name, "wrap");

        JLabel status = new JLabel(t.getStatus());
        status.setFont(Typography.SECONDARY);
        status.setForeground(ColorPalette.INFO);
        add(status, "wrap");

        JLabel game = new JLabel("Game: " + t.getGameTitle());
        game.setFont(Typography.BODY);
        add(game, "wrap");

        // Stats row
        JPanel statsRow = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow][grow][grow]", "[]"));
        statsRow.setOpaque(false);
        statsRow.add(createStatCard("Teams", String.valueOf(t.getMaxTeams())), "grow");
        statsRow.add(createStatCard("Prize Pool", "$" + String.format("%.2f", t.getPrizePool())), "grow");
        statsRow.add(createStatCard("Start", t.getStartDate()), "grow");
        statsRow.add(createStatCard("End", t.getEndDate() != null ? t.getEndDate() : "TBD"), "grow");
        add(statsRow, "growx, wrap, gapbottom 20");

        List<Team> registeredTeams = controller.getTeamsForTournament(tournamentId);
        JPanel registrationPanel = new JPanel(new MigLayout("fillx, insets 10", "[grow][]", "[][grow]"));
        registrationPanel.setBackground(ColorPalette.LIGHT_SURFACE);
        registrationPanel.setBorder(BorderFactory.createTitledBorder("Approved Registrations ("
                + registeredTeams.size() + "/" + t.getMaxTeams() + ")"));
        JTextArea roster = new JTextArea();
        roster.setEditable(false);
        roster.setRows(5);
        registeredTeams.forEach(team -> roster.append(team.getTeamName() + " [" + team.getTag() + "]\n"));
        if (registeredTeams.isEmpty()) {
            roster.setText("No teams have been registered yet.");
        }
        registrationPanel.add(new JScrollPane(roster), "grow, span 2, wrap");
        JButton registerTeam = new AppButton("Register Team", AppButton.ButtonType.PRIMARY);
        registerTeam.addActionListener(e -> registerTeam());
        registrationPanel.add(registerTeam, "right, span 2");
        add(registrationPanel, "growx, wrap, gapbottom 15");

        // Close button
        JButton closeBtn = new AppButton("Close", AppButton.ButtonType.SECONDARY);
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn, "align right");
    }

    private JPanel createStatCard(String title, String value) {
        JPanel card = new JPanel(new MigLayout("fill, insets 10", "[center]", "[center][center]"));
        card.setBackground(ColorPalette.LIGHT_SURFACE);
        card.setBorder(BorderFactory.createLineBorder(ColorPalette.LIGHT_BORDER));
        JLabel t = new JLabel(title);
        t.setFont(Typography.LABEL);
        t.setForeground(ColorPalette.LIGHT_TEXT_SECONDARY);
        JLabel v = new JLabel(value);
        v.setFont(Typography.CARD_TITLE);
        v.setForeground(ColorPalette.LIGHT_TEXT);
        card.add(t, "wrap, align center");
        card.add(v, "align center");
        return card;
    }

    private Tournament findTournament() {
        return controller.getAllTournaments().stream()
                .filter(t -> t.getTournamentId() == tournamentId)
                .findFirst().orElse(null);
    }

    private void registerTeam() {
        List<Team> availableTeams = controller.getAllTeams().stream()
                .filter(team -> "ACTIVE".equals(team.getStatus()))
                .toList();
        if (availableTeams.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No active teams are available for registration.");
            return;
        }
        JComboBox<Team> teams = new JComboBox<>(availableTeams.toArray(Team[]::new));
        if (JOptionPane.showConfirmDialog(this, teams, "Register Team", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            Team selected = (Team) teams.getSelectedItem();
            if (selected != null && controller.registerTeamForTournament(tournamentId, selected.getTeamId())) {
                dispose();
                new TournamentDetailDialog((JFrame) getOwner(), controller, tournamentId).setVisible(true);
            }
        }
    }
}
