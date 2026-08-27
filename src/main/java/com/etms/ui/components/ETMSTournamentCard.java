package com.etms.ui.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class ETMSTournamentCard extends JPanel {

    private JLabel nameLabel;
    private ETMSStatusBadge statusBadge;
    private JLabel locationLabel;
    private JLabel dateLabel;
    private JLabel participantsLabel;
    private JLabel matchesLabel;
    private ETMSButton actionButton;  // changed to ETMSButton

    public ETMSTournamentCard(String name, String status, String location, String date,
                              String participantsProgress, String matchesProgress, Runnable action) {
        setLayout(new MigLayout("fill, insets " + Spacing.LG, "[grow]", "[]4[]4[]4[]"));
        setBackground(ThemeManager.getSurface());
        setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true));

        JPanel topRow = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        topRow.setOpaque(false);
        nameLabel = new JLabel(name);
        nameLabel.setFont(Typography.BODY_BOLD);
        nameLabel.setForeground(ThemeManager.getText());
        topRow.add(nameLabel, "growx");

        statusBadge = ETMSStatusBadge.create(status);
        topRow.add(statusBadge);
        add(topRow, "growx, wrap");

        locationLabel = new JLabel("📍 " + location);
        locationLabel.setFont(Typography.SECONDARY);
        locationLabel.setForeground(ThemeManager.getTextSecondary());
        add(locationLabel, "wrap");

        dateLabel = new JLabel("📅 " + date);
        dateLabel.setFont(Typography.SECONDARY);
        dateLabel.setForeground(ThemeManager.getTextSecondary());
        add(dateLabel, "wrap");

        JPanel progressRow = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        progressRow.setOpaque(false);
        participantsLabel = new JLabel("👥 " + participantsProgress);
        participantsLabel.setFont(Typography.LABEL);
        participantsLabel.setForeground(ThemeManager.getTextSecondary());
        progressRow.add(participantsLabel, "growx");

        matchesLabel = new JLabel("🏆 " + matchesProgress);
        matchesLabel.setFont(Typography.LABEL);
        matchesLabel.setForeground(ThemeManager.getTextSecondary());
        progressRow.add(matchesLabel);
        add(progressRow, "wrap");

        actionButton = new ETMSButton("View →", ETMSButton.Variant.GHOST);
        actionButton.addActionListener(e -> action.run());
        add(actionButton, "align right");
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getSurface());
        setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true));
        nameLabel.setForeground(ThemeManager.getText());
        locationLabel.setForeground(ThemeManager.getTextSecondary());
        dateLabel.setForeground(ThemeManager.getTextSecondary());
        participantsLabel.setForeground(ThemeManager.getTextSecondary());
        matchesLabel.setForeground(ThemeManager.getTextSecondary());
        actionButton.refreshTheme(); // works now
        statusBadge.repaint();
        revalidate();
        repaint();
    }
}