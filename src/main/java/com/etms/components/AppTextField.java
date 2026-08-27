package com.etms.components;

import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;

import javax.swing.*;

public class AppTextField extends JTextField {

    public AppTextField(String placeholder) {
        setFont(Typography.BODY);
        putClientProperty("JTextField.placeholderText", placeholder);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            BorderFactory.createEmptyBorder(Spacing.SM, Spacing.MD, Spacing.SM, Spacing.MD)
        ));
        setBackground(ThemeManager.getSurface());
        setForeground(ThemeManager.getText());
        setCaretColor(ThemeManager.getAccent());
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getSurface());
        setForeground(ThemeManager.getText());
        setCaretColor(ThemeManager.getAccent());
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            BorderFactory.createEmptyBorder(Spacing.SM, Spacing.MD, Spacing.SM, Spacing.MD)
        ));
    }
}