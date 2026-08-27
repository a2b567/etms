package com.etms.model;

public class Team {
    private int teamId;
    private String teamName;
    private String tag;
    private int coachId;
    private String coachName;
    private String dateCreated;
    private int totalWins;
    private int totalLosses;
    private int tournamentPoints;
    private int ranking;
    private String status;
    private double eloRating = 1200.0;
    private String eloUpdated;

    public Team() {}

    public Team(String teamName, String tag) {
        this.teamName = teamName;
        this.tag = tag;
        this.status = "ACTIVE";
    }

    public double getWinRate() {
        int total = totalWins + totalLosses;
        if (total == 0) return 0.0;
        return (double) totalWins / total * 100;
    }

    // Getters and Setters
    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public int getCoachId() { return coachId; }
    public void setCoachId(int coachId) { this.coachId = coachId; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getDateCreated() { return dateCreated; }
    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    public int getTotalWins() { return totalWins; }
    public void setTotalWins(int totalWins) {
        if (totalWins < 0) throw new IllegalArgumentException("Wins cannot be negative");
        this.totalWins = totalWins;
    }

    public int getTotalLosses() { return totalLosses; }
    public void setTotalLosses(int totalLosses) {
        if (totalLosses < 0) throw new IllegalArgumentException("Losses cannot be negative");
        this.totalLosses = totalLosses;
    }

    public int getTournamentPoints() { return tournamentPoints; }
    public void setTournamentPoints(int tournamentPoints) {
        if (tournamentPoints < 0) throw new IllegalArgumentException("Points cannot be negative");
        this.tournamentPoints = tournamentPoints;
    }

    public int getRanking() { return ranking; }
    public void setRanking(int ranking) {
        if (ranking < 0) throw new IllegalArgumentException("Ranking cannot be negative");
        this.ranking = ranking;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (!"ACTIVE".equals(status) && !"INACTIVE".equals(status) && !"BANNED".equals(status)) {
            throw new IllegalArgumentException("Invalid team status");
        }
        this.status = status;
    }

    public double getEloRating() { return eloRating; }
    public void setEloRating(double eloRating) {
        if (eloRating < 0) throw new IllegalArgumentException("Elo rating cannot be negative");
        this.eloRating = eloRating;
    }

    public String getEloUpdated() { return eloUpdated; }
    public void setEloUpdated(String eloUpdated) { this.eloUpdated = eloUpdated; }

    @Override
    public String toString() { return teamName + " [" + tag + "]"; }
}