package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.model.Tournament;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import com.etms.ui.components.ETMSButton;
import com.etms.ui.components.ETMSPageHeader;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BracketsPanel extends JPanel {

    private final DashboardController controller;
    private JComboBox<String> tournamentCombo;
    private List<Tournament> tournaments;
    private JPanel bracketContainer;
    private ETMSButton refreshBtn; // declared

    public BracketsPanel(DashboardController controller) {
        this.controller = controller;
        setBackground(ThemeManager.getBackground());
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][][grow]"));
        initComponents();
        loadTournaments();
    }

    private void initComponents() {
        ETMSPageHeader header = new ETMSPageHeader("Brackets", "Tournament bracket visualization.");
        add(header, "growx, wrap, gapbottom " + Spacing.LG);

        JPanel filterPanel = new JPanel(new MigLayout("fillx, insets 0", "[][grow][]", "[]"));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Tournament:"));
        tournamentCombo = new JComboBox<>();
        tournamentCombo.setFont(Typography.BODY);
        tournamentCombo.setBackground(ThemeManager.getSurface());
        tournamentCombo.setForeground(ThemeManager.getText());
        tournamentCombo.addActionListener(e -> updateBracket());
        filterPanel.add(tournamentCombo, "growx, width 250!");

        refreshBtn = new ETMSButton("Refresh", ETMSButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadTournaments());
        filterPanel.add(refreshBtn);

        add(filterPanel, "growx, wrap, gapbottom " + Spacing.LG);

        bracketContainer = new JPanel(new BorderLayout());
        bracketContainer.setBackground(ThemeManager.getBackground());
        add(bracketContainer, "grow");
    }

    private void loadTournaments() {
        tournamentCombo.removeAllItems();
        tournaments = controller.getAllTournaments();
        for (Tournament t : tournaments) {
            tournamentCombo.addItem(t.getTournamentName() + " (ID:" + t.getTournamentId() + ")");
        }
        if (!tournaments.isEmpty()) updateBracket();
    }

    private void updateBracket() {
        int idx = tournamentCombo.getSelectedIndex();
        if (idx < 0 || tournaments.isEmpty()) return;
        int tournamentId = tournaments.get(idx).getTournamentId();
        bracketContainer.removeAll();
        BracketPanel bracketPanel = new BracketPanel(controller, tournamentId);
        bracketContainer.add(new JScrollPane(bracketPanel), BorderLayout.CENTER);
        bracketContainer.revalidate();
        bracketContainer.repaint();
    }

    public void refreshTheme() {
        setBackground(ThemeManager.getBackground());

        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                ((JPanel) comp).setBackground(ThemeManager.getBackground());
            }
            if (comp instanceof JScrollPane) {
                JScrollPane scroll = (JScrollPane) comp;
                scroll.setBackground(ThemeManager.getBackground());
                scroll.getViewport().setBackground(ThemeManager.getSurface());
            }
            if (comp instanceof JComboBox) {
                ((JComboBox<?>) comp).setBackground(ThemeManager.getSurface());
                ((JComboBox<?>) comp).setForeground(ThemeManager.getText());
            }
            if (comp instanceof JLabel) {
                ((JLabel) comp).setForeground(ThemeManager.getText());
            }
        }

        if (refreshBtn != null) refreshBtn.refreshTheme();
        if (bracketContainer != null) {
            bracketContainer.setBackground(ThemeManager.getBackground());
            for (Component child : bracketContainer.getComponents()) {
                if (child instanceof JPanel) {
                    ((JPanel) child).setBackground(ThemeManager.getBackground());
                }
                if (child instanceof JScrollPane) {
                    JScrollPane scroll = (JScrollPane) child;
                    scroll.setBackground(ThemeManager.getBackground());
                    scroll.getViewport().setBackground(ThemeManager.getSurface());
                }
            }
        }

        revalidate();
        repaint();
    }
}