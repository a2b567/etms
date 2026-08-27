package com.etms.view;

import com.etms.dao.GameDAO;
import com.etms.model.Game;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import com.etms.theme.Spacing;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class GameManagementPanel extends JPanel {

    private final GameDAO gameDAO = new GameDAO();
    private JTable gameTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, playerCountField, descField;

    public GameManagementPanel() {
        setBackground(ThemeManager.getBackground());
        initComponents();
        loadGames();
    }

    private void initComponents() {
        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));

        JLabel title = new JLabel("Game Management");
        title.setFont(Typography.PAGE_TITLE);
        title.setForeground(ThemeManager.getText());
        add(title, "wrap, gapbottom 20");

        JPanel formPanel = new JPanel(new MigLayout("wrap 2, fillx", "[right][grow]", "[]5[]"));
        formPanel.setBackground(ThemeManager.getSurface());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            "Add New Game",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            Typography.CARD_TITLE,
            ThemeManager.getText()
        ));

        formPanel.add(new JLabel("Game Name:"));
        nameField = new JTextField(20);
        nameField.setBackground(ThemeManager.getSurface());
        nameField.setForeground(ThemeManager.getText());
        nameField.setCaretColor(ThemeManager.getAccent());
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            BorderFactory.createEmptyBorder(Spacing.SM, Spacing.MD, Spacing.SM, Spacing.MD)
        ));
        formPanel.add(nameField, "growx");

        formPanel.add(new JLabel("Default Players per Team:"));
        playerCountField = new JTextField(5);
        playerCountField.setBackground(ThemeManager.getSurface());
        playerCountField.setForeground(ThemeManager.getText());
        playerCountField.setCaretColor(ThemeManager.getAccent());
        playerCountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            BorderFactory.createEmptyBorder(Spacing.SM, Spacing.MD, Spacing.SM, Spacing.MD)
        ));
        formPanel.add(playerCountField, "growx");

        formPanel.add(new JLabel("Description:"));
        descField = new JTextField(30);
        descField.setBackground(ThemeManager.getSurface());
        descField.setForeground(ThemeManager.getText());
        descField.setCaretColor(ThemeManager.getAccent());
        descField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            BorderFactory.createEmptyBorder(Spacing.SM, Spacing.MD, Spacing.SM, Spacing.MD)
        ));
        formPanel.add(descField, "growx");

        JButton addBtn = new JButton("Add Game");
        addBtn.setFont(Typography.BUTTON);
        addBtn.setBackground(ThemeManager.getAccent());
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> addGame());
        formPanel.add(addBtn, "span 2, align center, gaptop 10");

        add(formPanel, "growx, wrap, gapbottom 20");

        JPanel listPanel = new JPanel(new MigLayout("fill", "[grow]", "[][grow]"));
        listPanel.setBackground(ThemeManager.getSurface());
        listPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorder()),
            "Games List",
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            Typography.CARD_TITLE,
            ThemeManager.getText()
        ));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setFont(Typography.BUTTON);
        refreshBtn.setBackground(ThemeManager.getSurface());
        refreshBtn.setForeground(ThemeManager.getText());
        refreshBtn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
        refreshBtn.addActionListener(e -> loadGames());
        listPanel.add(refreshBtn, "wrap");

        String[] cols = {"ID", "Name", "Players/Team", "Description"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        gameTable = new JTable(tableModel);
        gameTable.setBackground(ThemeManager.getSurface());
        gameTable.setForeground(ThemeManager.getText());
        gameTable.setFont(Typography.BODY);
        gameTable.getTableHeader().setBackground(ThemeManager.getSurface());
        gameTable.getTableHeader().setForeground(ThemeManager.getTextSecondary());
        gameTable.setGridColor(ThemeManager.getBorder());
        gameTable.setRowHeight(Spacing.XXL + Spacing.SM);
        JScrollPane scrollPane = new JScrollPane(gameTable);
        scrollPane.getViewport().setBackground(ThemeManager.getSurface());
        listPanel.add(scrollPane, "grow");

        JButton deleteBtn = new JButton("Delete Selected");
        deleteBtn.setFont(Typography.BUTTON);
        deleteBtn.setBackground(ThemeManager.getDanger());
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> deleteGame());
        listPanel.add(deleteBtn, "growx, gaptop 10");

        add(listPanel, "grow");
    }

    private void loadGames() {
        tableModel.setRowCount(0);
        try {
            List<Game> games = gameDAO.getAllGames();
            for (Game g : games) {
                tableModel.addRow(new Object[]{
                    g.getGameId(), g.getName(), g.getDefaultPlayerCount(), g.getDescription()
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void addGame() {
        String name = nameField.getText().trim();
        int count;
        try {
            count = Integer.parseInt(playerCountField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid player count.");
            return;
        }
        String desc = descField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.");
            return;
        }

        try {
            Game g = new Game(name, count, desc);
            if (gameDAO.addGame(g)) {
                loadGames();
                clearForm();
                JOptionPane.showMessageDialog(this, "Game added.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add game (name may already exist).");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error.");
        }
    }

    private void deleteGame() {
        int row = gameTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a game to delete.");
            return;
        }
        int gameId = (int) tableModel.getValueAt(row, 0);
        try {
            if (gameDAO.deleteGame(gameId)) {
                loadGames();
                JOptionPane.showMessageDialog(this, "Game deleted.");
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void clearForm() {
        nameField.setText("");
        playerCountField.setText("");
        descField.setText("");
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
            if (comp instanceof JTextField) {
                JTextField tf = (JTextField) comp;
                tf.setBackground(ThemeManager.getSurface());
                tf.setForeground(ThemeManager.getText());
                tf.setCaretColor(ThemeManager.getAccent());
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.getBorder()),
                    BorderFactory.createEmptyBorder(Spacing.SM, Spacing.MD, Spacing.SM, Spacing.MD)
                ));
            }
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String text = btn.getText();
                if (text != null && text.equals("Add Game")) {
                    btn.setBackground(ThemeManager.getAccent());
                    btn.setForeground(Color.WHITE);
                } else if (text != null && text.equals("Delete Selected")) {
                    btn.setBackground(ThemeManager.getDanger());
                    btn.setForeground(Color.WHITE);
                } else {
                    btn.setBackground(ThemeManager.getSurface());
                    btn.setForeground(ThemeManager.getText());
                    btn.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorder()));
                }
            }
        }

        if (gameTable != null) {
            gameTable.setBackground(ThemeManager.getSurface());
            gameTable.setForeground(ThemeManager.getText());
            gameTable.setGridColor(ThemeManager.getBorder());
            gameTable.getTableHeader().setBackground(ThemeManager.getSurface());
            gameTable.getTableHeader().setForeground(ThemeManager.getTextSecondary());
        }

        revalidate();
        repaint();
    }
}