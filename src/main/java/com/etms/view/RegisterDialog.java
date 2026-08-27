package com.etms.view;

import com.etms.controller.AuthController;
import com.etms.util.ValidationUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class RegisterDialog extends JDialog {

    private final AuthController authController;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JComboBox<String> roleComboBox;
    private JButton registerButton;
    private JLabel messageLabel;

    public RegisterDialog(JFrame parent, AuthController authController) {
        super(parent, "Register New Account", true);
        this.authController = authController;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new MigLayout("wrap 2, fillx, insets 20",
                "[right][250:300:]", "[]10[]"));

        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(titleLabel, "span 2, align center, wrap, gapbottom 15");

        mainPanel.add(new JLabel("Username (3-20 chars):"));
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, "growx");

        mainPanel.add(new JLabel("Password (min 6 chars):"));
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, "growx");

        mainPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField(20);
        mainPanel.add(confirmPasswordField, "growx");

        mainPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        mainPanel.add(emailField, "growx");

        mainPanel.add(new JLabel("Role:"));
        // Only non-privileged roles available during self-registration.
        // ADMIN and ORGANIZER must be assigned by an admin.
        roleComboBox = new JComboBox<>(new String[]{"PLAYER", "COACH", "REFEREE"});
        mainPanel.add(roleComboBox, "growx");

        JPanel buttonPanel = new JPanel(new MigLayout("fillx", "[grow][grow]", "[]"));
        registerButton = new JButton("Register");
        registerButton.setBackground(new Color(0, 120, 215));
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> handleRegister());
        buttonPanel.add(registerButton, "growx, h 35!");

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton, "growx, h 35!");
        mainPanel.add(buttonPanel, "span 2, growx, gaptop 15");

        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(messageLabel, "span 2");

        add(mainPanel);
        pack();
        setLocationRelativeTo(getParent());
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();

        StringBuilder errors = new StringBuilder();
        if (!ValidationUtil.isValidUsername(username))
            errors.append("• Username must be 3-20 characters (letters, numbers, underscore).\n");
        if (password.length() < 6)
            errors.append("• Password must be at least 6 characters.\n");
        if (!password.equals(confirm))
            errors.append("• Passwords do not match.\n");
        if (!ValidationUtil.isValidEmail(email))
            errors.append("• Invalid email address.\n");

        if (errors.length() > 0) {
            messageLabel.setText("<html>" + errors.toString().replace("\n", "<br>") + "</html>");
            return;
        }

        registerButton.setEnabled(false);
        messageLabel.setText("Registering...");
        messageLabel.setForeground(Color.BLUE);

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return authController.register(username, password, email, role);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(RegisterDialog.this,
                                "Registration successful! You can now login.",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        messageLabel.setText("Username or email already exists.");
                        messageLabel.setForeground(Color.RED);
                        registerButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    messageLabel.setText("Registration failed.");
                    messageLabel.setForeground(Color.RED);
                    registerButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }
}