package com.etms.model;

public class PrizeDistribution {
    private int distributionId;
    private int tournamentId;
    private int position;
    private int teamId;
    private double percentage;
    private double amount;
    private boolean awarded;
    
    public PrizeDistribution() {}
    
    public PrizeDistribution(int position, double percentage, double amount) {
        this.position = position;
        this.percentage = percentage;
        this.amount = amount;
        this.awarded = false;
    }
    
    // Getters and Setters
    public int getDistributionId() { return distributionId; }
    public void setDistributionId(int distributionId) { this.distributionId = distributionId; }
    
    public int getTournamentId() { return tournamentId; }
    public void setTournamentId(int tournamentId) { this.tournamentId = tournamentId; }
    
    public int getPosition() { return position; }
    public void setPosition(int position) { this.position = position; }
    
    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }
    
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public boolean isAwarded() { return awarded; }
    public void setAwarded(boolean awarded) { this.awarded = awarded; }
}