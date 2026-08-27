package com.etms.ui.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;

import javax.swing.*;
import java.awt.*;

public class ETMSEmptyState extends JPanel {

    public ETMSEmptyState(String message, String actionText, Runnable action) {
        setLayout(new GridBagLayout());
        setBackground(ThemeManager.getSurface());
        setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(20, 20, 20, 20);

        JLabel iconLabel = new JLabel("📂"); // replace with proper icon later
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setForeground(ThemeManager.getTextSecondary());
        add(iconLabel, gbc);

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(Typography.BODY);
        msgLabel.setForeground(ThemeManager.getText());
        add(msgLabel, gbc);

        if (actionText != null && action != null) {
            ETMSButton btn = new ETMSButton(actionText, ETMSButton.Variant.PRIMARY);
            btn.addActionListener(e -> action.run());
            add(btn, gbc);
        }
    }
}