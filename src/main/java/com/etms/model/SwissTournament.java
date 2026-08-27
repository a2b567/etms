package com.etms.model;

public class SwissTournament extends Tournament {

    public SwissTournament() {
        setTournamentType("SWISS");
    }

    @Override
    public void generateBracket() {
        // Logic for Swiss pairings
    }

    @Override
    public String getBracketDescription() {
        return "Swiss tournament system. Teams paired based on similar scores.";
    }

    @Override
    public int getMaxRounds() {
        return (int) Math.ceil(Math.log(getMaxTeams()) / Math.log(2));
    }

    @Override
    public String getFormatDescription() {
        return "Swiss System – pair teams with similar records";
    }
}