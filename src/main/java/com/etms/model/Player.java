package com.etms.model;

// Player.java - INHERITANCE from Person
public class Player extends Person {
    private int playerId;
    private String inGameName;
    private String gameRank;
    private String gameRole;
    private int teamId;
    private String teamName;
    private int totalMatches;
    private int wins;
    private int losses;
    private int mvpCount;
    private String status;

    // NEW FIELDS for roster management
    private boolean isCaptain = false;
    private boolean isStarter = false;
    private int jerseyNumber = 0;

    public Player() {
        super();
    }

    public Player(String firstName, String lastName, String inGameName) {
        super(firstName, lastName);
        this.inGameName = inGameName;
        this.status = "ACTIVE";
    }

    @Override
    public String getRole() {
        return "PLAYER";
    }

    public double getWinRate() {
        if (totalMatches == 0) return 0.0;
        return (double) wins / totalMatches * 100;
    }

    // Getters and Setters (existing)
    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public String getInGameName() { return inGameName; }
    public void setInGameName(String inGameName) { this.inGameName = inGameName; }

    public String getGameRank() { return gameRank; }
    public void setGameRank(String gameRank) { this.gameRank = gameRank; }

    public String getGameRole() { return gameRole; }
    public void setGameRole(String gameRole) { this.gameRole = gameRole; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public int getTotalMatches() { return totalMatches; }
    public void setTotalMatches(int totalMatches) { this.totalMatches = totalMatches; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public int getMvpCount() { return mvpCount; }
    public void setMvpCount(int mvpCount) { this.mvpCount = mvpCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // NEW getters/setters for captain/starter/jersey
    public boolean isCaptain() { return isCaptain; }
    public void setCaptain(boolean captain) { isCaptain = captain; }

    public boolean isStarter() { return isStarter; }
    public void setStarter(boolean starter) { isStarter = starter; }

    public int getJerseyNumber() { return jerseyNumber; }
    public void setJerseyNumber(int jerseyNumber) { this.jerseyNumber = jerseyNumber; }
}