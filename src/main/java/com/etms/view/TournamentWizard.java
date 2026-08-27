package com.etms.view;

import com.etms.controller.DashboardController;
import com.etms.dao.GameDAO;
import com.etms.dao.VenueDAO;
import com.etms.model.Game;
import com.etms.model.User;
import com.etms.model.Venue;
import com.etms.service.UserSession;
import com.etms.theme.ColorPalette;
import com.etms.theme.Typography;
import com.etms.util.ValidationUtil;
import com.etms.ui.components.ETMSButton;  // ← Use ETMSButton
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Full multi‑step wizard for creating a tournament.
 * ENCAPSULATION: Step data stored in private map.
 * ABSTRACTION: Wizard hides step implementation details.
 */
public class TournamentWizard extends JDialog {
    private final DashboardController controller;
    private CardLayout cardLayout;
    private JPanel stepsPanel;
    private ETMSButton nextBtn, backBtn, cancelBtn;  // ← Use ETMSButton
    private int currentStep = 0;
    private Map<String, JComponent> fields = new LinkedHashMap<>();

    public TournamentWizard(JFrame parent, DashboardController controller) {
        super(parent, "Create Tournament", true);
        this.controller = controller;
        setSize(700, 550);
        setLocationRelativeTo(parent);
        setBackground(ColorPalette.LIGHT_BG);
        initComponents();
        showStep(0);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));

        cardLayout = new CardLayout();
        stepsPanel = new JPanel(cardLayout);
        stepsPanel.setBackground(ColorPalette.LIGHT_BG);
        stepsPanel.add(createBasicInfoStep(), "0");
        stepsPanel.add(createGameStep(), "1");
        stepsPanel.add(createFormatStep(), "2");
        stepsPanel.add(createVenueStep(), "3");
        stepsPanel.add(createRegistrationStep(), "4");
        stepsPanel.add(createPrizePoolStep(), "5");
        stepsPanel.add(createReviewStep(), "6");
        add(stepsPanel, BorderLayout.CENTER);

        JPanel navPanel = new JPanel(new MigLayout("insets 10", "[grow][]20[]20[]", "[center]"));
        navPanel.setBackground(ColorPalette.LIGHT_SURFACE);

        // Use ETMSButton instead of AppButton
        backBtn = new ETMSButton("< Back", ETMSButton.Variant.SECONDARY);
        backBtn.addActionListener(e -> goBack());
        navPanel.add(backBtn, "align left");

        nextBtn = new ETMSButton("Next >", ETMSButton.Variant.PRIMARY);
        nextBtn.addActionListener(e -> goNext());
        navPanel.add(nextBtn, "align right");

        cancelBtn = new ETMSButton("Cancel", ETMSButton.Variant.GHOST);
        cancelBtn.addActionListener(e -> dispose());
        navPanel.add(cancelBtn, "align right");

        // Ensure visibility (sometimes needed)
        backBtn.setVisible(true);
        nextBtn.setVisible(true);
        cancelBtn.setVisible(true);

        add(navPanel, BorderLayout.SOUTH);
    }

    // ---------- Step 1: Basic Info ----------
    private JPanel createBasicInfoStep() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 30", "[right][300:400:]", "[]15[]"));
        panel.setBackground(ColorPalette.LIGHT_BG);
        panel.add(createStepTitle("Step 1: Basic Information"), "span 2, wrap");

        panel.add(new JLabel("Tournament Name *:"));
        JTextField nameField = new JTextField();
        nameField.setFont(Typography.BODY);
        fields.put("name", nameField);
        panel.add(nameField, "growx");

        panel.add(new JLabel("Start Date (YYYY-MM-DD) *:"));
        JTextField startField = new JTextField();
        startField.setFont(Typography.BODY);
        fields.put("start", startField);
        panel.add(startField, "growx");

        panel.add(new JLabel("End Date (YYYY-MM-DD) (optional):"));
        JTextField endField = new JTextField();
        endField.setFont(Typography.BODY);
        fields.put("end", endField);
        panel.add(endField, "growx");

        return panel;
    }

    // ---------- Step 2: Game ----------
    private JPanel createGameStep() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 30", "[right][300:400:]", "[]15[]"));
        panel.setBackground(ColorPalette.LIGHT_BG);
        panel.add(createStepTitle("Step 2: Game Selection"), "span 2, wrap");

        panel.add(new JLabel("Game Title *:"));
        JComboBox<String> gameCombo = new JComboBox<>();
        gameCombo.setFont(Typography.BODY);
        try {
            GameDAO gameDAO = new GameDAO();
            List<Game> games = gameDAO.getAllGames();
            for (Game g : games) gameCombo.addItem(g.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            gameCombo.addItem("Valorant");
            gameCombo.addItem("League of Legends");
        }
        fields.put("game", gameCombo);
        panel.add(gameCombo, "growx");
        return panel;
    }

    // ---------- Step 3: Format ----------
    private JPanel createFormatStep() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 30", "[right][300:400:]", "[]15[]"));
        panel.setBackground(ColorPalette.LIGHT_BG);
        panel.add(createStepTitle("Step 3: Tournament Format"), "span 2, wrap");

        panel.add(new JLabel("Format *:"));
        JComboBox<String> formatCombo = new JComboBox<>(new String[]{"SINGLE_ELIMINATION", "ROUND_ROBIN"});
        formatCombo.setFont(Typography.BODY);
        fields.put("format", formatCombo);
        panel.add(formatCombo, "growx");

        panel.add(new JLabel("Max Teams *:"));
        JSpinner teamsSpinner = new JSpinner(new SpinnerNumberModel(16, 2, 128, 2));
        teamsSpinner.setFont(Typography.BODY);
        fields.put("maxTeams", teamsSpinner);
        panel.add(teamsSpinner, "growx");
        return panel;
    }

    // ---------- Step 4: Venue ----------
    private JPanel createVenueStep() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 30", "[right][300:400:]", "[]15[]"));
        panel.setBackground(ColorPalette.LIGHT_BG);
        panel.add(createStepTitle("Step 4: Venue (Optional)"), "span 2, wrap");

        panel.add(new JLabel("Select Venue:"));
        JComboBox<String> venueCombo = new JComboBox<>();
        venueCombo.addItem("None");
        try {
            VenueDAO venueDAO = new VenueDAO();
            for (Venue v : venueDAO.getAllVenues()) venueCombo.addItem(v.getName() + " (ID:" + v.getVenueId() + ")");
        } catch (SQLException e) { e.printStackTrace(); }
        fields.put("venue", venueCombo);
        panel.add(venueCombo, "growx");

        panel.add(new JLabel("Venue Location:"));
        JTextField locationField = new JTextField();
        locationField.setEditable(false);
        fields.put("location", locationField);
        panel.add(locationField, "growx");

        venueCombo.addActionListener(e -> {
            int idx = venueCombo.getSelectedIndex();
            if (idx > 0) {
                try {
                    String entry = (String) venueCombo.getSelectedItem();
                    int id = Integer.parseInt(entry.substring(entry.lastIndexOf(":") + 1, entry.lastIndexOf(")")));
                    Venue v = new VenueDAO().getAllVenues().stream().filter(ve -> ve.getVenueId() == id).findFirst().orElse(null);
                    if (v != null) locationField.setText(v.getLocation());
                } catch (Exception ex) { ex.printStackTrace(); }
            } else {
                locationField.setText("");
            }
        });
        return panel;
    }

    // ---------- Step 5: Registration ----------
    private JPanel createRegistrationStep() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 30", "[right][300:400:]", "[]15[]"));
        panel.setBackground(ColorPalette.LIGHT_BG);
        panel.add(createStepTitle("Step 5: Registration Settings"), "span 2, wrap");

        panel.add(new JLabel("Registration Deadline (YYYY-MM-DD):"));
        JTextField deadlineField = new JTextField();
        fields.put("deadline", deadlineField);
        panel.add(deadlineField, "growx");

        panel.add(new JLabel("Min Players per Team:"));
        JSpinner minPlayersSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        fields.put("minPlayers", minPlayersSpinner);
        panel.add(minPlayersSpinner, "growx");
        return panel;
    }

    // ---------- Step 6: Prize Pool ----------
    private JPanel createPrizePoolStep() {
        JPanel panel = new JPanel(new MigLayout("wrap 2, fillx, insets 30", "[right][300:400:]", "[]15[]"));
        panel.setBackground(ColorPalette.LIGHT_BG);
        panel.add(createStepTitle("Step 6: Prize Pool"), "span 2, wrap");

        panel.add(new JLabel("Total Prize Pool ($):"));
        JSpinner prizeSpinner = new JSpinner(new SpinnerNumberModel(1000.0, 0.0, 1000000.0, 100.0));
        fields.put("prize", prizeSpinner);
        panel.add(prizeSpinner, "growx");
        return panel;
    }

    // ---------- Step 7: Review ----------
    private JPanel createReviewStep() {
        JPanel panel = new JPanel(new MigLayout("wrap 1, fillx, insets 30", "[grow]", "[]15[]"));
        panel.setBackground(ColorPalette.LIGHT_BG);
        panel.add(createStepTitle("Step 7: Review & Confirm"), "wrap");

        JTextArea reviewArea = new JTextArea(10, 40);
        reviewArea.setEditable(false);
        reviewArea.setFont(Typography.BODY);
        fields.put("review", reviewArea);
        JScrollPane scrollPane = new JScrollPane(reviewArea);
        panel.add(scrollPane, "grow");
        return panel;
    }

    private JLabel createStepTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Typography.SECTION_TITLE);
        lbl.setForeground(ColorPalette.LIGHT_TEXT);
        return lbl;
    }

    private void showStep(int step) {
        cardLayout.show(stepsPanel, String.valueOf(step));
        currentStep = step;
        backBtn.setEnabled(step > 0);
        nextBtn.setText(step == 6 ? "Finish" : "Next >");
        if (step == 6) updateReview();
        // Force repaint to ensure visibility
        nextBtn.revalidate();
        nextBtn.repaint();
    }

    private void updateReview() {
        JTextArea area = (JTextArea) fields.get("review");
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(getFieldText("name")).append("\n");
        sb.append("Game: ").append(getSelectedGame()).append("\n");
        sb.append("Format: ").append(getFieldText("format")).append("\n");
        sb.append("Max Teams: ").append(((JSpinner)fields.get("maxTeams")).getValue()).append("\n");
        sb.append("Start: ").append(getFieldText("start")).append("\n");
        sb.append("End: ").append(getFieldText("end")).append("\n");
        sb.append("Prize Pool: $").append(((JSpinner)fields.get("prize")).getValue()).append("\n");
        JComboBox<?> venueCombo = (JComboBox<?>) fields.get("venue");
        if (venueCombo != null && venueCombo.getSelectedIndex() > 0) {
            sb.append("Venue: ").append(venueCombo.getSelectedItem()).append("\n");
        }
        area.setText(sb.toString());
    }

    private String getFieldText(String key) {
        JComponent comp = fields.get(key);
        if (comp instanceof JTextField) return ((JTextField)comp).getText().trim();
        if (comp instanceof JComboBox) return (String)((JComboBox<?>)comp).getSelectedItem();
        return "";
    }

    private String getSelectedGame() {
        JComboBox<?> gameCombo = (JComboBox<?>) fields.get("game");
        return (String) gameCombo.getSelectedItem();
    }

    private boolean validateStep(int step) {
        switch (step) {
            case 0:
                if (getFieldText("name").isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tournament name is required.");
                    return false;
                }
                if (!ValidationUtil.isDateYYYYMMDD(getFieldText("start"))) {
                    JOptionPane.showMessageDialog(this, "Invalid start date (YYYY-MM-DD).");
                    return false;
                }
                String end = getFieldText("end");
                if (!end.isEmpty() && !ValidationUtil.isDateYYYYMMDD(end)) {
                    JOptionPane.showMessageDialog(this, "Invalid end date (YYYY-MM-DD).");
                    return false;
                }
                return true;
            case 1:
                if (getFieldText("game") == null || getSelectedGame().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please select a game.");
                    return false;
                }
                return true;
            case 2:
                if (getFieldText("format") == null) {
                    JOptionPane.showMessageDialog(this, "Please select a format.");
                    return false;
                }
                return true;
            case 4:
                String deadline = getFieldText("deadline");
                if (!deadline.isEmpty() && !ValidationUtil.isDateYYYYMMDD(deadline)) {
                    JOptionPane.showMessageDialog(this, "Invalid registration deadline (YYYY-MM-DD).");
                    return false;
                }
                return true;
            default:
                return true;
        }
    }

    private void goNext() {
        if (!validateStep(currentStep)) return;
        if (currentStep == 6) {
            finishWizard();
        } else {
            showStep(currentStep + 1);
        }
    }

    private void goBack() {
        if (currentStep > 0) showStep(currentStep - 1);
    }

    private void finishWizard() {
        String name = getFieldText("name");
        String game = getSelectedGame();
        String format = getFieldText("format");
        String start = getFieldText("start");
        String end = getFieldText("end");
        int maxTeams = (Integer) ((JSpinner)fields.get("maxTeams")).getValue();
        double prize = (Double) ((JSpinner)fields.get("prize")).getValue();

        User user = UserSession.getCurrentUser();
        int organizerId = user != null ? user.getUserId() : 1;

        JComboBox<?> venueCombo = (JComboBox<?>) fields.get("venue");
        int venueId = 0;
        if (venueCombo != null && venueCombo.getSelectedIndex() > 0) {
            String venueEntry = (String) venueCombo.getSelectedItem();
            venueId = Integer.parseInt(venueEntry.substring(venueEntry.lastIndexOf(":") + 1, venueEntry.lastIndexOf(")")));
        }
        String deadline = getFieldText("deadline");
        int minPlayers = (Integer) ((JSpinner) fields.get("minPlayers")).getValue();
        boolean success = controller.createTournament(name, game, format, start,
                end.isEmpty() ? null : end, maxTeams, prize, organizerId, venueId,
                deadline.isEmpty() ? null : deadline, minPlayers);

        if (success) {
            JOptionPane.showMessageDialog(this, "Tournament created successfully!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create tournament. Check database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}