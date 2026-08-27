package com.etms.service;

import com.etms.model.Match;
import com.etms.model.Team;
import com.etms.model.Tournament;
import java.util.*;

public class SingleEliminationGenerator implements BracketGenerator {

    private int tournamentId;
    private List<Team> teams;

    public SingleEliminationGenerator(int tournamentId, List<Team> teams) {
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
        if (teams == null || teams.size() < 2) return matches;

        List<Team> seeded = new ArrayList<>(teams);
        int totalSlots = nextPowerOfTwo(seeded.size());
        int byes = totalSlots - seeded.size();
        int matchNum = 1;
        int round = 1;
        int idx = 0;

        for (int i = 0; i < totalSlots / 2; i++) {
            Match m = new Match();
            m.setTournamentId(tournamentId);
            m.setRoundNumber(round);
            m.setMatchNumber(matchNum++);

            if (i < byes) {
                m.setTeam1Id(seeded.get(idx++).getTeamId());
                m.setTeam2Id(0);
                m.setWinnerTeamId(m.getTeam1Id());
                m.setStatus("COMPLETED");
            } else {
                m.setTeam1Id(seeded.get(idx++).getTeamId());
                m.setTeam2Id(seeded.get(idx++).getTeamId());
                m.setStatus("SCHEDULED");
            }
            matches.add(m);
        }
        return matches;
    }

    @Override
    public List<Match> generateNextRoundMatches(List<Match> completedMatches) {
        List<Match> nextRound = new ArrayList<>();
        List<Integer> winners = new ArrayList<>();
        for (Match m : completedMatches) {
            if (m.getWinnerTeamId() > 0) {
                winners.add(m.getWinnerTeamId());
            }
        }
        if (winners.size() <= 1) return nextRound;

        int matchNum = 1;
        int round = completedMatches.get(0).getRoundNumber() + 1;
        for (int i = 0; i < winners.size(); i += 2) {
            Match m = new Match();
            m.setTournamentId(tournamentId);
            m.setRoundNumber(round);
            m.setMatchNumber(matchNum++);
            m.setTeam1Id(winners.get(i));
            if (i + 1 < winners.size()) {
                m.setTeam2Id(winners.get(i + 1));
                m.setStatus("SCHEDULED");
            } else {
                m.setTeam2Id(0);
                m.setWinnerTeamId(m.getTeam1Id());
                m.setStatus("COMPLETED");
            }
            nextRound.add(m);
        }
        return nextRound;
    }

    private int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) power *= 2;
        return power;
    }
}