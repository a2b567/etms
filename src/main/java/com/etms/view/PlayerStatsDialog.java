package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Player;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class PlayerStatsDialog extends JDialog {

    private final DashboardController controller;
    private final Player player;

    public PlayerStatsDialog(JFrame parent, DashboardController controller, Player player) {
        super(parent, "Player Statistics: " + player.getInGameName(), true);
        this.controller = controller;
        this.player = player;
        initComponents();
        pack();
        setSize(700, 600);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));

        JLabel nameLabel = new JLabel(player.getFullName() + " (" + player.getInGameName() + ")");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLabel.setForeground(new Color(25, 118, 210));
        add(nameLabel, "wrap, gapbottom 10");

        JPanel statsPanel = new JPanel(new MigLayout("wrap 2, fillx", "[right][150:200:]", ""));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createTitledBorder("Overall Statistics"));

        Map<String, Integer> agg = controller.getPlayerAggregateStats(player.getPlayerId());
        int kills = agg.getOrDefault("kills", 0);
        int deaths = agg.getOrDefault("deaths", 0);
        int assists = agg.getOrDefault("assists", 0);
        int mvps = agg.getOrDefault("mvps", 0);
        double kda = (deaths == 0) ? kills : (kills + assists) / (double) deaths;

        addStatRow(statsPanel, "Total Matches", String.valueOf(player.getTotalMatches()));
        addStatRow(statsPanel, "Wins", String.valueOf(player.getWins()));
        addStatRow(statsPanel, "Losses", String.valueOf(player.getLosses()));
        addStatRow(statsPanel, "Win Rate", String.format("%.1f%%", player.getWinRate()));
        addStatRow(statsPanel, "Kills", String.valueOf(kills));
        addStatRow(statsPanel, "Deaths", String.valueOf(deaths));
        addStatRow(statsPanel, "Assists", String.valueOf(assists));
        addStatRow(statsPanel, "KDA", String.format("%.2f", kda));
        addStatRow(statsPanel, "MVPs", String.valueOf(mvps + player.getMvpCount()));

        add(statsPanel, "grow, wrap, gapbottom 20");

        // Chart: kills per match
        List<Map<String, Object>> perfList = controller.getPlayerPerformance(player.getPlayerId());
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map<String, Object> row : perfList) {
            int matchId = (Integer) row.get("matchId");
            int k = (Integer) row.get("kills");
            dataset.addValue(k, "Kills", "Match " + matchId);
        }
        JFreeChart chart = ChartFactory.createLineChart(
                "Kills per Match", "Match", "Kills", dataset,
                PlotOrientation.VERTICAL, false, true, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        add(chartPanel, "grow");
    }

    private void addStatRow(JPanel panel, String label, String value) {
        JLabel lblLabel = new JLabel(label + ":");
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lblLabel);
        panel.add(lblValue, "growx");
    }
}