package com.etms.model;

public abstract class Tournament {
    private int tournamentId;
    private String tournamentName;
    private String gameTitle;
    private String tournamentType;
    private String startDate;
    private String endDate;
    private int maxTeams;
    private double prizePool;
    private int organizerId;
    private String status;
    private int venueId;
    private String registrationDeadline;
    private int minPlayersPerTeam = 1;

    public Tournament() {}

    // Abstract methods - POLYMORPHISM
    public abstract void generateBracket();
    public abstract String getBracketDescription();
    public abstract int getMaxRounds();           // NEW
    public abstract String getFormatDescription(); // NEW

    // Getters and Setters
    public int getTournamentId() { return tournamentId; }
    public void setTournamentId(int tournamentId) { this.tournamentId = tournamentId; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public String getGameTitle() { return gameTitle; }
    public void setGameTitle(String gameTitle) { this.gameTitle = gameTitle; }

    public String getTournamentType() { return tournamentType; }
    public void setTournamentType(String tournamentType) { this.tournamentType = tournamentType; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public int getMaxTeams() { return maxTeams; }
    public void setMaxTeams(int maxTeams) {
        if (maxTeams < 2) {
            throw new IllegalArgumentException("Max teams must be at least 2");
        }
        this.maxTeams = maxTeams;
    }

    public double getPrizePool() { return prizePool; }
    public void setPrizePool(double prizePool) {
        if (prizePool < 0) {
            throw new IllegalArgumentException("Prize pool cannot be negative");
        }
        this.prizePool = prizePool;
    }

    public int getOrganizerId() { return organizerId; }
    public void setOrganizerId(int organizerId) { this.organizerId = organizerId; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        String[] valid = {"UPCOMING", "REGISTRATION", "ONGOING", "COMPLETED", "CANCELLED"};
        boolean found = false;
        for (String s : valid) {
            if (s.equals(status)) { found = true; break; }
        }
        if (!found) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        this.status = status;
    }

    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) { this.venueId = venueId; }

    public String getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(String registrationDeadline) {
        // Basic date format check (optional)
        this.registrationDeadline = registrationDeadline;
    }

    public int getMinPlayersPerTeam() { return minPlayersPerTeam; }
    public void setMinPlayersPerTeam(int minPlayersPerTeam) {
        if (minPlayersPerTeam < 1 || minPlayersPerTeam > 10) {
            throw new IllegalArgumentException("Min players per team must be between 1 and 10");
        }
        this.minPlayersPerTeam = minPlayersPerTeam;
    }
}