package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import com.etms.ui.components.ETMSButton;
import com.etms.ui.components.ETMSPageHeader;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ReportingPanel extends JPanel {

    private final DashboardController controller;
    private JComboBox<String> reportTypeCombo;
    private JComboBox<String> formatCombo;
    private JTextField dateRangeField;
    private ETMSButton generateBtn;
    private JLabel statusLabel;

    public ReportingPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        initComponents();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Reports", "Generate tournament reports.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel formPanel = new JPanel(new MigLayout("wrap 2, fillx", "[right][grow]", "[]10[]"));
        formPanel.setBackground(ThemeManager.getSurface());
        formPanel.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));

        formPanel.add(new JLabel("Report Type:"));
        reportTypeCombo = new JComboBox<>(new String[]{"Tournament Summary", "Match Results", "Team Rankings", "Financial Report"});
        reportTypeCombo.setBackground(ThemeManager.getSurface());
        reportTypeCombo.setForeground(ThemeManager.getText());
        formPanel.add(reportTypeCombo, "growx");

        formPanel.add(new JLabel("Output Format:"));
        formatCombo = new JComboBox<>(new String[]{"PDF", "Excel", "CSV"});
        formatCombo.setBackground(ThemeManager.getSurface());
        formatCombo.setForeground(ThemeManager.getText());
        formPanel.add(formatCombo, "growx");

        formPanel.add(new JLabel("Date Range (optional):"));
        dateRangeField = new JTextField("YYYY-MM-DD to YYYY-MM-DD");
        dateRangeField.setBackground(ThemeManager.getSurface());
        dateRangeField.setForeground(ThemeManager.getText());
        dateRangeField.setCaretColor(ThemeManager.getAccent());
        dateRangeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            BorderFactory.createEmptyBorder(Spacing.SM, Spacing.MD, Spacing.SM, Spacing.MD)
        ));
        formPanel.add(dateRangeField, "growx");

        generateBtn = new ETMSButton("Generate Report", ETMSButton.Variant.PRIMARY);
        generateBtn.addActionListener(e -> generateReport());
        formPanel.add(generateBtn, "span 2, growx, gaptop " + Spacing.SM);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(Typography.LABEL);
        statusLabel.setForeground(ThemeManager.getTextSecondary());
        formPanel.add(statusLabel, "span 2");

        add(formPanel, "grow");
    }

    private void generateReport() {
        statusLabel.setText("Generating...");
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(1500);
                return null;
            }
            @Override
            protected void done() {
                statusLabel.setText("Report generated successfully!");
                JOptionPane.showMessageDialog(ReportingPanel.this, "Report generated: " + reportTypeCombo.getSelectedItem());
            }
        }.execute();
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getBackground());

        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(ThemeManager.getSurface());
                panel.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
            }
            if (comp instanceof JTextField) {
                JTextField tf = (JTextField) comp;
                tf.setBackground(ThemeManager.getSurface());
                tf.setForeground(ThemeManager.getText());
                tf.setCaretColor(ThemeManager.getAccent());
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.getBorder()),
                    BorderFactory.createEmptyBorder(Spacing.SM, Spacing.MD, Spacing.SM, Spacing.MD)
                ));
            }
            if (comp instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) comp;
                cb.setBackground(ThemeManager.getSurface());
                cb.setForeground(ThemeManager.getText());
            }
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(ThemeManager.getText());
            }
        }

        if (generateBtn != null) generateBtn.refreshTheme();
        if (statusLabel != null) statusLabel.setForeground(ThemeManager.getTextSecondary());

        revalidate();
        repaint();
    }
}