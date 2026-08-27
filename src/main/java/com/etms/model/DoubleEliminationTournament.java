package com.etms.model;

public class DoubleEliminationTournament extends Tournament {

    public DoubleEliminationTournament() {
        setTournamentType("DOUBLE_ELIMINATION");
    }

    @Override
    public void generateBracket() {
        // Logic for double elimination bracket
    }

    @Override
    public String getBracketDescription() {
        return "Double elimination bracket. A team is eliminated after two losses.";
    }

    @Override
    public int getMaxRounds() {
        return 2 * (int) Math.ceil(Math.log(getMaxTeams()) / Math.log(2));
    }

    @Override
    public String getFormatDescription() {
        return "Double Elimination – best of 3 in finals";
    }
}