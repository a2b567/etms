package com.etms.ui.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ETMSButton extends JButton {

    public enum Variant {
        PRIMARY,
        SECONDARY,
        DANGER,
        GHOST
    }

    private Variant variant;

    public ETMSButton(String text) {
        this(text, Variant.PRIMARY);
    }

    public ETMSButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        setFont(Typography.BUTTON);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(Spacing.SM, Spacing.LG, Spacing.SM, Spacing.LG));
        applyStyle();
        addMouseListener(new HoverAdapter());
    }

    private void applyStyle() {
        Color bg, fg, borderColor;
        switch (variant) {
            case PRIMARY:
                bg = ThemeManager.getAccent();
                fg = Color.WHITE;
                borderColor = bg;
                break;
            case SECONDARY:
                bg = ThemeManager.getSurface();
                fg = ThemeManager.getText();
                borderColor = ThemeManager.getBorder();
                break;
            case DANGER:
                bg = ThemeManager.getDanger();
                fg = Color.WHITE;
                borderColor = bg;
                break;
            case GHOST:
            default:
                bg = new Color(0, 0, 0, 0);
                fg = ThemeManager.getText();
                borderColor = ThemeManager.getBorder();
                break;
        }
        setBackground(bg);
        setForeground(fg);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(Spacing.SM, Spacing.LG, Spacing.SM, Spacing.LG)
        ));
        setOpaque(variant != Variant.GHOST);
    }

    private class HoverAdapter extends MouseAdapter {
        @Override
        public void mouseEntered(MouseEvent e) {
            if (variant == Variant.PRIMARY) {
                setBackground(ThemeManager.getAccent().darker());
            } else if (variant == Variant.DANGER) {
                setBackground(ThemeManager.getDanger().darker());
            } else if (variant == Variant.SECONDARY || variant == Variant.GHOST) {
                setBackground(ThemeManager.getAccentSoft());
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            applyStyle();
        }
    }

    public void refreshTheme() {
        applyStyle();
    }
}