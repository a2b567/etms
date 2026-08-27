package com.etms.theme;

public final class UIConstants {

    private UIConstants() {}

    // ===== BORDER RADIUS =====
    public static final int BORDER_RADIUS_NONE = 0;
    public static final int BORDER_RADIUS_SM = 6;
    public static final int BORDER_RADIUS_MD = 10;
    public static final int BORDER_RADIUS_LG = 14;
    public static final int BORDER_RADIUS_XL = 20;

    // Semantic aliases
    public static final int BORDER_RADIUS_BUTTON = BORDER_RADIUS_SM;
    public static final int BORDER_RADIUS_CARD = BORDER_RADIUS_MD;
    public static final int BORDER_RADIUS_DIALOG = BORDER_RADIUS_LG;
    public static final int BORDER_RADIUS_INPUT = BORDER_RADIUS_SM;
    public static final int BORDER_RADIUS_BADGE = BORDER_RADIUS_XL; // pill shape

    // ===== COMPONENT HEIGHTS =====
    public static final int COMPONENT_HEIGHT_SM = 28;
    public static final int COMPONENT_HEIGHT_MD = 36;
    public static final int COMPONENT_HEIGHT_LG = 44;

    public static final int BUTTON_HEIGHT = COMPONENT_HEIGHT_MD;
    public static final int INPUT_HEIGHT = COMPONENT_HEIGHT_MD;

    // ===== SHADOWS (conceptual – Swing doesn't support natively, but we use borders) =====
    // For cards, we use subtle border contrast instead of shadows.
}