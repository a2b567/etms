package com.etms.ui.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ETMSMetricCard extends JPanel {

    private JLabel iconLabel;
    private JLabel titleLabel;
    private JLabel valueLabel;
    private JLabel subtitleLabel;

    public ETMSMetricCard(String title, String value, Color valueColor, Icon icon, String subtitle) {
        setLayout(new MigLayout("fill, insets " + Spacing.LG, "[center]", "[]4[]4[]"));
        setBackground(ThemeManager.getSurface());
        setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true));

        if (icon != null) {
            iconLabel = new JLabel(icon);
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(iconLabel, "wrap, align center");
        }

        titleLabel = new JLabel(title);
        titleLabel.setFont(Typography.LABEL);
        titleLabel.setForeground(ThemeManager.getTextSecondary());
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, "wrap, align center");

        valueLabel = new JLabel(value);
        valueLabel.setFont(Typography.METRIC);
        valueLabel.setForeground(valueColor != null ? valueColor : ThemeManager.getText());
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(valueLabel, "wrap, align center");

        if (subtitle != null) {
            subtitleLabel = new JLabel(subtitle);
            subtitleLabel.setFont(Typography.CAPTION);
            subtitleLabel.setForeground(ThemeManager.getTextMuted());
            subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(subtitleLabel, "align center");
        }
    }

    public void setValue(String value) {
        valueLabel.setText(value);
    }

    public void setSubtitle(String subtitle) {
        if (subtitleLabel != null) {
            subtitleLabel.setText(subtitle);
        }
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getSurface());
        setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true));
        titleLabel.setForeground(ThemeManager.getTextSecondary());
        valueLabel.setForeground(valueLabel.getForeground()); // Keep its own color
        if (subtitleLabel != null) {
            subtitleLabel.setForeground(ThemeManager.getTextMuted());
        }
        revalidate();
        repaint();
    }
}