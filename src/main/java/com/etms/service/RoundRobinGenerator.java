package com.etms.service;

import com.etms.model.Match;
import com.etms.model.Team;
import com.etms.model.Tournament;
import java.util.*;

public class RoundRobinGenerator implements BracketGenerator {

    private int tournamentId;
    private List<Team> teams;

    public RoundRobinGenerator(int tournamentId, List<Team> teams) {
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
        List<Match> matches = new ArrayList<>();
        int matchNum = 1;
        for (int i = 0; i < teams.size(); i++) {
            for (int j = i + 1; j < teams.size(); j++) {
                Match m = new Match();
                m.setTournamentId(tournamentId);
                m.setRoundNumber(1);
                m.setMatchNumber(matchNum++);
                m.setTeam1Id(teams.get(i).getTeamId());
                m.setTeam2Id(teams.get(j).getTeamId());
                m.setStatus("SCHEDULED");
                matches.add(m);
            }
        }
        return matches;
    }

    @Override
    public List<Match> generateNextRoundMatches(List<Match> completedMatches) {
        return new ArrayList<>(); // round robin has no next round
    }
}