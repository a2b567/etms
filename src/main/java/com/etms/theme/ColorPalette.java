package com.etms.theme;

import java.awt.Color;

public final class ColorPalette {
    private ColorPalette() {}

    // Dark Theme
    public static final Color DARK_BG = new Color(0x0F1115);
    public static final Color DARK_SURFACE = new Color(0x1A1D24);
    public static final Color DARK_ELEVATED = new Color(0x262A33);
    public static final Color DARK_BORDER = new Color(0x333844);
    public static final Color DARK_TEXT = new Color(0xEDF2F7);
    public static final Color DARK_TEXT_SECONDARY = new Color(0x9CA3AF);
    // 🔥 FIX: made muted text lighter
    public static final Color DARK_TEXT_MUTED = new Color(0x9CA3AF); // was 0x6B7280

    // Light Theme
    public static final Color LIGHT_BG = new Color(0xF3F4F6);
    public static final Color LIGHT_SURFACE = new Color(0xFFFFFF);
    public static final Color LIGHT_BORDER = new Color(0xE5E7EB);
    public static final Color LIGHT_TEXT = new Color(0x111827);
    public static final Color LIGHT_TEXT_SECONDARY = new Color(0x6B7280);
    public static final Color LIGHT_TEXT_MUTED = new Color(0x9CA3AF);

    // Brand accent
    public static final Color ACCENT = new Color(0x3B82F6);
    public static final Color ACCENT_HOVER = new Color(0x2563EB);
    public static final Color ACCENT_SOFT = new Color(0xDBEAFE);

    // Semantic colors
    public static final Color SUCCESS = new Color(0x10B981);
    public static final Color SUCCESS_SOFT = new Color(0xD1FAE5);
    public static final Color WARNING = new Color(0xF59E0B);
    public static final Color WARNING_SOFT = new Color(0xFEF3C7);
    public static final Color DANGER = new Color(0xEF4444);
    public static final Color DANGER_SOFT = new Color(0xFEE2E2);
    public static final Color INFO = new Color(0x06B6D4);
    public static final Color INFO_SOFT = new Color(0xCFFAFE);
    public static final Color NEUTRAL = new Color(0x6B7280);
    public static final Color NEUTRAL_SOFT = new Color(0xF3F4F6);
}