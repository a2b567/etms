package com.etms.service;

import com.etms.model.Match;
import com.etms.model.Team;
import com.etms.model.Tournament;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of BracketGenerator.
 * Uses SingleEliminationGenerator for initial bracket generation.
 */
public class DefaultBracketGenerator implements BracketGenerator {

    @Override
    public List<Match> generateBracket(Tournament tournament, List<Team> teams) {
        // Use single elimination as the default
        SingleEliminationGenerator generator = new SingleEliminationGenerator(
                tournament.getTournamentId(), teams);
        return generator.generateInitialBracket();
    }

    @Override
    public List<Match> generateNextRoundMatches(List<Match> completedMatches) {
        if (completedMatches == null || completedMatches.isEmpty()) {
            return new ArrayList<>();
        }
        // Get tournamentId from the first match
        int tournamentId = completedMatches.get(0).getTournamentId();
        // Collect winners
        List<Integer> winnerIds = new ArrayList<>();
        for (Match m : completedMatches) {
            if (m.getWinnerTeamId() > 0) {
                winnerIds.add(m.getWinnerTeamId());
            }
        }
        if (winnerIds.size() <= 1) {
            return new ArrayList<>(); // no next round
        }
        // Use SingleEliminationGenerator's helper to create next round
        // We need to pass a list of Team objects, but we only have IDs.
        // For simplicity, we'll create a minimal Team list (just IDs).
        // In a real scenario, we would fetch Teams from DAO, but that's out of scope.
        // Here we assume the method can work with just IDs; we'll create dummy Team objects.
        List<Team> winners = new ArrayList<>();
        for (Integer id : winnerIds) {
            Team t = new Team();
            t.setTeamId(id);
            winners.add(t);
        }
        SingleEliminationGenerator generator = new SingleEliminationGenerator(tournamentId, winners);
        // We can reuse the method but need to adjust round number.
        // The generator's generateNextRoundMatches will produce matches with round+1.
        return generator.generateNextRoundMatches(completedMatches);
    }
}