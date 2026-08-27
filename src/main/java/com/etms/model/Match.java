package com.etms.model;

public class Match {
    private int matchId;
    private int tournamentId;
    private int roundNumber;
    private int matchNumber;
    private int team1Id;
    private int team2Id;
    private String team1Name;
    private String team2Name;
    private int winnerTeamId;
    private String winnerTeamName;
    private int team1Score;
    private int team2Score;
    private String scheduledTime;
    private String status;
    private int refereeId;
    private String refereeName;

    public Match() {
        this.status = "SCHEDULED";
    }

    // Getters and Setters
    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }

    public int getTournamentId() { return tournamentId; }
    public void setTournamentId(int tournamentId) { this.tournamentId = tournamentId; }

    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) {
        if (roundNumber < 1) throw new IllegalArgumentException("Round must be >= 1");
        this.roundNumber = roundNumber;
    }

    public int getMatchNumber() { return matchNumber; }
    public void setMatchNumber(int matchNumber) {
        if (matchNumber < 1) throw new IllegalArgumentException("Match number must be >= 1");
        this.matchNumber = matchNumber;
    }

    public int getTeam1Id() { return team1Id; }
    public void setTeam1Id(int team1Id) { this.team1Id = team1Id; }

    public int getTeam2Id() { return team2Id; }
    public void setTeam2Id(int team2Id) { this.team2Id = team2Id; }

    public String getTeam1Name() { return team1Name; }
    public void setTeam1Name(String team1Name) { this.team1Name = team1Name; }

    public String getTeam2Name() { return team2Name; }
    public void setTeam2Name(String team2Name) { this.team2Name = team2Name; }

    public int getWinnerTeamId() { return winnerTeamId; }
    public void setWinnerTeamId(int winnerTeamId) { this.winnerTeamId = winnerTeamId; }

    public String getWinnerTeamName() { return winnerTeamName; }
    public void setWinnerTeamName(String winnerTeamName) { this.winnerTeamName = winnerTeamName; }

    public int getTeam1Score() { return team1Score; }
    public void setTeam1Score(int team1Score) {
        if (team1Score < 0) throw new IllegalArgumentException("Score cannot be negative");
        this.team1Score = team1Score;
    }

    public int getTeam2Score() { return team2Score; }
    public void setTeam2Score(int team2Score) {
        if (team2Score < 0) throw new IllegalArgumentException("Score cannot be negative");
        this.team2Score = team2Score;
    }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        String[] valid = {"SCHEDULED", "LIVE", "COMPLETED", "CANCELLED"};
        boolean found = false;
        for (String s : valid) {
            if (s.equals(status)) { found = true; break; }
        }
        if (!found) {
            throw new IllegalArgumentException("Invalid match status: " + status);
        }
        this.status = status;
    }

    public int getRefereeId() { return refereeId; }
    public void setRefereeId(int refereeId) { this.refereeId = refereeId; }

    public String getRefereeName() { return refereeName; }
    public void setRefereeName(String refereeName) { this.refereeName = refereeName; }

    @Override
    public String toString() {
        return team1Name + " vs " + team2Name + " [" + status + "]";
    }
}