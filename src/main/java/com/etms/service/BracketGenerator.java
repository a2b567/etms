package com.etms.service;

import com.etms.model.Match;
import com.etms.model.Team;
import com.etms.model.Tournament;
import java.util.List;

public interface BracketGenerator {
    List<Match> generateBracket(Tournament tournament, List<Team> teams);
    // NEW: required by DashboardController
    List<Match> generateNextRoundMatches(List<Match> completedMatches);
}