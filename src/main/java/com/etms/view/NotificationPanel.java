package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class NotificationPanel extends JPanel {

    private final DashboardController controller;
    private JPanel listPanel;

    public NotificationPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        initComponents();
        loadNotifications();
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));

        JLabel title = new JLabel("Notification Center");
        title.setFont(Typography.PAGE_TITLE);
        title.setForeground(ThemeManager.getText());
        add(title, "wrap, gapbottom 10");

        JButton markAllBtn = new JButton("Mark All as Read");
        markAllBtn.setForeground(ThemeManager.getText());
        markAllBtn.setBackground(ThemeManager.getAccent());
        markAllBtn.setFont(Typography.BUTTON);
        markAllBtn.addActionListener(e -> {
            controller.markAllNotificationsRead();
            loadNotifications();
            refreshParentDashboard();
        });
        add(markAllBtn, "wrap, gapbottom 10");

        listPanel = new JPanel(new MigLayout("fillx, wrap 1", "[grow]", "[]"));
        listPanel.setBackground(ThemeManager.getSurface());
        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            "Notifications",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            Typography.CARD_TITLE,
            ThemeManager.getText()
        ));
        scrollPane.getViewport().setBackground(ThemeManager.getSurface());
        add(scrollPane, "grow");
    }

    private void loadNotifications() {
        listPanel.removeAll();
        List<Map<String, Object>> notifications = controller.getNotificationsForCurrentUser();
        if (notifications.isEmpty()) {
            JLabel empty = new JLabel("No notifications.");
            empty.setFont(Typography.BODY);
            empty.setForeground(ThemeManager.getTextSecondary());
            listPanel.add(empty, "grow");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Map<String, Object> n : notifications) {
                JPanel row = new JPanel(new MigLayout("fillx, insets 5", "[grow][]10[]", "[]"));
                boolean isRead = (Boolean) n.get("isRead");
                row.setBackground(isRead ? ThemeManager.getSurface() : ThemeManager.getAccentSoft());
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorder()));

                String msg = (String) n.get("message");
                LocalDateTime time = (LocalDateTime) n.get("createdAt");
                int id = (int) n.get("id");

                JLabel msgLabel = new JLabel("<html>" + (isRead ? "" : "● ") + msg + "</html>");
                msgLabel.setFont(Typography.BODY);
                msgLabel.setForeground(ThemeManager.getText());
                row.add(msgLabel, "growx");

                JLabel timeLabel = new JLabel(time.format(formatter));
                timeLabel.setFont(Typography.LABEL);
                timeLabel.setForeground(ThemeManager.getTextSecondary());
                row.add(timeLabel);

                if (!isRead) {
                    JButton markBtn = new JButton("Mark Read");
                    markBtn.setFont(Typography.LABEL);
                    markBtn.setForeground(ThemeManager.getAccent());
                    markBtn.setBackground(ThemeManager.getSurface());
                    markBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.getAccent()));
                    markBtn.addActionListener(e -> {
                        controller.markNotificationRead(id);
                        loadNotifications();
                        refreshParentDashboard();
                    });
                    row.add(markBtn);
                } else {
                    row.add(new JLabel(""));
                }
                listPanel.add(row, "growx");
            }
        }
        listPanel.revalidate();
        listPanel.repaint();
    }

    private void refreshParentDashboard() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof MainFrame) {
            ((MainFrame) window).refreshDashboard();
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
                        Typography.CARD_TITLE,
                        ThemeManager.getText()
                    ));
                }
            }
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(ThemeManager.getText());
            }
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String text = btn.getText();
                if (text != null && text.equals("Mark All as Read")) {
                    btn.setBackground(ThemeManager.getAccent());
                    btn.setForeground(Color.WHITE);
                } else {
                    btn.setBackground(ThemeManager.getSurface());
                    btn.setForeground(ThemeManager.getText());
                    btn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
                }
            }
        }

        if (listPanel != null) {
            listPanel.setBackground(ThemeManager.getSurface());
            for (Component row : listPanel.getComponents()) {
                if (row instanceof JPanel) {
                    JPanel rowPanel = (JPanel) row;
                    rowPanel.setBackground(ThemeManager.getSurface());
                    for (Component inner : rowPanel.getComponents()) {
                        if (inner instanceof JLabel) {
                            ((JLabel) inner).setForeground(ThemeManager.getText());
                        }
                        if (inner instanceof JButton) {
                            JButton btn = (JButton) inner;
                            btn.setBackground(ThemeManager.getSurface());
                            btn.setForeground(ThemeManager.getAccent());
                            btn.setBorder(BorderFactory.createLineBorder(ThemeManager.getAccent()));
                        }
                    }
                }
            }
        }

        revalidate();
        repaint();
    }
}