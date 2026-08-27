package com.etms.components;

import com.etms.view.MainFrame;
import com.etms.model.User;
import com.etms.service.UserSession;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import com.etms.util.IconHelper;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Sidebar extends JPanel {
    private final CardLayout cardLayout;
    private final JPanel contentPanel;
    private final MainFrame mainFrame;
    private boolean expanded = true;
    private String activePanel = "DASHBOARD";

    private final List<JButton> navButtons = new ArrayList<>();
    private JButton logoutBtn;
    private JButton activeBtn;

    private JButton dashboardBtn, analyticsBtn, tournamentsBtn, teamsBtn, playersBtn;
    private JButton matchesBtn, bracketsBtn, standingsBtn, venuesBtn;
    private JButton sponsorsBtn, prizePoolsBtn, refereesBtn, equipmentBtn;
    private JButton staffBtn, coachesBtn, auditBtn, usersBtn, settingsBtn, reportsBtn;

    private JPanel navPanel;
    private JScrollPane scrollPane;
    private JPanel bottomPanel;
    private JLabel appTitle;

    public Sidebar(CardLayout cardLayout, JPanel contentPanel, MainFrame mainFrame) {
        this.cardLayout = cardLayout;
        this.contentPanel = contentPanel;
        this.mainFrame = mainFrame;
        initComponents();
        updateVisibility();
        applyTheme();
        setPreferredSize(new Dimension(220, 0));
        setMinimumSize(new Dimension(220, 0));
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(ThemeManager.getSidebarBackground());

        // ===== TOP: Logo =====
        JPanel topPanel = new JPanel(new MigLayout("fill, insets 10 10 5 10", "[grow]", "[]"));
        topPanel.setBackground(ThemeManager.getSidebarBackground());
        appTitle = new JLabel("ETMS");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        appTitle.setForeground(ThemeManager.getAccent());
        appTitle.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(appTitle, "growx");
        add(topPanel, BorderLayout.NORTH);

        // ===== MIDDLE: Scrollable Navigation =====
        navPanel = new JPanel();
        navPanel.setLayout(new MigLayout("fill, insets " + Spacing.SM + " " + Spacing.MD + " " + Spacing.SM + " " + Spacing.MD, "[grow]", "[]10[]"));
        navPanel.setBackground(ThemeManager.getSidebarBackground());

        addSectionLabel("OVERVIEW");
        dashboardBtn = createNavButton("Dashboard", "DASHBOARD", e -> navigateTo("DASHBOARD"));
        navPanel.add(dashboardBtn, "growx, wrap");
        analyticsBtn = createNavButton("Analytics", "ANALYTICS", e -> navigateTo("ANALYTICS"));
        navPanel.add(analyticsBtn, "growx, wrap, gapbottom " + Spacing.SM);

        addSectionLabel("TOURNAMENT");
        tournamentsBtn = createNavButton("Tournaments", "TOURNAMENTS", e -> navigateTo("TOURNAMENTS"));
        navPanel.add(tournamentsBtn, "growx, wrap");
        teamsBtn = createNavButton("Teams", "TEAMS", e -> navigateTo("TEAMS"));
        navPanel.add(teamsBtn, "growx, wrap");
        playersBtn = createNavButton("Players", "PLAYERS", e -> navigateTo("PLAYERS"));
        navPanel.add(playersBtn, "growx, wrap");
        matchesBtn = createNavButton("Matches", "MATCHES", e -> navigateTo("MATCHES"));
        navPanel.add(matchesBtn, "growx, wrap");
        bracketsBtn = createNavButton("Brackets", "BRACKETS", e -> navigateTo("BRACKETS"));
        navPanel.add(bracketsBtn, "growx, wrap");
        standingsBtn = createNavButton("Standings", "STANDINGS", e -> navigateTo("STANDINGS"));
        navPanel.add(standingsBtn, "growx, wrap, gapbottom " + Spacing.SM);

        addSectionLabel("OPERATIONS");
        venuesBtn = createNavButton("Venues", "VENUES", e -> navigateTo("VENUES"));
        navPanel.add(venuesBtn, "growx, wrap");
        refereesBtn = createNavButton("Referees", "REFEREES", e -> navigateTo("REFEREES"));
        navPanel.add(refereesBtn, "growx, wrap");
        coachesBtn = createNavButton("Coaches", "COACHES", e -> navigateTo("COACHES"));
        navPanel.add(coachesBtn, "growx, wrap");
        staffBtn = createNavButton("Staff", "STAFF", e -> navigateTo("STAFF"));
        navPanel.add(staffBtn, "growx, wrap");
        equipmentBtn = createNavButton("Equipment", "EQUIPMENT", e -> navigateTo("EQUIPMENT"));
        navPanel.add(equipmentBtn, "growx, wrap, gapbottom " + Spacing.SM);

        addSectionLabel("FINANCE");
        sponsorsBtn = createNavButton("Sponsors", "SPONSORS", e -> navigateTo("SPONSORS"));
        navPanel.add(sponsorsBtn, "growx, wrap");
        prizePoolsBtn = createNavButton("Prize Pools", "PRIZE_POOLS", e -> navigateTo("PRIZE_POOLS"));
        navPanel.add(prizePoolsBtn, "growx, wrap, gapbottom " + Spacing.SM);

        addSectionLabel("SYSTEM");
        reportsBtn = createNavButton("Reports", "REPORTS", e -> navigateTo("REPORTS"));
        navPanel.add(reportsBtn, "growx, wrap");
        auditBtn = createNavButton("Audit", "AUDIT", e -> navigateTo("AUDIT"));
        navPanel.add(auditBtn, "growx, wrap");
        usersBtn = createNavButton("Users", "USERS", e -> navigateTo("USERS"));
        navPanel.add(usersBtn, "growx, wrap");
        settingsBtn = createNavButton("Settings", "SETTINGS", e -> navigateTo("SETTINGS"));
        navPanel.add(settingsBtn, "growx, wrap");

        scrollPane = new JScrollPane(navPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        scrollPane.setBackground(ThemeManager.getSidebarBackground());
        scrollPane.getViewport().setBackground(ThemeManager.getSidebarBackground());
        add(scrollPane, BorderLayout.CENTER);

        // ===== BOTTOM: Logout + Footer =====
        bottomPanel = new JPanel(new MigLayout("fill, insets " + Spacing.SM + " " + Spacing.MD + " " + Spacing.MD + " " + Spacing.MD, "[grow]", "[]2[]"));
        bottomPanel.setBackground(ThemeManager.getSidebarBackground());

        logoutBtn = createNavButton("Logout", "LOGOUT", e -> logout());
        logoutBtn.setForeground(ThemeManager.getDanger());
        bottomPanel.add(logoutBtn, "growx, wrap");

        JLabel footerLabel = new JLabel("ETMS © 2026");
        footerLabel.setFont(Typography.CAPTION);
        footerLabel.setForeground(ThemeManager.getTextMuted());
        footerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(footerLabel, "growx");

        add(bottomPanel, BorderLayout.SOUTH);

        // Default active
        setActiveButton(dashboardBtn);
    }

    private void addSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Typography.LABEL);
        label.setForeground(ThemeManager.getTextMuted());
        label.setBorder(BorderFactory.createEmptyBorder(Spacing.SM, Spacing.SM, Spacing.XS, Spacing.SM));
        navPanel.add(label, "growx, wrap");
    }

    // 🔥 Creates a flat button with NO hover, NO cursor change, NO background feedback
    private JButton createNavButton(String text, String panelName, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(Typography.NAVIGATION);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(Spacing.XS, Spacing.SM, Spacing.XS, Spacing.SM));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);       // no background on hover
        btn.setBorderPainted(false);           // no border
        btn.setCursor(Cursor.getDefaultCursor()); // no hand cursor
        btn.putClientProperty("panelName", panelName);
        btn.addActionListener(action);
        navButtons.add(btn);
        return btn;
    }

    private void navigateTo(String panelName) {
        // Reset all buttons to default style
        for (JButton btn : navButtons) {
            btn.setForeground(ThemeManager.getSidebarText());
            btn.setBorder(BorderFactory.createEmptyBorder(Spacing.XS, Spacing.SM, Spacing.XS, Spacing.SM));
            // keep content area false
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
        }

        // Find and highlight the active button
        for (JButton btn : navButtons) {
            Object name = btn.getClientProperty("panelName");
            if (name != null && name.toString().equals(panelName)) {
                setActiveButton(btn);
                break;
            }
        }

        activePanel = panelName;
        mainFrame.showPanel(panelName);
    }

    private void setActiveButton(JButton btn) {
        if (btn == null) return;
        btn.setForeground(ThemeManager.getAccent());
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, ThemeManager.getAccent()),
                BorderFactory.createEmptyBorder(Spacing.XS, Spacing.SM - 3, Spacing.XS, Spacing.SM)
        ));
        activeBtn = btn;
    }

    private void applyTheme() {
        Color bg = ThemeManager.getSidebarBackground();
        Color textColor = ThemeManager.getSidebarText();

        setBackground(bg);
        if (appTitle != null) appTitle.setForeground(ThemeManager.getAccent());

        if (scrollPane != null) {
            scrollPane.setBackground(bg);
            scrollPane.getViewport().setBackground(bg);
        }
        if (navPanel != null) navPanel.setBackground(bg);
        if (bottomPanel != null) {
            bottomPanel.setBackground(bg);
            for (Component comp : bottomPanel.getComponents()) {
                if (comp instanceof JLabel && ((JLabel) comp).getFont() == Typography.CAPTION) {
                    ((JLabel) comp).setForeground(ThemeManager.getTextMuted());
                }
            }
        }

        // Update section labels
        for (Component comp : navPanel.getComponents()) {
            if (comp instanceof JLabel && ((JLabel) comp).getFont() == Typography.LABEL) {
                ((JLabel) comp).setForeground(ThemeManager.getTextMuted());
            }
        }

        // Update buttons
        for (JButton btn : navButtons) {
            String text = btn.getText();
            Icon icon = null;
            switch (text) {
                case "Dashboard": icon = IconHelper.getDashboardIcon(textColor); break;
                case "Analytics": icon = IconHelper.getAnalyticsIcon(textColor); break;
                case "Tournaments": icon = IconHelper.getTournamentsIcon(textColor); break;
                case "Teams": icon = IconHelper.getTeamsIcon(textColor); break;
                case "Players": icon = IconHelper.getPlayersIcon(textColor); break;
                case "Matches": icon = IconHelper.getMatchesIcon(textColor); break;
                case "Brackets": icon = IconHelper.getBracketsIcon(textColor); break;
                case "Standings": icon = IconHelper.getStandingsIcon(textColor); break;
                case "Venues": icon = IconHelper.getVenuesIcon(textColor); break;
                case "Sponsors": icon = IconHelper.getSponsorsIcon(textColor); break;
                case "Prize Pools": icon = IconHelper.getPrizePoolsIcon(textColor); break;
                case "Referees": icon = IconHelper.getRefereesIcon(textColor); break;
                case "Equipment": icon = IconHelper.getEquipmentIcon(textColor); break;
                case "Coaches": icon = IconHelper.getCoachesIcon(textColor); break;
                case "Staff": icon = IconHelper.getStaffIcon(textColor); break;
                case "Reports": icon = IconHelper.getReportsIcon(textColor); break;
                case "Audit": icon = IconHelper.getAuditIcon(textColor); break;
                case "Users": icon = IconHelper.getUsersIcon(textColor); break;
                case "Settings": icon = IconHelper.getSettingsIcon(textColor); break;
                case "Logout": icon = IconHelper.getLogoutIcon(textColor); break;
                default: break;
            }
            if (icon != null) btn.setIcon(icon);
            btn.setForeground(textColor);
            btn.setBackground(bg);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
        }

        // Re-apply active state
        if (activeBtn != null) {
            activeBtn.setForeground(ThemeManager.getAccent());
            activeBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, ThemeManager.getAccent()),
                    BorderFactory.createEmptyBorder(Spacing.XS, Spacing.SM - 3, Spacing.XS, Spacing.SM)
            ));
        }

        if (logoutBtn != null) {
            logoutBtn.setForeground(ThemeManager.getDanger());
            logoutBtn.setBackground(bg);
        }
    }

    public void toggleSidebar() {
        expanded = !expanded;
        setPreferredSize(expanded ? new Dimension(220, 0) : new Dimension(50, 0));
        revalidate();
        repaint();
    }

    private void updateVisibility() {
        User user = UserSession.getCurrentUser();
        boolean isAdmin = user != null && user.isAdmin();
        boolean isOrganizer = user != null && user.isOrganizer();
        boolean isCoach = user != null && user.isCoach();

        dashboardBtn.setVisible(true);
        analyticsBtn.setVisible(isAdmin || isOrganizer);

        tournamentsBtn.setVisible(isAdmin || isOrganizer || isCoach);
        teamsBtn.setVisible(isAdmin || isOrganizer || isCoach);
        playersBtn.setVisible(isAdmin || isOrganizer || isCoach);
        matchesBtn.setVisible(isAdmin || isOrganizer || isCoach);
        bracketsBtn.setVisible(isAdmin || isOrganizer || isCoach);
        standingsBtn.setVisible(isAdmin || isOrganizer || isCoach);

        venuesBtn.setVisible(isAdmin || isOrganizer);
        refereesBtn.setVisible(isAdmin || isOrganizer);
        coachesBtn.setVisible(isAdmin || isOrganizer);
        staffBtn.setVisible(isAdmin || isOrganizer);
        equipmentBtn.setVisible(isAdmin || isOrganizer);

        sponsorsBtn.setVisible(isAdmin || isOrganizer);
        prizePoolsBtn.setVisible(isAdmin || isOrganizer);

        reportsBtn.setVisible(isAdmin || isOrganizer);
        auditBtn.setVisible(isAdmin);
        usersBtn.setVisible(isAdmin);
        settingsBtn.setVisible(isAdmin);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.setCurrentUser(null);
            SwingUtilities.getWindowAncestor(this).dispose();
            System.exit(0);
        }
    }

    public void refresh() {
        applyTheme();
        updateVisibility();
        revalidate();
        repaint();
    }
}