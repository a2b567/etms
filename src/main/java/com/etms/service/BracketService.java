package com.etms.service;

import com.etms.model.Match;
import com.etms.model.Team;
import com.etms.model.Tournament;
import java.util.List;

public class BracketService {

    private final BracketGenerator bracketGenerator;
    private final MatchScheduler matchScheduler;
    private final ScoringSystem scoringSystem;

    public BracketService() {
        this.bracketGenerator = new DefaultBracketGenerator(); // if missing, create stub
        this.matchScheduler = new DefaultMatchScheduler();
        this.scoringSystem = new DefaultScoringSystem();
    }

    public BracketService(BracketGenerator generator, MatchScheduler scheduler, ScoringSystem scorer) {
        this.bracketGenerator = generator;
        this.matchScheduler = scheduler;
        this.scoringSystem = scorer;
    }

    // NEW: returns appropriate generator based on tournament type
    public BracketGenerator getGenerator(Tournament tournament, List<Team> teams) {
        String type = tournament.getTournamentType();
        if ("SINGLE_ELIMINATION".equalsIgnoreCase(type)) {
            return new SingleEliminationGenerator(tournament.getTournamentId(), teams);
        } else if ("DOUBLE_ELIMINATION".equalsIgnoreCase(type)) {
            return new DoubleEliminationGenerator(tournament.getTournamentId(), teams);
        } else if ("ROUND_ROBIN".equalsIgnoreCase(type)) {
            return new RoundRobinGenerator(tournament.getTournamentId(), teams);
        } else if ("SWISS".equalsIgnoreCase(type)) {
            return new SwissGenerator(tournament.getTournamentId(), teams);
        } else {
            return new SingleEliminationGenerator(tournament.getTournamentId(), teams);
        }
    }

    public List<Match> generateBracket(Tournament tournament, List<Team> teams) {
        if (tournament == null || teams == null || teams.size() < 2) {
            throw new IllegalArgumentException("Tournament and at least 2 teams are required");
        }
        BracketGenerator generator = getGenerator(tournament, teams);
        List<Match> matches = generator.generateBracket(tournament, teams);
        if (matches != null && !matches.isEmpty()) {
            matches = matchScheduler.scheduleMatches(matches);
        }
        return matches;
    }

    public List<Match> advanceBracket(List<Match> completedMatches) {
        // This method may be refactored; kept as original logic
        if (completedMatches == null || completedMatches.isEmpty()) return null;
        // For now, return empty list (no logic change)
        return new java.util.ArrayList<>();
    }
}