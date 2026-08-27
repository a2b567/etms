package com.etms.view;

import com.etms.controller.AuthController;
import com.etms.dao.AuditLogDAO;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final AuthController authController = new AuthController();
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JLabel messageLabel;

    public LoginFrame() {
        initComponents();
        setTitle("ETMS - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void initComponents() {
        JPanel gradientPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, new Color(25, 118, 210),
                        getWidth(), getHeight(), new Color(66, 165, 245)));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(gradientPanel);

        JPanel center = new JPanel(new MigLayout("fill, insets 30", "[center]", "[center]"));
        center.setOpaque(false);

        JPanel card = new JPanel(new MigLayout("wrap, fillx, insets 40", "[300]", "[]20[]")) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
                g2.dispose();
            }
        };
        card.setOpaque(false);

        JLabel title = new JLabel("ETMS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(25, 118, 210));
        card.add(title, "align center, gapbottom 10");

        JLabel subtitle = new JLabel("Sign in to continue");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);
        card.add(subtitle, "align center, gapbottom 25");

        usernameField = createTextField("Username");
        card.add(usernameField, "growx");
        passwordField = createPasswordField("Password");
        card.add(passwordField, "growx");

        loginBtn = new JButton("LOGIN");
        styleButton(loginBtn);
        loginBtn.addActionListener(e -> handleLogin());
        card.add(loginBtn, "growx, gaptop 15");

        messageLabel = new JLabel();
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        messageLabel.setForeground(Color.RED);
        card.add(messageLabel, "align center, gaptop 5");

        center.add(card);
        gradientPanel.add(center, BorderLayout.CENTER);
    }

    private JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.putClientProperty("JTextField.placeholderText", placeholder);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        return tf;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField pf = new JPasswordField();
        pf.putClientProperty("JPasswordField.placeholderText", placeholder);
        pf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        return pf;
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(25, 118, 210));
        btn.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Enter username and password.");
            return;
        }
        messageLabel.setText("Authenticating...");
        messageLabel.setForeground(Color.BLUE);
        loginBtn.setEnabled(false);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return authController.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        openDashboard(username);
                    } else {
                        messageLabel.setText("Invalid credentials.");
                        messageLabel.setForeground(Color.RED);
                        loginBtn.setEnabled(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    messageLabel.setText("Login error.");
                    messageLabel.setForeground(Color.RED);
                    loginBtn.setEnabled(true);
                }
            }
        }.execute();
    }

    private void openDashboard(String username) {
        // Audit: log login
        try {
            new AuditLogDAO().insertLog(null, username, "LOGIN", "User logged in");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.dispose();
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to open dashboard: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                new LoginFrame().setVisible(true);
            }
        });
    }
}