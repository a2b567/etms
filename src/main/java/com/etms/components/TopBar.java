package com.etms.components;

import com.etms.model.SearchResult;
import com.etms.service.SearchService;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import com.etms.util.IconHelper;
import com.etms.ui.components.ETMSButton;
import com.etms.view.MainFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class TopBar extends JPanel {
    private JLabel breadcrumbLabel;
    private JTextField searchField;
    private JLabel notificationLabel;
    private JButton userButton;
    private JPopupMenu userMenu;
    private JPopupMenu searchPopup;
    private SearchService searchService;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private MainFrame mainFrame;

    private ETMSButton menuToggleBtn;
    private ETMSButton themeToggleBtn;

    public TopBar(CardLayout cardLayout, JPanel contentPanel, MainFrame mainFrame) {
        this.cardLayout = cardLayout;
        this.contentPanel = contentPanel;
        this.mainFrame = mainFrame;
        this.searchService = new SearchService();
        initComponents();
        applyTheme();
    }

    private void initComponents() {
        setLayout(new MigLayout("insets 10 20", "[][grow][][][][][]", "[center]"));
        setBorder(BorderFactory.createMatteBorder(0,0,1,0, ThemeManager.getBorder()));

        menuToggleBtn = new ETMSButton("", ETMSButton.Variant.GHOST);
        menuToggleBtn.setIcon(IconHelper.getHamburgerIcon(ThemeManager.getText()));
        menuToggleBtn.setToolTipText("Toggle Sidebar");
        menuToggleBtn.addActionListener(e -> mainFrame.toggleSidebar());
        add(menuToggleBtn, "width 30!, height 30!");

        breadcrumbLabel = new JLabel("Dashboard");
        breadcrumbLabel.setFont(Typography.SECTION_TITLE);
        add(breadcrumbLabel, "growx");

        // Search field
        searchField = new JTextField();
        searchField.putClientProperty("JTextField.placeholderText", "Search...");
        searchField.setFont(Typography.BODY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            BorderFactory.createEmptyBorder(6,10,6,10)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { performSearch(); }
        });
        add(searchField, "width 220!");

        // Notification
        notificationLabel = new JLabel("0");
        notificationLabel.setFont(Typography.BODY);
        notificationLabel.setIcon(IconHelper.getNotificationIcon(ThemeManager.getTextSecondary()));
        notificationLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        add(notificationLabel);

        // User profile button with dropdown
        userButton = new JButton("👤 Administrator ▼");
        userButton.setFont(Typography.BODY);
        userButton.setForeground(ThemeManager.getText());
        userButton.setBackground(ThemeManager.getSurface());
        userButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        userButton.setFocusPainted(false);
        userButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        userButton.addActionListener(e -> showUserMenu());
        add(userButton);

        // User menu popup
        userMenu = new JPopupMenu();
        JMenuItem profileItem = new JMenuItem("Profile");
        profileItem.addActionListener(e -> JOptionPane.showMessageDialog(this, "Profile coming soon."));
        userMenu.add(profileItem);
        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.addActionListener(e -> mainFrame.showPanel("SETTINGS"));
        userMenu.add(settingsItem);
        userMenu.addSeparator();
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.addActionListener(e -> mainFrame.logout());
        userMenu.add(logoutItem);

        // Theme toggle
        themeToggleBtn = new ETMSButton("Dark", ETMSButton.Variant.SECONDARY);
        themeToggleBtn.setFont(Typography.BUTTON);
        themeToggleBtn.addActionListener(e -> {
            ThemeManager.toggleTheme();
            themeToggleBtn.setText(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "Dark" : "Light");
            mainFrame.refreshTheme();
        });
        add(themeToggleBtn, "width 70!");

        searchPopup = new JPopupMenu();
        themeToggleBtn.setText(ThemeManager.getCurrentTheme() == ThemeManager.Theme.LIGHT ? "Dark" : "Light");
        applyTheme();
    }

    private void showUserMenu() {
        userMenu.show(userButton, 0, userButton.getHeight());
    }

    private void applyTheme() {
        setBackground(ThemeManager.getSurface());
        breadcrumbLabel.setForeground(ThemeManager.getText());
        userButton.setForeground(ThemeManager.getText());
        userButton.setBackground(ThemeManager.getSurface());
        notificationLabel.setForeground(ThemeManager.getTextSecondary());
        notificationLabel.setIcon(IconHelper.getNotificationIcon(ThemeManager.getTextSecondary()));
        menuToggleBtn.setIcon(IconHelper.getHamburgerIcon(ThemeManager.getText()));
        menuToggleBtn.refreshTheme();
        themeToggleBtn.refreshTheme();
        searchField.setBackground(ThemeManager.getSurface());
        searchField.setForeground(ThemeManager.getText());
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            BorderFactory.createEmptyBorder(6,10,6,10)
        ));
        setBorder(BorderFactory.createMatteBorder(0,0,1,0, ThemeManager.getBorder()));
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) { searchPopup.setVisible(false); return; }
        List<SearchResult> results = searchService.search(query);
        searchPopup.removeAll();
        if (results.isEmpty()) {
            JMenuItem item = new JMenuItem("No results");
            item.setEnabled(false);
            searchPopup.add(item);
        } else {
            for (SearchResult r : results) {
                JMenuItem item = new JMenuItem(r.getType() + ": " + r.getDisplayText());
                item.addActionListener(e -> {
                    searchField.setText("");
                    searchPopup.setVisible(false);
                    mainFrame.showPanel(r.getPanelName());
                });
                searchPopup.add(item);
            }
        }
        searchPopup.pack();
        searchPopup.show(searchField, 0, searchField.getHeight());
    }

    public void setBreadcrumb(String text) { breadcrumbLabel.setText(text); }
    public void setNotificationCount(int count) { notificationLabel.setText(String.valueOf(count)); }
    public void setUser(String username) { userButton.setText("👤 " + username + " ▼"); }

    public void refresh() {
        applyTheme();
        revalidate();
        repaint();
    }
}