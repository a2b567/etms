package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Match;
import com.etms.service.UserSession;   // ✅ Added import
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import com.etms.util.IconHelper;
import com.etms.ui.components.ETMSCard;
import com.etms.ui.components.ETMSButton;
import com.etms.ui.components.ETMSStatusBadge;
import com.etms.ui.components.ETMSMetricCard;
import com.etms.ui.components.ETMSMatchCard;
import com.etms.ui.components.ETMSActivityItem;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class DashboardContainer extends JPanel {

    private final DashboardController controller;
    private final Runnable onViewTournament;
    private final Runnable onRegisterTeam;
    private final Runnable onAddPlayer;
    private final Runnable onScheduleMatch;

    // KPI metric components
    private ETMSMetricCard activeTournamentsMetric;
    private ETMSMetricCard teamsMetric;
    private ETMSMetricCard upcomingMatchesMetric;
    private ETMSMetricCard prizePoolMetric;

    // Other dynamic components
    private JPanel activeTournamentPanel;
    private JPanel upcomingMatchesPanel;
    private JPanel recentActivityPanel;
    private JPanel attentionRequiredPanel;

    private List<ETMSMatchCard> matchCards = new ArrayList<>();
    private List<ETMSActivityItem> activityItems = new ArrayList<>();

    public DashboardContainer(DashboardController controller,
                              Runnable onViewTournament,
                              Runnable onRegisterTeam,
                              Runnable onAddPlayer,
                              Runnable onScheduleMatch) {
        this.controller = controller;
        this.onViewTournament = onViewTournament;
        this.onRegisterTeam = onRegisterTeam;
        this.onAddPlayer = onAddPlayer;
        this.onScheduleMatch = onScheduleMatch;

        setLayout(new MigLayout("fill, insets 24", "[grow]", "[][][grow]"));
        applyTheme();

        add(createWelcomeSection(), "growx, wrap, gapbottom 20");
        add(createStatisticsRow(), "growx, wrap, gapbottom 20");
        add(createMainContent(), "grow");
        refreshDashboard();
    }

    // ========== WELCOME SECTION ==========
    private JPanel createWelcomeSection() {
        JPanel panel = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]"));
        panel.setOpaque(false);

        // Greeting
        String username = "Admin";
        if (UserSession.getCurrentUser() != null) {
            username = UserSession.getCurrentUser().getUsername();
        }
        JLabel greeting = new JLabel("Good " + getTimeOfDay() + ", " + username);
        greeting.setFont(Typography.PAGE_TITLE);
        greeting.setForeground(ThemeManager.getText());
        panel.add(greeting, "wrap");

        // Subtitle with summary
        int active = controller.getActiveTournaments();
        int upcoming = controller.getScheduledMatches();
        JLabel subtitle = new JLabel(active + " active tournaments · " + upcoming + " upcoming matches");
        subtitle.setFont(Typography.SECONDARY);
        subtitle.setForeground(ThemeManager.getTextSecondary());
        panel.add(subtitle);

        return panel;
    }

    private String getTimeOfDay() {
        int hour = LocalDateTime.now().getHour();
        if (hour < 12) return "morning";
        else if (hour < 17) return "afternoon";
        else return "evening";
    }

    // ========== KPI STATISTICS ROW ==========
    private JPanel createStatisticsRow() {
        JPanel stats = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow][grow][grow]", "[]"));
        stats.setOpaque(false);

        activeTournamentsMetric = new ETMSMetricCard(
            "Active Tournaments",
            "0",
            ThemeManager.getAccent(),
            IconHelper.getTournamentsIcon(ThemeManager.getAccent()),
            "Currently running"
        );
        stats.add(activeTournamentsMetric, "grow");

        teamsMetric = new ETMSMetricCard(
            "Registered Teams",
            "0",
            ThemeManager.getSuccess(),
            IconHelper.getTeamsIcon(ThemeManager.getSuccess()),
            "Across all tournaments"
        );
        stats.add(teamsMetric, "grow");

        upcomingMatchesMetric = new ETMSMetricCard(
            "Upcoming Matches",
            "0",
            ThemeManager.getWarning(),
            IconHelper.getMatchesIcon(ThemeManager.getWarning()),
            "Next 24 hours"
        );
        stats.add(upcomingMatchesMetric, "grow");

        prizePoolMetric = new ETMSMetricCard(
            "Total Prize Pool",
            "₱0.00",
            ThemeManager.getInfo(),
            IconHelper.getPrizePoolsIcon(ThemeManager.getInfo()),
            "Active tournaments"
        );
        stats.add(prizePoolMetric, "grow");

        return stats;
    }

    // ========== MAIN CONTENT: LEFT & RIGHT COLUMNS ==========
    private JPanel createMainContent() {
        JPanel main = new JPanel(new MigLayout("fillx, insets 0", "[grow][grow]", "[grow]"));
        main.setOpaque(false);

        // Left column: Active Tournament + Attention Required
        JPanel leftColumn = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]10[]"));
        leftColumn.setOpaque(false);
        leftColumn.add(createActiveTournamentCard(), "growx, wrap");
        leftColumn.add(createAttentionRequiredCard(), "growx");

        // Right column: Upcoming Matches + Recent Activity
        JPanel rightColumn = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]10[]"));
        rightColumn.setOpaque(false);
        rightColumn.add(createUpcomingMatchesCard(), "growx, wrap");
        rightColumn.add(createRecentActivityCard(), "growx");

        main.add(leftColumn, "grow");
        main.add(rightColumn, "grow");
        return main;
    }

    // ========== ACTIVE TOURNAMENT CARD ==========
    private JPanel createActiveTournamentCard() {
        ETMSCard card = new ETMSCard(16);
        card.setLayout(new MigLayout("fill, insets 16", "[grow]", "[]10[]"));

        JLabel sectionTitle = new JLabel("ACTIVE TOURNAMENT");
        sectionTitle.setFont(Typography.CARD_TITLE);
        sectionTitle.setForeground(ThemeManager.getTextMuted());
        card.add(sectionTitle, "wrap");

        JLabel tournamentName = new JLabel("VALORANT Championship 2026");
        tournamentName.setFont(Typography.SECTION_TITLE);
        tournamentName.setForeground(ThemeManager.getText());
        card.add(tournamentName, "wrap");

        ETMSStatusBadge statusBadge = ETMSStatusBadge.create("ONGOING");
        card.add(statusBadge, "wrap, gapbottom 8");

        JLabel stats = new JLabel("32 Teams · 128 Players · 48 Matches");
        stats.setFont(Typography.SECONDARY);
        stats.setForeground(ThemeManager.getTextSecondary());
        card.add(stats, "wrap, gapbottom 12");

        JLabel progressLabel = new JLabel("Tournament Progress");
        progressLabel.setFont(Typography.LABEL);
        progressLabel.setForeground(ThemeManager.getTextSecondary());
        card.add(progressLabel, "wrap");

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(82);
        progressBar.setForeground(ThemeManager.getAccent());
        progressBar.setBackground(ThemeManager.getElevated());
        progressBar.setBorderPainted(false);
        card.add(progressBar, "growx, wrap");

        JLabel progressText = new JLabel("82%");
        progressText.setFont(Typography.BODY_BOLD);
        progressText.setForeground(ThemeManager.getText());
        card.add(progressText, "wrap, gapbottom 8");

        JLabel nextStage = new JLabel("Next Stage: Semi-Finals");
        nextStage.setFont(Typography.BODY);
        nextStage.setForeground(ThemeManager.getTextSecondary());
        card.add(nextStage, "wrap");

        JLabel nextMatch = new JLabel("Next Match: Today, 7:00 PM");
        nextMatch.setFont(Typography.BODY);
        nextMatch.setForeground(ThemeManager.getTextSecondary());
        card.add(nextMatch, "wrap");

        ETMSButton viewBtn = new ETMSButton("View Tournament →", ETMSButton.Variant.PRIMARY);
        viewBtn.addActionListener(e -> onViewTournament.run());
        card.add(viewBtn, "align right, gaptop 8");

        activeTournamentPanel = card;
        return card;
    }

    // ========== UPCOMING MATCHES CARD ==========
    private JPanel createUpcomingMatchesCard() {
        ETMSCard card = new ETMSCard(16);
        card.setLayout(new MigLayout("fill, insets 16", "[grow]", "[]10[]"));

        JLabel sectionTitle = new JLabel("UPCOMING MATCHES");
        sectionTitle.setFont(Typography.CARD_TITLE);
        sectionTitle.setForeground(ThemeManager.getTextMuted());
        card.add(sectionTitle, "wrap, gapbottom 5");

        JPanel matchList = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]5[]"));
        matchList.setOpaque(false);
        card.add(matchList, "growx");

        List<Match> matches = controller.getUpcomingMatches(5);
        matchCards.clear();
        if (matches.isEmpty()) {
            JLabel empty = new JLabel("No upcoming matches.");
            empty.setFont(Typography.BODY);
            empty.setForeground(ThemeManager.getTextSecondary());
            matchList.add(empty, "wrap");
        } else {
            for (Match m : matches) {
                String team1 = m.getTeam1Name() != null ? m.getTeam1Name() : "TBD";
                String team2 = m.getTeam2Name() != null ? m.getTeam2Name() : "TBD";
                String time = m.getScheduledTime() != null ? formatTime(m.getScheduledTime()) : "TBD";
                String venue = "TBD";
                String status = m.getStatus() != null ? m.getStatus() : "SCHEDULED";

                ETMSMatchCard matchCard = new ETMSMatchCard(team1, team2, time, venue, status);
                matchList.add(matchCard, "growx, wrap");
                matchCards.add(matchCard);
            }
        }

        upcomingMatchesPanel = card;
        return card;
    }

    private String formatTime(String raw) {
        try {
            String[] parts = raw.split("T");
            String date = parts[0];
            String clock = parts[1].substring(0, 5);
            String[] dateParts = date.split("-");
            String month = java.time.Month.of(Integer.parseInt(dateParts[1])).toString().substring(0, 3);
            return month + " " + dateParts[2] + " • " + clock;
        } catch (Exception e) {
            return raw;
        }
    }

    // ========== RECENT ACTIVITY CARD ==========
    private JPanel createRecentActivityCard() {
        ETMSCard card = new ETMSCard(16);
        card.setLayout(new MigLayout("fill, insets 16", "[grow]", "[]10[]"));

        JLabel sectionTitle = new JLabel("RECENT ACTIVITY");
        sectionTitle.setFont(Typography.CARD_TITLE);
        sectionTitle.setForeground(ThemeManager.getTextMuted());
        card.add(sectionTitle, "wrap, gapbottom 5");

        JPanel activityList = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]8[]"));
        activityList.setOpaque(false);
        card.add(activityList, "growx");

        List<String> activities = controller.getRecentActivity();
        activityItems.clear();
        if (activities.isEmpty()) {
            JLabel empty = new JLabel("No recent activity.");
            empty.setFont(Typography.BODY);
            empty.setForeground(ThemeManager.getTextSecondary());
            activityList.add(empty, "wrap");
        } else {
            for (String act : activities) {
                String icon = getActivityIcon(act);
                String description = act;
                String timestamp = "Just now";
                ETMSActivityItem item = new ETMSActivityItem(icon, description, timestamp);
                activityList.add(item, "growx, wrap");
                activityItems.add(item);
            }
        }

        recentActivityPanel = card;
        return card;
    }

    private String getActivityIcon(String activity) {
        if (activity.contains("created")) return "📌";
        if (activity.contains("completed")) return "✅";
        if (activity.contains("conflict")) return "⚠️";
        if (activity.contains("registered")) return "👥";
        return "•";
    }

    // ========== ATTENTION REQUIRED CARD ==========
    private JPanel createAttentionRequiredCard() {
        ETMSCard card = new ETMSCard(16);
        card.setLayout(new MigLayout("fill, insets 16", "[grow]", "[]10[]"));

        JLabel sectionTitle = new JLabel("ATTENTION REQUIRED");
        sectionTitle.setFont(Typography.CARD_TITLE);
        sectionTitle.setForeground(ThemeManager.getTextMuted());
        card.add(sectionTitle, "wrap, gapbottom 5");

        String[] items = {
            "3 registrations pending approval",
            "2 matches require referee assignment",
            "1 venue schedule conflict detected",
            "1 match result awaiting confirmation"
        };

        for (String item : items) {
            JPanel row = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
            row.setOpaque(false);

            JLabel iconLabel = new JLabel("⚠️");
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            iconLabel.setForeground(ThemeManager.getWarning());
            row.add(iconLabel, "width 20!");

            JLabel lbl = new JLabel(item);
            lbl.setFont(Typography.BODY);
            lbl.setForeground(ThemeManager.getText());
            row.add(lbl, "growx");

            ETMSButton actionBtn = new ETMSButton("Review", ETMSButton.Variant.SECONDARY);
            actionBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Action: " + item));
            row.add(actionBtn);
            card.add(row, "growx, wrap");
        }

        attentionRequiredPanel = card;
        return card;
    }

    // ========== THEME & REFRESH ==========
    private void applyTheme() {
        setBackground(ThemeManager.getBackground());
    }

    public void refreshTheme() {
        applyTheme();
        if (activeTournamentsMetric != null) activeTournamentsMetric.refreshTheme();
        if (teamsMetric != null) teamsMetric.refreshTheme();
        if (upcomingMatchesMetric != null) upcomingMatchesMetric.refreshTheme();
        if (prizePoolMetric != null) prizePoolMetric.refreshTheme();

        if (activeTournamentPanel instanceof ETMSCard) ((ETMSCard) activeTournamentPanel).refreshTheme();
        if (upcomingMatchesPanel instanceof ETMSCard) ((ETMSCard) upcomingMatchesPanel).refreshTheme();
        if (recentActivityPanel instanceof ETMSCard) ((ETMSCard) recentActivityPanel).refreshTheme();
        if (attentionRequiredPanel instanceof ETMSCard) ((ETMSCard) attentionRequiredPanel).refreshTheme();

        for (ETMSMatchCard mc : matchCards) mc.refreshTheme();
        for (ETMSActivityItem ai : activityItems) ai.refreshTheme();

        revalidate();
        repaint();
    }

    public void refreshDashboard() {
        int active = controller.getActiveTournaments();
        int teams = controller.getTotalTeams();
        int upcoming = controller.getScheduledMatches();
        double prize = controller.getTotalRevenue();

        if (activeTournamentsMetric != null) activeTournamentsMetric.setValue(String.valueOf(active));
        if (teamsMetric != null) teamsMetric.setValue(String.valueOf(teams));
        if (upcomingMatchesMetric != null) upcomingMatchesMetric.setValue(String.valueOf(upcoming));
        if (prizePoolMetric != null) prizePoolMetric.setValue("₱" + String.format("%.2f", prize));
    }
}