package com.etms.ui.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ETMSActivityItem extends JPanel {

    private JLabel iconLabel;
    private JLabel descriptionLabel;
    private JLabel timestampLabel;

    public ETMSActivityItem(String iconText, String description, String timestamp) {
        setLayout(new MigLayout("fillx, insets 0", "[]10[grow][]", "[]"));
        setOpaque(false);

        iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLabel.setForeground(ThemeManager.getTextSecondary());
        add(iconLabel, "width 24!, align center");

        descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(Typography.BODY);
        descriptionLabel.setForeground(ThemeManager.getText());
        add(descriptionLabel, "growx");

        timestampLabel = new JLabel(timestamp);
        timestampLabel.setFont(Typography.CAPTION);
        timestampLabel.setForeground(ThemeManager.getTextMuted());
        add(timestampLabel);
    }

    public void refreshTheme() {
        iconLabel.setForeground(ThemeManager.getTextSecondary());
        descriptionLabel.setForeground(ThemeManager.getText());
        timestampLabel.setForeground(ThemeManager.getTextMuted());
        revalidate();
        repaint();
    }
}