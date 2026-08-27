package com.etms.ui.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ETMSPageHeader extends JPanel {

    private JLabel titleLabel;
    private JLabel subtitleLabel;

    public ETMSPageHeader(String title, String subtitle) {
        setLayout(new MigLayout("fillx, insets 0", "[grow]", "[]"));
        setOpaque(false);

        titleLabel = new JLabel(title);
        titleLabel.setFont(Typography.PAGE_TITLE);
        titleLabel.setForeground(ThemeManager.getText());
        add(titleLabel, "wrap, gapbottom " + Spacing.XS);

        if (subtitle != null && !subtitle.isEmpty()) {
            subtitleLabel = new JLabel(subtitle);
            subtitleLabel.setFont(Typography.SECONDARY);
            subtitleLabel.setForeground(ThemeManager.getTextSecondary());
            add(subtitleLabel);
        }
        applyTheme();
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setSubtitle(String subtitle) {
        if (subtitleLabel != null) {
            subtitleLabel.setText(subtitle);
        }
    }

    public void applyTheme() {
        titleLabel.setForeground(ThemeManager.getText());
        if (subtitleLabel != null) {
            subtitleLabel.setForeground(ThemeManager.getTextSecondary());
        }
    }
}