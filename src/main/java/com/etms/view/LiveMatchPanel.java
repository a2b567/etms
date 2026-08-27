package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Match;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.util.MatchTimer;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.time.Duration;

public class LiveMatchPanel extends JPanel {
    private final DashboardController controller;
    private final Match match;
    private MatchTimer matchTimer;

    private JLabel team1Label, team2Label;
    private JLabel score1Label, score2Label;
    private JLabel timerLabel;
    private JButton startPauseBtn, resetBtn, team1ScoreBtn, team2ScoreBtn, declareWinnerBtn;
    private int score1, score2;
    private boolean timerStarted = false;

    public LiveMatchPanel(DashboardController controller, Match match) {
        this.controller = controller;
        this.match = match;
        this.score1 = match.getTeam1Score();
        this.score2 = match.getTeam2Score();
        setBackground(ThemeManager.getBackground());
        initComponents();
        setupTimer();
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 30", "[center]", "[center]"));

        JLabel title = new JLabel("LIVE MATCH CONTROL");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(ThemeManager.getAccent());
        add(title, "span, align center, wrap, gapbottom 30");

        JPanel teamsPanel = new JPanel(new MigLayout("fillx", "[grow][center][grow]", "[center]"));
        teamsPanel.setBackground(ThemeManager.getSurface());
        teamsPanel.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));

        team1Label = new JLabel(match.getTeam1Name() != null ? match.getTeam1Name() : "Team 1");
        team1Label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        team1Label.setForeground(ThemeManager.getText());
        teamsPanel.add(team1Label, "align center");

        JLabel vsLabel = new JLabel("VS");
        vsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        vsLabel.setForeground(ThemeManager.getTextSecondary());
        teamsPanel.add(vsLabel, "align center");

        team2Label = new JLabel(match.getTeam2Name() != null ? match.getTeam2Name() : "Team 2");
        team2Label.setFont(new Font("Segoe UI", Font.BOLD, 22));
        team2Label.setForeground(ThemeManager.getText());
        teamsPanel.add(team2Label, "align center");

        add(teamsPanel, "growx, wrap, gapbottom 20");

        JPanel scoresPanel = new JPanel(new MigLayout("fillx", "[grow][center][grow]", "[center]"));
        scoresPanel.setBackground(ThemeManager.getSurface());
        scoresPanel.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));

        score1Label = new JLabel(String.valueOf(score1));
        score1Label.setFont(new Font("Segoe UI", Font.BOLD, 48));
        score1Label.setForeground(ThemeManager.getAccent());
        scoresPanel.add(score1Label, "align center");

        JLabel dash = new JLabel("-");
        dash.setFont(new Font("Segoe UI", Font.BOLD, 48));
        dash.setForeground(ThemeManager.getTextSecondary());
        scoresPanel.add(dash, "align center");

        score2Label = new JLabel(String.valueOf(score2));
        score2Label.setFont(new Font("Segoe UI", Font.BOLD, 48));
        score2Label.setForeground(ThemeManager.getDanger());
        scoresPanel.add(score2Label, "align center");

        add(scoresPanel, "growx, wrap, gapbottom 30");

        timerLabel = new JLabel("00:00");
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timerLabel.setForeground(ThemeManager.getText());
        add(timerLabel, "align center, wrap, gapbottom 20");

        JPanel controlPanel = new JPanel(new MigLayout("fillx", "[grow][grow][grow][grow]", "[]"));
        controlPanel.setOpaque(false);

        startPauseBtn = new JButton("Start / Pause");
        startPauseBtn.setFont(Typography.BUTTON);
        startPauseBtn.setBackground(ThemeManager.getAccent());
        startPauseBtn.setForeground(Color.WHITE);
        startPauseBtn.addActionListener(e -> toggleTimer());
        controlPanel.add(startPauseBtn, "growx, h 40!");

        resetBtn = new JButton("Reset Timer");
        resetBtn.setFont(Typography.BUTTON);
        resetBtn.setBackground(ThemeManager.getSurface());
        resetBtn.setForeground(ThemeManager.getText());
        resetBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
        resetBtn.addActionListener(e -> matchTimer.reset());
        controlPanel.add(resetBtn, "growx, h 40!");

        team1ScoreBtn = new JButton("Team 1 Score +1");
        team1ScoreBtn.setFont(Typography.BUTTON);
        team1ScoreBtn.setBackground(ThemeManager.getAccent());
        team1ScoreBtn.setForeground(Color.WHITE);
        team1ScoreBtn.addActionListener(e -> { score1++; updateScores(); });
        controlPanel.add(team1ScoreBtn, "growx, h 40!");

        team2ScoreBtn = new JButton("Team 2 Score +1");
        team2ScoreBtn.setFont(Typography.BUTTON);
        team2ScoreBtn.setBackground(ThemeManager.getDanger());
        team2ScoreBtn.setForeground(Color.WHITE);
        team2ScoreBtn.addActionListener(e -> { score2++; updateScores(); });
        controlPanel.add(team2ScoreBtn, "growx, h 40!");

        add(controlPanel, "growx, wrap, gapbottom 30");

        JPanel winnerPanel = new JPanel(new MigLayout("fillx", "[grow][grow]", "[]"));
        winnerPanel.setOpaque(false);

        JButton winner1Btn = new JButton("Declare " + team1Label.getText() + " Winner");
        winner1Btn.setFont(Typography.BUTTON);
        winner1Btn.setBackground(ThemeManager.getSuccess());
        winner1Btn.setForeground(Color.WHITE);
        winner1Btn.addActionListener(e -> endMatch(match.getTeam1Id()));
        winnerPanel.add(winner1Btn, "growx, h 50!");

        JButton winner2Btn = new JButton("Declare " + team2Label.getText() + " Winner");
        winner2Btn.setFont(Typography.BUTTON);
        winner2Btn.setBackground(ThemeManager.getSuccess());
        winner2Btn.setForeground(Color.WHITE);
        winner2Btn.addActionListener(e -> endMatch(match.getTeam2Id()));
        winnerPanel.add(winner2Btn, "growx, h 50!");

        add(winnerPanel, "growx");
    }

    private void setupTimer() {
        matchTimer = new MatchTimer(elapsed -> {
            timerLabel.setText(formatDuration(elapsed));
        });
    }

    private void toggleTimer() {
        if (!timerStarted) {
            matchTimer.start();
            timerStarted = true;
            startPauseBtn.setText("Pause");
        } else {
            if (matchTimer.isRunning()) {
                matchTimer.pause();
                startPauseBtn.setText("Resume");
            } else {
                matchTimer.resume();
                startPauseBtn.setText("Pause");
            }
        }
    }

    private void updateScores() {
        score1Label.setText(String.valueOf(score1));
        score2Label.setText(String.valueOf(score2));
    }

    private String formatDuration(Duration d) {
        long mins = d.toMinutes();
        long secs = d.minusMinutes(mins).getSeconds();
        return String.format("%02d:%02d", mins, secs);
    }

    private void endMatch(int winnerId) {
        matchTimer.pause();
        int choice = JOptionPane.showConfirmDialog(this,
                "Confirm winner?\n" + team1Label.getText() + " " + score1 + " - " + score2 + " " + team2Label.getText(),
                "Confirm Winner", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            boolean ok = controller.processMatchResult(match.getMatchId(), score1, score2, winnerId);
            if (ok) {
                controller.advanceBracket(match.getMatchId());
                controller.notifyAdmins(match.getTeam1Name() + " vs " + match.getTeam2Name() + " result recorded.");
            }
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) window.dispose();
        }
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getBackground());

        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(ThemeManager.getBackground());
                if (((JPanel) comp).getBorder() != null) {
                    ((JPanel) comp).setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
                }
            }
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(ThemeManager.getText());
            }
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String text = btn.getText();
                if (text != null) {
                    if (text.equals("Start / Pause") || text.equals("Pause") || text.equals("Resume")) {
                        btn.setBackground(ThemeManager.getAccent());
                        btn.setForeground(Color.WHITE);
                    } else if (text.equals("Reset Timer")) {
                        btn.setBackground(ThemeManager.getSurface());
                        btn.setForeground(ThemeManager.getText());
                        btn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
                    } else if (text.equals("Team 1 Score +1")) {
                        btn.setBackground(ThemeManager.getAccent());
                        btn.setForeground(Color.WHITE);
                    } else if (text.equals("Team 2 Score +1")) {
                        btn.setBackground(ThemeManager.getDanger());
                        btn.setForeground(Color.WHITE);
                    } else if (text.startsWith("Declare")) {
                        btn.setBackground(ThemeManager.getSuccess());
                        btn.setForeground(Color.WHITE);
                    }
                }
            }
        }

        timerLabel.setForeground(ThemeManager.getText());
        team1Label.setForeground(ThemeManager.getText());
        team2Label.setForeground(ThemeManager.getText());

        revalidate();
        repaint();
    }
}