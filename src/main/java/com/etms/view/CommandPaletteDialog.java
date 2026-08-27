package com.etms.view;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class CommandPaletteDialog extends JDialog {

    private JTextField searchField;
    private JList<String> resultList;
    private DefaultListModel<String> listModel;
    private List<Command> allCommands;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JFrame owner; // store reference

    public CommandPaletteDialog(JFrame owner, CardLayout cardLayout, JPanel contentPanel) {
        super(owner, "Command Palette", false);
        this.owner = owner;            // save owner
        this.cardLayout = cardLayout;
        this.contentPanel = contentPanel;
        setUndecorated(true);
        initCommands();
        initComponents();
    }

    private void initCommands() {
        allCommands = new ArrayList<>();
        allCommands.add(new Command("Dashboard", () -> cardLayout.show(contentPanel, "DASHBOARD")));
        allCommands.add(new Command("Tournaments", () -> cardLayout.show(contentPanel, "TOURNAMENTS")));
        allCommands.add(new Command("Teams", () -> cardLayout.show(contentPanel, "TEAMS")));
        allCommands.add(new Command("Players", () -> cardLayout.show(contentPanel, "PLAYERS")));
        allCommands.add(new Command("Matches", () -> cardLayout.show(contentPanel, "MATCHES")));
        allCommands.add(new Command("Financial", () -> cardLayout.show(contentPanel, "FINANCIAL")));
        allCommands.add(new Command("Sponsors", () -> cardLayout.show(contentPanel, "SPONSORS")));
        allCommands.add(new Command("Venues", () -> cardLayout.show(contentPanel, "VENUES")));
        allCommands.add(new Command("Equipment", () -> cardLayout.show(contentPanel, "EQUIPMENT")));
        allCommands.add(new Command("Notifications", () -> cardLayout.show(contentPanel, "NOTIFICATIONS")));
        allCommands.add(new Command("Audit Trail", () -> cardLayout.show(contentPanel, "AUDIT")));
        allCommands.add(new Command("Backup & Restore", () -> cardLayout.show(contentPanel, "BACKUP")));
        allCommands.add(new Command("QR Check‑In", () -> cardLayout.show(contentPanel, "CHECKIN")));
        allCommands.add(new Command("Reports", () -> cardLayout.show(contentPanel, "REPORTS")));
        allCommands.add(new Command("Games", () -> cardLayout.show(contentPanel, "GAMES")));
        allCommands.add(new Command("New Tournament", () -> cardLayout.show(contentPanel, "TOURNAMENTS")));
        allCommands.add(new Command("Register Team", () -> cardLayout.show(contentPanel, "TEAMS")));
        allCommands.add(new Command("Add Player", () -> cardLayout.show(contentPanel, "PLAYERS")));
        allCommands.add(new Command("Schedule Match", () -> cardLayout.show(contentPanel, "MATCHES")));
        allCommands.add(new Command("Logout", () -> {
            dispose();
            owner.dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }));
        allCommands.add(new Command("Exit", () -> System.exit(0)));
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterCommands(searchField.getText().trim());
            }
        });
        searchField.addActionListener(e -> executeSelected());
        add(searchField, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        resultList = new JList<>(listModel);
        resultList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    executeSelected();
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(resultList);
        add(scrollPane, BorderLayout.CENTER);

        filterCommands(""); // show all initially

        setSize(500, 400);
        setLocationRelativeTo(getOwner());
    }

    private void filterCommands(String query) {
        listModel.clear();
        for (Command cmd : allCommands) {
            if (cmd.name.toLowerCase().contains(query.toLowerCase())) {
                listModel.addElement(cmd.name);
            }
        }
    }

    private void executeSelected() {
        int idx = resultList.getSelectedIndex();
        if (idx == -1 && listModel.size() > 0) {
            resultList.setSelectedIndex(0);
            idx = 0;
        }
        if (idx != -1) {
            String selectedName = listModel.getElementAt(idx);
            for (Command cmd : allCommands) {
                if (cmd.name.equals(selectedName)) {
                    dispose();
                    cmd.action.run();
                    break;
                }
            }
        }
    }

    private static class Command {
        String name;
        Runnable action;

        Command(String name, Runnable action) {
            this.name = name;
            this.action = action;
        }
    }
}