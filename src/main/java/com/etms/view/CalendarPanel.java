package com.etms.view;

import com.etms.config.DatabaseConfig;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class CalendarPanel extends JPanel {

    private YearMonth currentMonth;
    private JLabel monthLabel;
    private JPanel daysPanel;
    private JTextArea eventArea;
    private LocalDate selectedDate;

    public CalendarPanel() {
        currentMonth = YearMonth.now();
        setBackground(ThemeManager.getBackground());
        initComponents();
        updateCalendar();
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));

        JLabel title = new JLabel("Calendar Scheduler");
        title.setFont(Typography.PAGE_TITLE);
        title.setForeground(ThemeManager.getText());
        add(title, "wrap, gapbottom 20");

        JPanel navPanel = new JPanel(new MigLayout("insets 0", "[][grow][]", "[]"));
        navPanel.setOpaque(false);
        JButton prevBtn = new JButton("<");
        prevBtn.setForeground(ThemeManager.getText());
        prevBtn.setBackground(ThemeManager.getSurface());
        prevBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
        prevBtn.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            updateCalendar();
        });
        navPanel.add(prevBtn);

        monthLabel = new JLabel();
        monthLabel.setFont(Typography.SECTION_TITLE);
        monthLabel.setForeground(ThemeManager.getText());
        monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        navPanel.add(monthLabel, "growx");

        JButton nextBtn = new JButton(">");
        nextBtn.setForeground(ThemeManager.getText());
        nextBtn.setBackground(ThemeManager.getSurface());
        nextBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
        nextBtn.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            updateCalendar();
        });
        navPanel.add(nextBtn);

        add(navPanel, "growx, wrap, gapbottom 10");

        daysPanel = new JPanel(new GridLayout(0, 7, 2, 2));
        daysPanel.setBackground(ThemeManager.getBackground());
        add(daysPanel, "grow, wrap");

        eventArea = new JTextArea(5, 30);
        eventArea.setEditable(false);
        eventArea.setFont(Typography.BODY);
        eventArea.setBackground(ThemeManager.getSurface());
        eventArea.setForeground(ThemeManager.getText());
        eventArea.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
        JScrollPane eventScroll = new JScrollPane(eventArea);
        eventScroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            "Events for selected date",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            Typography.CARD_TITLE,
            ThemeManager.getText()
        ));
        eventScroll.getViewport().setBackground(ThemeManager.getSurface());
        add(eventScroll, "growx");
    }

    private void updateCalendar() {
        daysPanel.removeAll();
        monthLabel.setText(currentMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + currentMonth.getYear());

        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        for (String name : dayNames) {
            JLabel lbl = new JLabel(name, SwingConstants.CENTER);
            lbl.setFont(Typography.LABEL);
            lbl.setOpaque(true);
            lbl.setBackground(ThemeManager.getSurface());
            lbl.setForeground(ThemeManager.getTextSecondary());
            daysPanel.add(lbl);
        }

        LocalDate firstOfMonth = currentMonth.atDay(1);
        int startDay = firstOfMonth.getDayOfWeek().getValue() - 1;
        int daysInMonth = currentMonth.lengthOfMonth();

        for (int i = 0; i < startDay; i++) {
            JLabel empty = new JLabel("");
            empty.setBackground(ThemeManager.getBackground());
            daysPanel.add(empty);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);
            JButton dayBtn = new JButton(String.valueOf(day));
            dayBtn.setFont(Typography.BODY);
            dayBtn.setBackground(ThemeManager.getSurface());
            dayBtn.setForeground(ThemeManager.getText());
            dayBtn.setFocusPainted(false);
            dayBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));

            if (date.equals(LocalDate.now())) {
                dayBtn.setBackground(ThemeManager.getAccent());
                dayBtn.setForeground(Color.WHITE);
            }
            if (selectedDate != null && date.equals(selectedDate)) {
                dayBtn.setBackground(ThemeManager.getAccentSoft());
                dayBtn.setForeground(ThemeManager.getAccent());
            }

            boolean hasEvents = hasEvents(date);
            if (hasEvents) {
                dayBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.getSuccess(), 2));
            }

            dayBtn.addActionListener(e -> {
                selectedDate = date;
                updateCalendar();
                loadEventsForDate(date);
            });

            daysPanel.add(dayBtn);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private boolean hasEvents(LocalDate date) {
        try (Connection conn = DatabaseConfig.getInstance().getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement("SELECT COUNT(*) FROM matches WHERE DATE(scheduled_time) = ?");
            ps1.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next() && rs1.getInt(1) > 0) return true;

            PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) FROM tournaments WHERE start_date = ?");
            ps2.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next() && rs2.getInt(1) > 0) return true;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    private void loadEventsForDate(LocalDate date) {
        StringBuilder sb = new StringBuilder();
        sb.append("Events for ").append(date.format(DateTimeFormatter.ISO_LOCAL_DATE)).append(":\n\n");

        try (Connection conn = DatabaseConfig.getInstance().getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement(
                "SELECT m.match_id, t1.team_name AS team1, t2.team_name AS team2, m.scheduled_time " +
                "FROM matches m " +
                "LEFT JOIN teams t1 ON m.team1_id = t1.team_id " +
                "LEFT JOIN teams t2 ON m.team2_id = t2.team_id " +
                "WHERE DATE(m.scheduled_time) = ?");
            ps1.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs1 = ps1.executeQuery();
            while (rs1.next()) {
                String team1 = rs1.getString("team1") != null ? rs1.getString("team1") : "TBD";
                String team2 = rs1.getString("team2") != null ? rs1.getString("team2") : "TBD";
                String time = rs1.getTimestamp("scheduled_time") != null ?
                    rs1.getTimestamp("scheduled_time").toLocalDateTime().toLocalTime().toString() : "All day";
                sb.append("Match: ").append(team1).append(" vs ").append(team2).append(" at ").append(time).append("\n");
            }

            PreparedStatement ps2 = conn.prepareStatement("SELECT tournament_name FROM tournaments WHERE start_date = ?");
            ps2.setDate(1, java.sql.Date.valueOf(date));
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                sb.append("Tournament: ").append(rs2.getString("tournament_name")).append(" starts\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (sb.toString().equals("Events for " + date + ":\n\n")) {
            sb.append("No events.");
        }

        eventArea.setText(sb.toString());
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
                btn.setBackground(ThemeManager.getSurface());
                btn.setForeground(ThemeManager.getText());
                btn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
                // Re-apply special styles based on text
                String text = btn.getText();
                if (text != null && (text.equals("<") || text.equals(">"))) {
                    btn.setBackground(ThemeManager.getSurface());
                    btn.setForeground(ThemeManager.getText());
                }
            }
        }

        if (monthLabel != null) monthLabel.setForeground(ThemeManager.getText());
        if (eventArea != null) {
            eventArea.setBackground(ThemeManager.getSurface());
            eventArea.setForeground(ThemeManager.getText());
            eventArea.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
        }

        if (daysPanel != null) {
            for (Component comp : daysPanel.getComponents()) {
                if (comp instanceof JButton) {
                    JButton btn = (JButton) comp;
                    btn.setBackground(ThemeManager.getSurface());
                    btn.setForeground(ThemeManager.getText());
                    btn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
                    LocalDate now = LocalDate.now();
                    if (btn.getText().equals(String.valueOf(now.getDayOfMonth())) &&
                        currentMonth.getMonth() == now.getMonth() &&
                        currentMonth.getYear() == now.getYear()) {
                        btn.setBackground(ThemeManager.getAccent());
                        btn.setForeground(Color.WHITE);
                    }
                    if (selectedDate != null && btn.getText().equals(String.valueOf(selectedDate.getDayOfMonth())) &&
                        currentMonth.getMonth() == selectedDate.getMonth() &&
                        currentMonth.getYear() == selectedDate.getYear()) {
                        btn.setBackground(ThemeManager.getAccentSoft());
                        btn.setForeground(ThemeManager.getAccent());
                    }
                }
                if (comp instanceof JLabel) {
                    JLabel lbl = (JLabel) comp;
                    lbl.setBackground(ThemeManager.getSurface());
                    lbl.setForeground(ThemeManager.getTextSecondary());
                }
            }
        }

        revalidate();
        repaint();
    }
}