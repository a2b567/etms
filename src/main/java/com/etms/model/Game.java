package com.etms.model;

public class Game {
    private int gameId;
    private String name;
    private int defaultPlayerCount;
    private String description;

    public Game() {}

    public Game(String name, int defaultPlayerCount, String description) {
        this.name = name;
        this.defaultPlayerCount = defaultPlayerCount;
        this.description = description;
    }

    public int getGameId() { return gameId; }
    public void setGameId(int gameId) { this.gameId = gameId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDefaultPlayerCount() { return defaultPlayerCount; }
    public void setDefaultPlayerCount(int defaultPlayerCount) { this.defaultPlayerCount = defaultPlayerCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() { return name; }
}