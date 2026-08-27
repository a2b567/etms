package com.etms.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;

import javax.swing.*;
import java.awt.*;

public class AppButton extends JButton {

    public enum ButtonType { PRIMARY, SECONDARY, SUCCESS, DANGER, GHOST }

    private ButtonType type;

    public AppButton(String text, ButtonType type) {
        super(text);
        this.type = type;
        setFont(Typography.BUTTON);
        setFocusPainted(false);
        setContentAreaFilled(false);  // So we can control background manually
        setBorderPainted(false);      // No border at all
        setOpaque(false);             // Start transparent
        setCursor(Cursor.getDefaultCursor());
        applyStyle();
    }

    private void applyStyle() {
        // All buttons start with no background and no border
        setBackground(null);
        setForeground(ThemeManager.getText());

        switch (type) {
            case PRIMARY:
                setBackground(ThemeManager.getAccent());
                setForeground(Color.WHITE);
                setOpaque(true);
                break;
            case SUCCESS:
                setBackground(ThemeManager.getSuccess());
                setForeground(Color.WHITE);
                setOpaque(true);
                break;
            case DANGER:
                setBackground(ThemeManager.getDanger());
                setForeground(Color.WHITE);
                setOpaque(true);
                break;
            case SECONDARY:
            case GHOST:
            default:
                // Plain text – no background, no border
                setBackground(null);
                setForeground(ThemeManager.getText());
                setOpaque(false);
                break;
        }
    }

    /**
     * Refreshes the button when the theme changes.
     */
    public void refreshTheme() {
        applyStyle();
    }
}