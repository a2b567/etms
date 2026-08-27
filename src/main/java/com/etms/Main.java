package com.etms;

import com.etms.config.DatabaseInitializer;
import com.etms.theme.ThemeManager;          // ✅ Use the correct ThemeManager
import com.etms.view.LoginFrame;
import com.etms.view.SplashScreen;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        // Apply the light theme (FlatLaf) and initialize the theme state
        ThemeManager.applyLightTheme();

        SplashScreen splash = new SplashScreen();
        splash.setVisible(true);

        setApplicationIcon();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    DatabaseInitializer.initialize();
                } catch (SQLException e) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null,
                            "Database initialization failed:\n" + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    });
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                splash.dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginFrame login = new LoginFrame();
                    setFrameIcon(login);
                    login.setVisible(true);
                });
            }
        };
        worker.execute();
    }

    private static void setApplicationIcon() {
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(
                    Main.class.getResource("/icons/app_icon.png")
            );
            if (icon != null) {
                UIManager.put("javax.swing.frame.icon", new ImageIcon(icon));
            }
        } catch (Exception ignored) {}
    }

    private static void setFrameIcon(Frame frame) {
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(
                    Main.class.getResource("/icons/app_icon.png")
            );
            if (icon != null) {
                frame.setIconImage(icon);
            }
        } catch (Exception ignored) {}
    }
}