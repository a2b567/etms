package com.etms.model;

public class SingleEliminationTournament extends Tournament {

    public SingleEliminationTournament() {
        setTournamentType("SINGLE_ELIMINATION");
    }

    @Override
    public void generateBracket() {
        // Logic for single elimination bracket generation
        // This will be called by BracketService
    }

    @Override
    public String getBracketDescription() {
        return "Standard single elimination bracket. Each match eliminates one team.";
    }

    @Override
    public int getMaxRounds() {
        return (int) Math.ceil(Math.log(getMaxTeams()) / Math.log(2));
    }

    @Override
    public String getFormatDescription() {
        return "Single Elimination – best of 1";
    }
}