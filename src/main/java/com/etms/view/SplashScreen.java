package com.etms.view;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {
    public SplashScreen() {
        setSize(400, 250);
        setLocationRelativeTo(null);
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(25, 118, 210));
        JLabel label = new JLabel("ETMS", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 40));
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.CENTER);
        JLabel loading = new JLabel("Loading...", SwingConstants.CENTER);
        loading.setForeground(Color.WHITE);
        panel.add(loading, BorderLayout.SOUTH);
        add(panel);
    }
}