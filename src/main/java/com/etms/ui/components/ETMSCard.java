package com.etms.ui.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.UIConstants;
import com.etms.theme.Spacing;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ETMSCard extends JPanel {

    private int padding;

    public ETMSCard() {
        this(Spacing.LG);
    }

    public ETMSCard(int padding) {
        this.padding = padding;
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(ThemeManager.getSurface());
        applyBorder();
    }

    private void applyBorder() {
        // Use rounded border with line border and padding
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorder(), 1, true),
                new EmptyBorder(padding, padding, padding, padding)
        ));
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getSurface());
        applyBorder();
    }

    public void setPadding(int padding) {
        this.padding = padding;
        applyBorder();
    }
}