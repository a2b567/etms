package com.etms.ui.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ETMSMatchCard extends JPanel {

    private JLabel team1Label;
    private JLabel vsLabel;
    private JLabel team2Label;
    private JLabel timeLabel;
    private JLabel venueLabel;
    private ETMSStatusBadge statusBadge;

    public ETMSMatchCard(String team1, String team2, String time, String venue, String status) {
        setLayout(new MigLayout("fill, insets " + Spacing.MD, "[grow][][]", "[]4[]"));
        setBackground(ThemeManager.getSurface());
        setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true));

        // Teams
        team1Label = new JLabel(team1);
        team1Label.setFont(Typography.BODY_BOLD);
        team1Label.setForeground(ThemeManager.getText());
        add(team1Label, "align center");

        vsLabel = new JLabel("VS");
        vsLabel.setFont(Typography.LABEL);
        vsLabel.setForeground(ThemeManager.getTextMuted());
        add(vsLabel, "align center, gapleft 10, gapright 10");

        team2Label = new JLabel(team2);
        team2Label.setFont(Typography.BODY_BOLD);
        team2Label.setForeground(ThemeManager.getText());
        add(team2Label, "align center, wrap");

        // Time and venue
        timeLabel = new JLabel("🕐 " + time);
        timeLabel.setFont(Typography.SECONDARY);
        timeLabel.setForeground(ThemeManager.getTextSecondary());
        add(timeLabel, "span 3, align left, wrap");

        venueLabel = new JLabel("📍 " + venue);
        venueLabel.setFont(Typography.SECONDARY);
        venueLabel.setForeground(ThemeManager.getTextSecondary());
        add(venueLabel, "span 3, align left, wrap");

        // Status badge
        statusBadge = ETMSStatusBadge.create(status);
        add(statusBadge, "span 3, align right");
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getSurface());
        setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true));
        team1Label.setForeground(ThemeManager.getText());
        team2Label.setForeground(ThemeManager.getText());
        timeLabel.setForeground(ThemeManager.getTextSecondary());
        venueLabel.setForeground(ThemeManager.getTextSecondary());
        statusBadge.repaint();
        revalidate();
        repaint();
    }
}