package com.etms.ui.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.UIConstants;
import com.etms.theme.Spacing;

import javax.swing.*;
import java.awt.*;

public class ETMSDialog {

    public static void applyDialogTheme(JDialog dialog) {
        dialog.getContentPane().setBackground(ThemeManager.getBackground());
        // Set a standard padding on the content pane's root panel if needed
        if (dialog.getContentPane().getLayout() instanceof BorderLayout) {
            JPanel content = (JPanel) dialog.getContentPane();
            content.setBorder(BorderFactory.createEmptyBorder(Spacing.LG, Spacing.LG, Spacing.LG, Spacing.LG));
        }
        // Ensure buttons are styled, etc.
    }
}