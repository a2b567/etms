package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import com.etms.ui.components.ETMSButton;
import com.etms.ui.components.ETMSPageHeader;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {

    private final DashboardController controller;
    private JCheckBox autoSaveCheck;
    private JButton saveBtn;

    public SettingsPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));
        initComponents();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Settings", "System configuration and preferences.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel settingsForm = new JPanel(new MigLayout("wrap 2, fillx", "[right][grow]", "[]10[]"));
        settingsForm.setBackground(ThemeManager.getSurface());
        settingsForm.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));

        // Removed Dark Mode toggle – it's now in the top bar (global theme switch)

        settingsForm.add(new JLabel("Auto-save:"));
        autoSaveCheck = new JCheckBox("Enable auto-save");
        autoSaveCheck.setFont(Typography.BODY);
        settingsForm.add(autoSaveCheck, "growx");

        saveBtn = new ETMSButton("Save Settings", ETMSButton.Variant.PRIMARY);
        saveBtn.addActionListener(e -> saveSettings());
        settingsForm.add(saveBtn, "span 2, growx, gaptop " + Spacing.SM);

        add(settingsForm, "grow");
    }

    private void saveSettings() {
        JOptionPane.showMessageDialog(this, "Settings saved!");
    }
}