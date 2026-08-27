package com.etms.theme;

import java.awt.Font;

public final class Typography {

    private Typography() {}

    // ===== HIERARCHY =====
    // Page Title – largest, boldest
    public static final Font PAGE_TITLE = new Font("Segoe UI", Font.BOLD, 28);

    // Section Title – large, semibold
    public static final Font SECTION_TITLE = new Font("Segoe UI", Font.BOLD, 20);

    // Card Title – medium, semibold
    public static final Font CARD_TITLE = new Font("Segoe UI", Font.BOLD, 14);

    // Metric – large, bold (for KPI numbers)
    public static final Font METRIC = new Font("Segoe UI", Font.BOLD, 30);

    // Body – regular reading text
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);

    // Body Bold – bold reading text
    public static final Font BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // Secondary – slightly smaller, muted
    public static final Font SECONDARY = new Font("Segoe UI", Font.PLAIN, 13);

    // Label – small, muted (for form labels, metadata)
    public static final Font LABEL = new Font("Segoe UI", Font.PLAIN, 12);

    // Caption – very small, muted (for timestamps, footers)
    public static final Font CAPTION = new Font("Segoe UI", Font.PLAIN, 11);

    // Button – bold, consistent size
    public static final Font BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    // Badge – small, semibold
    public static final Font BADGE = new Font("Segoe UI", Font.BOLD, 11);

    // Navigation – medium weight, clean
    public static final Font NAVIGATION = new Font("Segoe UI", Font.PLAIN, 14);

    // Navigation Active – same size, bold
    public static final Font NAVIGATION_ACTIVE = new Font("Segoe UI", Font.BOLD, 14);
}