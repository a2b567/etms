package com.etms.service;

import com.etms.model.Match;
import com.etms.model.Team;
import com.etms.model.Tournament;
import java.util.*;

public class SwissGenerator implements BracketGenerator {

    private int tournamentId;
    private List<Team> teams;
    private int currentRound = 0;

    public SwissGenerator(int tournamentId, List<Team> teams) {
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
        currentRound = 1;
        List<Team> shuffled = new ArrayList<>(teams);
        Collections.shuffle(shuffled);
        List<Match> matches = new ArrayList<>();
        int matchNum = 1;
        for (int i = 0; i < shuffled.size(); i += 2) {
            Match m = new Match();
            m.setTournamentId(tournamentId);
            m.setRoundNumber(currentRound);
            m.setMatchNumber(matchNum++);
            m.setTeam1Id(shuffled.get(i).getTeamId());
            if (i + 1 < shuffled.size()) {
                m.setTeam2Id(shuffled.get(i + 1).getTeamId());
            } else {
                m.setTeam2Id(0);
                m.setWinnerTeamId(m.getTeam1Id());
                m.setStatus("COMPLETED");
            }
            matches.add(m);
        }
        return matches;
    }

    @Override
    public List<Match> generateNextRoundMatches(List<Match> completedMatches) {
        // Placeholder for Swiss pairing logic
        return new ArrayList<>();
    }
}