package com.etms.view;

import com.etms.dao.TeamDAO;
import com.etms.model.Player;
import com.etms.model.Team;
import com.etms.theme.ThemeManager;
import com.etms.theme.Typography;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PlayerEditDialog extends JDialog {

    private JTextField firstNameField, lastNameField, inGameNameField, rankField, roleField;
    private JComboBox<String> teamCombo;
    private List<Team> teams;
    private boolean confirmed = false;
    private Player originalPlayer;
    private Player updatedPlayer;

    public PlayerEditDialog(JFrame parent, Player player) {
        super(parent, player == null ? "Add Player" : "Edit Player", true);
        this.originalPlayer = player;

        // Create new player for Add mode, or copy for Edit
        if (player == null) {
            this.updatedPlayer = new Player();
            this.updatedPlayer.setStatus("ACTIVE");
        } else {
            this.updatedPlayer = new Player();
            copyPlayer(player, this.updatedPlayer);
        }

        loadTeams();
        initComponents();
        populateFields();
        pack();
        setLocationRelativeTo(parent);
        setMinimumSize(new Dimension(450, 400));
    }

    private void copyPlayer(Player from, Player to) {
        if (from == null) return;
        to.setPlayerId(from.getPlayerId());
        to.setPersonId(from.getPersonId());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setInGameName(from.getInGameName());
        to.setGameRank(from.getGameRank() != null ? from.getGameRank() : "Unranked");
        to.setGameRole(from.getGameRole() != null ? from.getGameRole() : "Flex");
        to.setTeamId(from.getTeamId());
        to.setStatus(from.getStatus() != null ? from.getStatus() : "ACTIVE");
    }

    private void loadTeams() {
        try {
            teams = new TeamDAO().getAllTeams();
        } catch (SQLException e) {
            e.printStackTrace();
            teams = List.of();
        }
    }

    private void initComponents() {
        setLayout(new MigLayout("wrap 2, fillx, insets 20", "[right][200:300:]", "[]10[]"));
        getContentPane().setBackground(ThemeManager.getBackground());

        // First Name
        add(new JLabel("First Name *:"));
        firstNameField = new JTextField(20);
        add(firstNameField, "growx");

        // Last Name
        add(new JLabel("Last Name *:"));
        lastNameField = new JTextField(20);
        add(lastNameField, "growx");

        // In-Game Name
        add(new JLabel("In-Game Name *:"));
        inGameNameField = new JTextField(20);
        add(inGameNameField, "growx");

        // Rank
        add(new JLabel("Rank:"));
        rankField = new JTextField(20);
        add(rankField, "growx");

        // Role
        add(new JLabel("Role:"));
        roleField = new JTextField(20);
        add(roleField, "growx");

        // Team
        add(new JLabel("Team:"));
        teamCombo = new JComboBox<>();
        teamCombo.addItem("-- No Team --");
        for (Team t : teams) {
            teamCombo.addItem(t.getTeamName() + " (ID:" + t.getTeamId() + ")");
        }
        add(teamCombo, "growx");

        // Buttons
        JPanel buttonPanel = new JPanel(new MigLayout("align right"));
        buttonPanel.setOpaque(false);

        JButton saveBtn = new JButton("Save");
        saveBtn.setBackground(new Color(0, 120, 215));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> saveChanges());
        buttonPanel.add(saveBtn);

        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());
        buttonPanel.add(cancelBtn, "gapleft 10");

        add(buttonPanel, "span 2, align right, gaptop 10");
    }

    private void populateFields() {
        if (originalPlayer == null) {
            // New player – empty fields
            firstNameField.setText("");
            lastNameField.setText("");
            inGameNameField.setText("");
            rankField.setText("");
            roleField.setText("");
            teamCombo.setSelectedIndex(0);
            return;
        }

        // Edit mode – populate from existing player
        firstNameField.setText(originalPlayer.getFirstName() != null ? originalPlayer.getFirstName() : "");
        lastNameField.setText(originalPlayer.getLastName() != null ? originalPlayer.getLastName() : "");
        inGameNameField.setText(originalPlayer.getInGameName() != null ? originalPlayer.getInGameName() : "");
        rankField.setText(originalPlayer.getGameRank() != null ? originalPlayer.getGameRank() : "");
        roleField.setText(originalPlayer.getGameRole() != null ? originalPlayer.getGameRole() : "");

        // Set team
        if (originalPlayer.getTeamId() > 0) {
            for (int i = 0; i < teams.size(); i++) {
                if (teams.get(i).getTeamId() == originalPlayer.getTeamId()) {
                    teamCombo.setSelectedIndex(i + 1); // +1 because first item is "-- No Team --"
                    break;
                }
            }
        } else {
            teamCombo.setSelectedIndex(0);
        }
    }

    private void saveChanges() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String inGameName = inGameNameField.getText().trim();
        String rank = rankField.getText().trim();
        String role = roleField.getText().trim();

        // Validation
        if (firstName.isEmpty() || lastName.isEmpty() || inGameName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name, Last Name, and In-Game Name are required.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        updatedPlayer.setFirstName(firstName);
        updatedPlayer.setLastName(lastName);
        updatedPlayer.setInGameName(inGameName);
        updatedPlayer.setGameRank(rank.isEmpty() ? "Unranked" : rank);
        updatedPlayer.setGameRole(role.isEmpty() ? "Flex" : role);

        // Set team
        int teamIdx = teamCombo.getSelectedIndex();
        if (teamIdx > 0) {
            Team selected = teams.get(teamIdx - 1);
            updatedPlayer.setTeamId(selected.getTeamId());
        } else {
            updatedPlayer.setTeamId(0);
        }

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Player getUpdatedPlayer() {
        return updatedPlayer;
    }
}