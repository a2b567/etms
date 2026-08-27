package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Match;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.ui.components.ETMSCard;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class BracketPanel extends JPanel {

    private final DashboardController controller;
    private final int tournamentId;
    private List<Match> matches;
    private int maxRound;
    private Map<Integer, List<Match>> roundMatches;
    private Map<Integer, Map<Integer, JComponent>> matchComponents;
    private int cardWidth = 220;   // increased
    private int cardHeight = 90;   // increased
    private int verticalGap = 15;

    public BracketPanel(DashboardController controller, int tournamentId) {
        this.controller = controller;
        this.tournamentId = tournamentId;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[grow]"));
        loadBracket();
    }

    private void loadBracket() {
        removeAll();
        matches = controller.getMatchesByTournament(tournamentId);
        if (matches.isEmpty()) {
            add(new JLabel("No matches found for this tournament."));
            return;
        }

        roundMatches = new LinkedHashMap<>();
        for (Match m : matches) {
            int round = m.getRoundNumber();
            roundMatches.computeIfAbsent(round, k -> new ArrayList<>()).add(m);
        }
        maxRound = roundMatches.keySet().stream().max(Integer::compareTo).orElse(0);

        if (maxRound == 0) {
            add(new JLabel("No rounds found."));
            return;
        }

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridy = 0;

        matchComponents = new LinkedHashMap<>();

        for (int round = 1; round <= maxRound; round++) {
            gbc.gridx = round - 1;
            List<Match> roundMatchList = roundMatches.getOrDefault(round, new ArrayList<>());
            JPanel roundPanel = createRoundPanel(round, roundMatchList);
            roundPanel.setBackground(ThemeManager.getSurface());
            roundPanel.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(ThemeManager.getBorder()),
                    "Round " + round,
                    TitledBorder.CENTER,
                    TitledBorder.TOP,
                    Typography.CARD_TITLE,
                    ThemeManager.getText()
            ));
            add(roundPanel, gbc);
        }

        BracketLinePanel linePanel = new BracketLinePanel();
        add(linePanel, new GridBagConstraints(0, 0, maxRound, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));

        revalidate();
        repaint();
    }

    private JPanel createRoundPanel(int round, List<Match> roundMatchList) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ThemeManager.getSurface());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        roundMatchList.sort(Comparator.comparingInt(Match::getMatchNumber));
        Map<Integer, JComponent> compMap = new LinkedHashMap<>();

        for (Match m : roundMatchList) {
            // Create the card with increased size
            JPanel matchCard = new ETMSCard(8);
            matchCard.setLayout(new MigLayout("fill, insets 4", "[grow]", "[]"));
            matchCard.setPreferredSize(new Dimension(cardWidth, cardHeight));
            matchCard.setMaximumSize(new Dimension(cardWidth, cardHeight));
            matchCard.setMinimumSize(new Dimension(cardWidth, cardHeight));

            // Team names – bold, larger
            JLabel teamLabel = new JLabel(
                    (m.getTeam1Name() != null ? m.getTeam1Name() : "TBD") +
                            " vs " +
                            (m.getTeam2Name() != null ? m.getTeam2Name() : "TBD")
            );
            teamLabel.setFont(Typography.BODY_BOLD);
            teamLabel.setForeground(ThemeManager.getText());
            matchCard.add(teamLabel, "wrap, growx");

            // Status – smaller font to fit
            JLabel statusLabel = new JLabel("Status: " + m.getStatus());
            statusLabel.setFont(Typography.LABEL);
            statusLabel.setForeground(ThemeManager.getTextSecondary());
            matchCard.add(statusLabel, "wrap, growx");

            // Winner (if available)
            if (m.getWinnerTeamName() != null) {
                JLabel winnerLabel = new JLabel("Winner: " + m.getWinnerTeamName());
                winnerLabel.setFont(Typography.LABEL);
                winnerLabel.setForeground(ThemeManager.getSuccess());
                matchCard.add(winnerLabel, "wrap, growx");
            }

            panel.add(matchCard);
            panel.add(Box.createRigidArea(new Dimension(0, verticalGap)));

            compMap.put(m.getMatchNumber(), matchCard);
        }

        matchComponents.put(round, compMap);
        return panel;
    }

    private class BracketLinePanel extends JPanel {
        public BracketLinePanel() {
            setOpaque(false);
            setLayout(new BorderLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(ThemeManager.getAccent());
            g2d.setStroke(new BasicStroke(2));

            for (int round = 1; round < maxRound; round++) {
                Map<Integer, JComponent> currentRoundComps = matchComponents.get(round);
                Map<Integer, JComponent> nextRoundComps = matchComponents.get(round + 1);
                if (currentRoundComps == null || nextRoundComps == null) continue;

                for (Map.Entry<Integer, JComponent> entry : currentRoundComps.entrySet()) {
                    int matchNumber = entry.getKey();
                    int parentMatchNumber = (matchNumber + 1) / 2;
                    JComponent nextComp = nextRoundComps.get(parentMatchNumber);
                    if (nextComp == null) continue;

                    Point from = SwingUtilities.convertPoint(entry.getValue(), 0, entry.getValue().getHeight() / 2, this);
                    Point to = SwingUtilities.convertPoint(nextComp, 0, nextComp.getHeight() / 2, this);
                    g2d.drawLine(from.x, from.y, to.x, to.y);
                }
            }
            g2d.dispose();
        }
    }
}