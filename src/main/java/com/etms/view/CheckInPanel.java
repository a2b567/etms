package com.etms.view;

import com.etms.config.DatabaseConfig;
import com.etms.dao.PlayerDAO;
import com.etms.dao.TeamDAO;
import com.etms.model.Player;
import com.etms.model.Team;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import com.etms.util.QRCodeUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CheckInPanel extends JPanel {

    private JComboBox<String> entityTypeCombo;
    private JComboBox<String> entityCombo;
    private JLabel qrLabel;
    private JButton generateBtn, saveBtn, scanBtn;
    private JTextArea logArea;
    private JFileChooser fileChooser;

    public CheckInPanel() {
        setBackground(ThemeManager.getBackground());
        initComponents();
        loadEntities();
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));

        JLabel title = new JLabel("QR Code Check‑In");
        title.setFont(Typography.PAGE_TITLE);
        title.setForeground(ThemeManager.getText());
        add(title, "wrap, gapbottom 20");

        JPanel genPanel = new JPanel(new MigLayout("wrap 2, fillx", "[right][grow]", "[]5[]"));
        genPanel.setBackground(ThemeManager.getSurface());
        genPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            "Generate QR Code",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            Typography.CARD_TITLE,
            ThemeManager.getText()
        ));

        genPanel.add(new JLabel("Entity Type:"));
        entityTypeCombo = new JComboBox<>(new String[]{"PLAYER", "TEAM", "REFEREE"});
        entityTypeCombo.setFont(Typography.BODY);
        entityTypeCombo.setBackground(ThemeManager.getSurface());
        entityTypeCombo.setForeground(ThemeManager.getText());
        entityTypeCombo.addActionListener(e -> loadEntities());
        genPanel.add(entityTypeCombo, "growx");

        genPanel.add(new JLabel("Select Entity:"));
        entityCombo = new JComboBox<>();
        entityCombo.setFont(Typography.BODY);
        entityCombo.setBackground(ThemeManager.getSurface());
        entityCombo.setForeground(ThemeManager.getText());
        genPanel.add(entityCombo, "growx");

        generateBtn = new JButton("Generate QR");
        generateBtn.setFont(Typography.BUTTON);
        generateBtn.setBackground(ThemeManager.getAccent());
        generateBtn.setForeground(Color.WHITE);
        generateBtn.addActionListener(e -> generateQR());
        genPanel.add(generateBtn, "span 2, align center, gaptop 10");

        qrLabel = new JLabel();
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qrLabel.setPreferredSize(new Dimension(200, 200));
        qrLabel.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
        genPanel.add(qrLabel, "span 2, align center, gaptop 10");

        saveBtn = new JButton("Save QR Image");
        saveBtn.setFont(Typography.BUTTON);
        saveBtn.setBackground(ThemeManager.getSuccess());
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> saveQR());
        saveBtn.setEnabled(false);
        genPanel.add(saveBtn, "span 2, align center, gaptop 5");

        add(genPanel, "grow, wrap, gapbottom 20");

        JPanel scanPanel = new JPanel(new MigLayout("fillx, insets 0", "[grow]", "[]10[]"));
        scanPanel.setBackground(ThemeManager.getSurface());
        scanPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            "Scan / Check‑In",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            Typography.CARD_TITLE,
            ThemeManager.getText()
        ));

        scanBtn = new JButton("Load QR Image & Check‑In");
        scanBtn.setFont(Typography.BUTTON);
        scanBtn.setBackground(ThemeManager.getAccent());
        scanBtn.setForeground(Color.WHITE);
        scanBtn.addActionListener(e -> scanAndCheckIn());
        scanPanel.add(scanBtn, "growx, wrap");

        JLabel infoLabel = new JLabel("Click to load a QR code image file (png, jpg).");
        infoLabel.setFont(Typography.SECONDARY);
        infoLabel.setForeground(ThemeManager.getTextSecondary());
        scanPanel.add(infoLabel, "wrap");

        logArea = new JTextArea(8, 40);
        logArea.setEditable(false);
        logArea.setFont(Typography.BODY);
        logArea.setBackground(ThemeManager.getSurface());
        logArea.setForeground(ThemeManager.getText());
        logArea.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            "Recent Check‑Ins",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            Typography.LABEL,
            ThemeManager.getText()
        ));
        scrollPane.getViewport().setBackground(ThemeManager.getSurface());
        scanPanel.add(scrollPane, "growx");

        add(scanPanel, "grow, wrap");

        fileChooser = new JFileChooser();
        refreshCheckInLog();
    }

    private void loadEntities() {
        entityCombo.removeAllItems();
        String type = (String) entityTypeCombo.getSelectedItem();
        try {
            switch (type) {
                case "PLAYER":
                    List<Player> players = new PlayerDAO().getAllPlayers();
                    for (Player p : players) {
                        entityCombo.addItem(p.getInGameName() + " (ID:" + p.getPlayerId() + ")");
                    }
                    break;
                case "TEAM":
                    List<Team> teams = new TeamDAO().getAllTeams();
                    for (Team t : teams) {
                        entityCombo.addItem(t.getTeamName() + " (ID:" + t.getTeamId() + ")");
                    }
                    break;
                case "REFEREE":
                    entityCombo.addItem("No referees available");
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getSelectedEntityId() {
        String selected = (String) entityCombo.getSelectedItem();
        if (selected == null || selected.startsWith("No")) return -1;
        try {
            return Integer.parseInt(selected.substring(selected.lastIndexOf(":") + 1, selected.lastIndexOf(")")));
        } catch (Exception e) {
            return -1;
        }
    }

    private String getSelectedEntityType() {
        return (String) entityTypeCombo.getSelectedItem();
    }

    private void generateQR() {
        int entityId = getSelectedEntityId();
        if (entityId == -1) {
            JOptionPane.showMessageDialog(this, "Please select an entity.");
            return;
        }
        String type = getSelectedEntityType();
        String qrContent = type + ":" + entityId;
        BufferedImage qrImage = QRCodeUtil.generateQRCodeImage(qrContent, 200, 200, null);
        if (qrImage != null) {
            qrLabel.setIcon(new ImageIcon(qrImage));
            saveBtn.setEnabled(true);
        } else {
            qrLabel.setIcon(null);
            JOptionPane.showMessageDialog(this, "Failed to generate QR code.");
        }
    }

    private void saveQR() {
        Icon icon = qrLabel.getIcon();
        if (icon == null) return;
        int entityId = getSelectedEntityId();
        if (entityId == -1) return;
        String type = getSelectedEntityType();
        String fileName = type + "_" + entityId + ".png";
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }
            QRCodeUtil.generateQRCodeImage(type + ":" + entityId, 200, 200, file.toPath());
            JOptionPane.showMessageDialog(this, "QR code saved to " + file.getAbsolutePath());
        }
    }

    private void scanAndCheckIn() {
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String decodedText = QRCodeUtil.decodeQRCode(file.toPath());
            if (decodedText == null) {
                JOptionPane.showMessageDialog(this, "Failed to decode QR code.");
                return;
            }
            String[] parts = decodedText.split(":");
            if (parts.length != 2) {
                JOptionPane.showMessageDialog(this, "Invalid QR content: " + decodedText);
                return;
            }
            String type = parts[0];
            int id;
            try {
                id = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid ID in QR code.");
                return;
            }

            try (Connection conn = DatabaseConfig.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO check_ins (entity_type, entity_id) VALUES (?,?)")) {
                ps.setString(1, type);
                ps.setInt(2, id);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error during check-in.");
                return;
            }

            JOptionPane.showMessageDialog(this, "Check‑in successful! " + type + ":" + id);
            refreshCheckInLog();
        }
    }

    private void refreshCheckInLog() {
        logArea.setText("");
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM check_ins ORDER BY checked_at DESC LIMIT 20");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String type = rs.getString("entity_type");
                int id = rs.getInt("entity_id");
                Timestamp ts = rs.getTimestamp("checked_at");
                logArea.append(type + " " + id + " at " + ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getBackground());

        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                panel.setBackground(ThemeManager.getBackground());
                if (panel.getBorder() instanceof TitledBorder) {
                    panel.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(ThemeManager.getBorder()),
                        ((TitledBorder) panel.getBorder()).getTitle(),
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        Typography.CARD_TITLE,
                        ThemeManager.getText()
                    ));
                }
            }
            if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                scroll.setBackground(ThemeManager.getBackground());
                scroll.getViewport().setBackground(ThemeManager.getSurface());
                if (scroll.getBorder() instanceof TitledBorder) {
                    scroll.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(ThemeManager.getBorder()),
                        ((TitledBorder) scroll.getBorder()).getTitle(),
                        TitledBorder.DEFAULT_JUSTIFICATION,
                        TitledBorder.DEFAULT_POSITION,
                        Typography.LABEL,
                        ThemeManager.getText()
                    ));
                }
            }
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(ThemeManager.getText());
            }
            if (comp instanceof JComboBox) {
                JComboBox<?> cb = (JComboBox<?>) comp;
                cb.setBackground(ThemeManager.getSurface());
                cb.setForeground(ThemeManager.getText());
            }
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String text = btn.getText();
                if (text != null) {
                    if (text.equals("Generate QR") || text.equals("Load QR Image & Check‑In")) {
                        btn.setBackground(ThemeManager.getAccent());
                        btn.setForeground(Color.WHITE);
                    } else if (text.equals("Save QR Image")) {
                        btn.setBackground(ThemeManager.getSuccess());
                        btn.setForeground(Color.WHITE);
                    } else {
                        btn.setBackground(ThemeManager.getSurface());
                        btn.setForeground(ThemeManager.getText());
                        btn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
                    }
                }
            }
            if (comp instanceof JTextArea) {
                JTextArea ta = (JTextArea) comp;
                ta.setBackground(ThemeManager.getSurface());
                ta.setForeground(ThemeManager.getText());
                ta.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
            }
            if (comp == qrLabel) {
                qrLabel.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
            }
        }

        revalidate();
        repaint();
    }
}