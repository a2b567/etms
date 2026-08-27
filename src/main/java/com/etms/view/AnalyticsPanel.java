package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import com.etms.ui.components.ETMSPageHeader;
import net.miginfocom.swing.MigLayout;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AnalyticsPanel extends JPanel {

    private final DashboardController controller;
    private JPanel chartContainer;
    private JScrollPane scrollPane;

    public AnalyticsPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        initComponents();
        loadCharts();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Analytics", "Tournament statistics and insights.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        chartContainer = new JPanel(new GridLayout(1, 2, 20, 20));
        chartContainer.setBackground(ThemeManager.getBackground());

        scrollPane = new JScrollPane(chartContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(ThemeManager.getBackground());
        scrollPane.setBorder(null);

        add(scrollPane, "grow");
    }

    private void loadCharts() {
        chartContainer.removeAll();

        // Bar Chart
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        Map<String, Integer> monthlyCount = controller.getTournamentMonthlyCount();
        if (monthlyCount.isEmpty()) {
            JLabel noDataLabel = new JLabel("No data available yet.");
            noDataLabel.setFont(Typography.BODY);
            noDataLabel.setForeground(ThemeManager.getTextSecondary());
            chartContainer.add(noDataLabel);
        } else {
            for (Map.Entry<String, Integer> entry : monthlyCount.entrySet()) {
                barDataset.addValue(entry.getValue(), "Tournaments", entry.getKey());
            }
            JFreeChart barChart = ChartFactory.createBarChart(
                    "Tournaments per Month",
                    "Month",
                    "Count",
                    barDataset
            );
            styleBarChart(barChart);
            ChartPanel barPanel = new ChartPanel(barChart);
            barPanel.setBackground(ThemeManager.getSurface());
            barPanel.setPreferredSize(new Dimension(450, 350));
            chartContainer.add(barPanel);
        }

        // Pie Chart
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        Map<String, Double> poolDist = controller.getPrizePoolDistribution();
        if (poolDist.isEmpty()) {
            JLabel noDataLabel = new JLabel("No data available yet.");
            noDataLabel.setFont(Typography.BODY);
            noDataLabel.setForeground(ThemeManager.getTextSecondary());
            chartContainer.add(noDataLabel);
        } else {
            for (Map.Entry<String, Double> entry : poolDist.entrySet()) {
                pieDataset.setValue(entry.getKey(), entry.getValue());
            }
            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Prize Pool Distribution",
                    pieDataset,
                    true,
                    true,
                    false
            );
            stylePieChart(pieChart);
            ChartPanel piePanel = new ChartPanel(pieChart);
            piePanel.setBackground(ThemeManager.getSurface());
            piePanel.setPreferredSize(new Dimension(450, 350));
            chartContainer.add(piePanel);
        }

        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private void styleBarChart(JFreeChart chart) {
        chart.setBackgroundPaint(ThemeManager.getSurface());
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(ThemeManager.getSurface());
        plot.setRangeGridlinePaint(ThemeManager.getBorder());
        plot.setOutlinePaint(ThemeManager.getBorder());
        plot.getRenderer().setSeriesPaint(0, ThemeManager.getAccent());
        chart.getTitle().setPaint(ThemeManager.getText());
        chart.getLegend().setItemPaint(ThemeManager.getText());
        plot.getDomainAxis().setLabelPaint(ThemeManager.getTextSecondary());
        plot.getRangeAxis().setLabelPaint(ThemeManager.getTextSecondary());
        plot.getDomainAxis().setTickLabelPaint(ThemeManager.getTextSecondary());
        plot.getRangeAxis().setTickLabelPaint(ThemeManager.getTextSecondary());
    }

    private void stylePieChart(JFreeChart chart) {
        chart.setBackgroundPaint(ThemeManager.getSurface());
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(ThemeManager.getSurface());
        plot.setOutlinePaint(ThemeManager.getBorder());
        plot.setLabelPaint(ThemeManager.getText());
        chart.getTitle().setPaint(ThemeManager.getText());
        chart.getLegend().setItemPaint(ThemeManager.getText());
        plot.setSectionPaint(0, ThemeManager.getAccent());
        plot.setSectionPaint(1, ThemeManager.getSuccess());
        plot.setSectionPaint(2, ThemeManager.getWarning());
        plot.setSectionPaint(3, ThemeManager.getInfo());
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getBackground());
        chartContainer.setBackground(ThemeManager.getBackground());
        scrollPane.getViewport().setBackground(ThemeManager.getBackground());
        loadCharts();
    }
}