package com.etms.ui.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ETMSStatusBadge extends JLabel {

    public ETMSStatusBadge(String text, Color bgColor, Color fgColor) {
        super(text);
        setFont(Typography.BADGE);
        setForeground(fgColor);
        setBackground(bgColor);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1, true),
                new EmptyBorder(Spacing.XS, Spacing.MD, Spacing.XS, Spacing.MD)
        ));
        setHorizontalAlignment(SwingConstants.CENTER);
    }

    public static ETMSStatusBadge create(String status) {
        Color bg, fg;
        switch (status.toUpperCase()) {
            case "ACTIVE":
            case "COMPLETED":
            case "APPROVED":
            case "ONGOING":
                bg = ThemeManager.getSuccess();
                fg = Color.WHITE;
                break;
            case "SCHEDULED":
            case "UPCOMING":
                bg = ThemeManager.getInfo();
                fg = Color.WHITE;
                break;
            case "PENDING":
            case "DRAFT":
                bg = ThemeManager.getWarning();
                fg = Color.WHITE;
                break;
            case "CANCELLED":
            case "DECLINED":
            case "REJECTED":
                bg = ThemeManager.getDanger();
                fg = Color.WHITE;
                break;
            default:
                // Use border color as fallback (existing method)
                bg = ThemeManager.getBorder();
                fg = ThemeManager.getText();
                break;
        }
        return new ETMSStatusBadge(status, bg, fg);
    }
}