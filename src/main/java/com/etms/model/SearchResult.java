package com.etms.model;

public class SearchResult {
    private String type;          // e.g., "Team", "Player", "Tournament"
    private int id;
    private String displayText;
    private String panelName;     // card layout key for navigation

    public SearchResult(String type, int id, String displayText, String panelName) {
        this.type = type;
        this.id = id;
        this.displayText = displayText;
        this.panelName = panelName;
    }

    public String getType() { return type; }
    public int getId() { return id; }
    public String getDisplayText() { return displayText; }
    public String getPanelName() { return panelName; }

    @Override
    public String toString() {
        return type + ": " + displayText;
    }
}