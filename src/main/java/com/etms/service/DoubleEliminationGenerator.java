package com.etms.service;

import com.etms.model.Match;
import com.etms.model.Team;
import com.etms.model.Tournament;
import java.util.*;

public class DoubleEliminationGenerator implements BracketGenerator {

    private int tournamentId;
    private List<Team> teams;

    public DoubleEliminationGenerator(int tournamentId, List<Team> teams) {
        this.tournamentId = tournamentId;
        this.teams = teams;
    }

    @Override
    public List<Match> generateBracket(Tournament tournament, List<Team> teams) {
        this.tournamentId = tournament.getTournamentId();
        this.teams = teams;
        return generateInitialBracket();
    }

    public List<Match> generateInitialBracket() {
        return new SingleEliminationGenerator(tournamentId, teams).generateInitialBracket();
    }

    @Override
    public List<Match> generateNextRoundMatches(List<Match> completedMatches) {
        // Placeholder – implement full double elimination if needed
        return new ArrayList<>();
    }
}