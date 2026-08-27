package com.etms.model;

public class RoundRobinTournament extends Tournament {

    public RoundRobinTournament() {
        setTournamentType("ROUND_ROBIN");
    }

    @Override
    public void generateBracket() {
        // Logic for round robin schedule
    }

    @Override
    public String getBracketDescription() {
        return "Round robin schedule. Each team plays every other team once.";
    }

    @Override
    public int getMaxRounds() {
        return getMaxTeams() - 1;
    }

    @Override
    public String getFormatDescription() {
        return "Round Robin – each team plays all others once";
    }
}