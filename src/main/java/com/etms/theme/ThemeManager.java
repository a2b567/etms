package com.etms.theme;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.Color;

public final class ThemeManager {
    public enum Theme { LIGHT, DARK }

    private static Theme currentTheme = Theme.LIGHT;

    private ThemeManager() {}

    public static Theme getCurrentTheme() { return currentTheme; }

    public static void applyLightTheme() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            currentTheme = Theme.LIGHT;
            customize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applyDarkTheme() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            currentTheme = Theme.DARK;
            customize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void toggleTheme() {
        if (currentTheme == Theme.LIGHT) {
            applyDarkTheme();
        } else {
            applyLightTheme();
        }
    }

    private static void customize() {
        UIManager.put("Component.arc", UIConstants.BORDER_RADIUS_MD);
        UIManager.put("Button.arc", UIConstants.BORDER_RADIUS_BUTTON);
        UIManager.put("TextComponent.arc", UIConstants.BORDER_RADIUS_SM);
        UIManager.put("Table.alternateRowColor", true);
    }

    // ========== Color Getters ==========

    public static Color getBackground() {
        return currentTheme == Theme.LIGHT ? ColorPalette.LIGHT_BG : ColorPalette.DARK_BG;
    }

    public static Color getSurface() {
        return currentTheme == Theme.LIGHT ? ColorPalette.LIGHT_SURFACE : ColorPalette.DARK_SURFACE;
    }

    public static Color getElevated() {
        return currentTheme == Theme.LIGHT ? ColorPalette.LIGHT_SURFACE : ColorPalette.DARK_ELEVATED;
    }

    public static Color getBorder() {
        return currentTheme == Theme.LIGHT ? ColorPalette.LIGHT_BORDER : ColorPalette.DARK_BORDER;
    }

    public static Color getText() {
        return currentTheme == Theme.LIGHT ? ColorPalette.LIGHT_TEXT : ColorPalette.DARK_TEXT;
    }

    public static Color getTextSecondary() {
        return currentTheme == Theme.LIGHT ? ColorPalette.LIGHT_TEXT_SECONDARY : ColorPalette.DARK_TEXT_SECONDARY;
    }

    public static Color getTextMuted() {
        return currentTheme == Theme.LIGHT ? ColorPalette.LIGHT_TEXT_MUTED : ColorPalette.DARK_TEXT_MUTED;
    }

    public static Color getAccent() { return ColorPalette.ACCENT; }
    public static Color getAccentHover() { return ColorPalette.ACCENT_HOVER; }
    public static Color getAccentSoft() { return ColorPalette.ACCENT_SOFT; }

    public static Color getSuccess() { return ColorPalette.SUCCESS; }
    public static Color getSuccessSoft() { return ColorPalette.SUCCESS_SOFT; }
    public static Color getWarning() { return ColorPalette.WARNING; }
    public static Color getWarningSoft() { return ColorPalette.WARNING_SOFT; }
    public static Color getDanger() { return ColorPalette.DANGER; }
    public static Color getDangerSoft() { return ColorPalette.DANGER_SOFT; }
    public static Color getInfo() { return ColorPalette.INFO; }
    public static Color getInfoSoft() { return ColorPalette.INFO_SOFT; }
    public static Color getNeutral() { return ColorPalette.NEUTRAL; }
    public static Color getNeutralSoft() { return ColorPalette.NEUTRAL_SOFT; }

    // ===== Sidebar =====
    // 🔥 FIX: sidebar text ALWAYS light because sidebar is always dark
    public static Color getSidebarBackground() {
        return ColorPalette.DARK_SURFACE;
    }

    public static Color getSidebarText() {
        return ColorPalette.DARK_TEXT; // always light
    }

    public static Color getSidebarHover() {
        return currentTheme == Theme.LIGHT ? ColorPalette.NEUTRAL_SOFT : ColorPalette.DARK_ELEVATED;
    }

    public static Color getSidebarActive() {
        return getAccentSoft();
    }
}