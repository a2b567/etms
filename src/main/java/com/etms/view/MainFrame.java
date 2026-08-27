package com.etms.view;

import com.etms.components.Sidebar;
import com.etms.components.TopBar;
import com.etms.controller.DashboardController;
import com.etms.model.User;
import com.etms.service.UserSession;
import com.etms.theme.ThemeManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Method;

public class MainFrame extends JFrame {

    private final DashboardController dashboardController = new DashboardController();
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private Sidebar sidebar;
    private TopBar topBar;
    private DashboardContainer dashboardContainer;
    private JPanel mainContentPanel;

    public MainFrame() {
        System.out.println(">>> NEW MainFrame loaded!");
        initComponents();
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("ETMS - Esports Tournament Management System");
        applyTheme();
        User current = UserSession.getCurrentUser();
        topBar.setUser(current != null ? current.getUsername() : "Guest");
        topBar.setNotificationCount(dashboardController.getNotificationCount());
        setFrameIcon();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ThemeManager.getBackground());

        topBar = new TopBar(cardLayout, contentPanel, this);
        add(topBar, BorderLayout.NORTH);

        dashboardContainer = new DashboardContainer(
                dashboardController,
                () -> showPanel("TOURNAMENTS"),
                () -> showPanel("TEAMS"),
                () -> showPanel("PLAYERS"),
                () -> showPanel("MATCHES")
        );
        contentPanel.add(dashboardContainer, "DASHBOARD");

        // ===== ALL PANELS =====
        contentPanel.add(new TournamentPanel(dashboardController), "TOURNAMENTS");
        contentPanel.add(new TeamPanel(dashboardController), "TEAMS");
        contentPanel.add(new PlayerPanel(dashboardController), "PLAYERS");
        contentPanel.add(new MatchPanel(dashboardController), "MATCHES");
        contentPanel.add(new BracketsPanel(dashboardController), "BRACKETS");
        contentPanel.add(new StandingsPanel(dashboardController), "STANDINGS");
        contentPanel.add(new VenuePanel(dashboardController), "VENUES");
        contentPanel.add(new RefereePanel(dashboardController), "REFEREES");
        contentPanel.add(new CoachPanel(dashboardController), "COACHES");
        contentPanel.add(new StaffPanel(dashboardController), "STAFF");
        contentPanel.add(new EquipmentPanel(dashboardController), "EQUIPMENT");
        contentPanel.add(new SponsorPanel(dashboardController), "SPONSORS");
        contentPanel.add(new PrizePoolPanel(dashboardController), "PRIZE_POOLS");
        contentPanel.add(new AnalyticsPanel(dashboardController), "ANALYTICS");
        contentPanel.add(new AuditPanel(dashboardController), "AUDIT");
        contentPanel.add(new UsersPanel(dashboardController), "USERS");
        contentPanel.add(new SettingsPanel(dashboardController), "SETTINGS");
        contentPanel.add(new ReportingPanel(dashboardController), "REPORTS");

        // Additional panels (if they exist)
        // contentPanel.add(new CalendarPanel(), "CALENDAR");
        // contentPanel.add(new CheckInPanel(), "CHECKIN");
        // contentPanel.add(new FinancialPanel(dashboardController), "FINANCIAL");
        // contentPanel.add(new GameManagementPanel(), "GAMEMANAGEMENT");
        // contentPanel.add(new NotificationPanel(dashboardController), "NOTIFICATIONS");

        mainContentPanel = new JPanel(new BorderLayout());

        sidebar = new Sidebar(cardLayout, contentPanel, this);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setMinimumSize(new Dimension(220, 0));
        mainContentPanel.add(sidebar, BorderLayout.WEST);
        mainContentPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);

        showPanel("DASHBOARD");
        topBar.setBreadcrumb("Dashboard");
    }

    private void applyTheme() {
        mainContentPanel.setBackground(ThemeManager.getBackground());
        contentPanel.setBackground(ThemeManager.getBackground());
        getContentPane().setBackground(ThemeManager.getBackground());
    }

    private void setFrameIcon() {
        try {
            Image icon = Toolkit.getDefaultToolkit().getImage(
                    MainFrame.class.getResource("/icons/app_icon.png")
            );
            if (icon != null) {
                setIconImage(icon);
            }
        } catch (Exception ignored) {
            // Icon not found – keep default.
        }
    }

    public void toggleSidebar() {
        sidebar.toggleSidebar();
    }

    /**
     * Refreshes the entire UI when the theme changes.
     * Propagates the new theme to all child panels and components.
     */
    public void refreshTheme() {
        applyTheme();

        // Refresh known components directly
        topBar.refresh();
        sidebar.refresh();
        dashboardContainer.refreshTheme();

        // Refresh all panels via reflection
        refreshAllPanels();

        // 🔥 AGGRESSIVE: Recursively update all components in contentPanel
        refreshAllComponents(contentPanel);

        revalidate();
        repaint();
    }

    /**
     * Uses reflection to call refreshTheme() on every child panel of contentPanel.
     */
    private void refreshAllPanels() {
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JPanel) {
                try {
                    Method method = comp.getClass().getMethod("refreshTheme");
                    method.invoke(comp);
                } catch (Exception e) {
                    // Ignore – panel doesn't have refreshTheme()
                }
            }
        }
    }

    /**
     * 🔥 Recursively updates background of ALL containers, tables, scroll panes, and viewports.
     * This ensures NO white areas remain.
     */
    private void refreshAllComponents(Container container) {
        for (Component comp : container.getComponents()) {
            // Update JPanel
            if (comp instanceof JPanel) {
                ((JPanel) comp).setBackground(ThemeManager.getBackground());
            }

            // Update JScrollPane and its viewport
            if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                scroll.setBackground(ThemeManager.getBackground());
                scroll.getViewport().setBackground(ThemeManager.getSurface());
                // Also refresh the viewport's view (which might be a table or panel)
                Component view = scroll.getViewport().getView();
                if (view != null) {
                    if (view instanceof JPanel) {
                        ((JPanel) view).setBackground(ThemeManager.getSurface());
                    }
                    if (view instanceof JTable) {
                        // If it's our custom table, call refreshTheme; otherwise just set background
                        try {
                            Method m = view.getClass().getMethod("refreshTheme");
                            m.invoke(view);
                        } catch (Exception e) {
                            view.setBackground(ThemeManager.getSurface());
                            view.setForeground(ThemeManager.getText());
                        }
                    }
                }
            }

            // Update JTable directly
            if (comp instanceof JTable) {
                JTable table = (JTable) comp;
                try {
                    Method m = table.getClass().getMethod("refreshTheme");
                    m.invoke(table);
                } catch (Exception e) {
                    table.setBackground(ThemeManager.getSurface());
                    table.setForeground(ThemeManager.getText());
                    table.setGridColor(ThemeManager.getBorder());
                }
            }

            // Recursively process children
            if (comp instanceof Container) {
                refreshAllComponents((Container) comp);
            }
        }
    }

    public void showPanel(String panelName) {
        User user = UserSession.getCurrentUser();
        if (user == null) {
            panelName = "DASHBOARD";
        }

        System.out.println(">>> showPanel called with: " + panelName);

        switch (panelName) {
            case "DASHBOARD":
            case "NOTIFICATIONS":
            case "CALENDAR":
                break;

            case "TOURNAMENTS":
            case "TEAMS":
            case "PLAYERS":
            case "MATCHES":
            case "BRACKETS":
            case "STANDINGS":
                if (!(user.isAdmin() || user.isOrganizer() || user.isCoach())) {
                    showAccessDenied();
                    return;
                }
                break;

            case "FINANCIAL":
            case "SPONSORS":
            case "VENUES":
            case "EQUIPMENT":
                if (!(user.isAdmin() || user.isOrganizer())) {
                    showAccessDenied();
                    return;
                }
                break;

            case "AUDIT":
            case "USERS":
            case "SETTINGS":
            case "BACKUP":
                if (!user.isAdmin()) {
                    showAccessDenied();
                    return;
                }
                break;

            case "REFEREES":
            case "STAFF":
            case "COACHES":
                if (!(user.isAdmin() || user.isOrganizer())) {
                    showAccessDenied();
                    return;
                }
                break;

            case "ANALYTICS":
            case "PRIZE_POOLS":
                if (!(user.isAdmin() || user.isOrganizer())) {
                    showAccessDenied();
                    return;
                }
                break;

            default:
                break;
        }

        try {
            cardLayout.show(contentPanel, panelName);
            topBar.setBreadcrumb(panelName.substring(0, 1).toUpperCase() + panelName.substring(1).toLowerCase());
        } catch (Exception e) {
            System.err.println("Error showing panel: " + panelName);
            e.printStackTrace();
        }
    }

    private void showAccessDenied() {
        JOptionPane.showMessageDialog(this, "You do not have permission to view this section.", "Access Denied", JOptionPane.ERROR_MESSAGE);
    }

    public void refreshDashboard() {
        if (dashboardContainer != null) {
            dashboardContainer.refreshDashboard();
        }
    }

    public void logout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.setCurrentUser(null);
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            User testUser = new User();
            testUser.setUsername("Admin");
            testUser.setRole("ADMIN");
            UserSession.setCurrentUser(testUser);

            MainFrame frame = new MainFrame();
            try {
                Image icon = Toolkit.getDefaultToolkit().getImage(
                        MainFrame.class.getResource("/icons/app_icon.png")
                );
                if (icon != null) frame.setIconImage(icon);
            } catch (Exception ignored) {}
            frame.setVisible(true);
        });
    }
}