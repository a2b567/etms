package com.etms.service;

import com.etms.dao.*;
import com.etms.model.SearchResult;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SearchService {

    private final TeamDAO teamDAO = new TeamDAO();
    private final PlayerDAO playerDAO = new PlayerDAO();
    private final TournamentDAO tournamentDAO = new TournamentDAO();
    private final SponsorDAO sponsorDAO = new SponsorDAO();
    private final VenueDAO venueDAO = new VenueDAO();
    private final EquipmentDAO equipmentDAO = new EquipmentDAO();
    private final GameDAO gameDAO = new GameDAO();

    public List<SearchResult> search(String query) {
        List<SearchResult> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) return results;

        String q = query.trim();
        try {
            // Teams
            teamDAO.search(q).forEach(t -> results.add(new SearchResult("Team", t.getTeamId(), t.getTeamName(), "TEAMS")));
            // Players
            playerDAO.search(q).forEach(p -> results.add(new SearchResult("Player", p.getPlayerId(), p.getInGameName() + " (" + p.getFullName() + ")", "PLAYERS")));
            // Tournaments
            tournamentDAO.search(q).forEach(t -> results.add(new SearchResult("Tournament", t.getTournamentId(), t.getTournamentName(), "TOURNAMENTS")));
            // Sponsors
            sponsorDAO.search(q).forEach(s -> results.add(new SearchResult("Sponsor", s.getSponsorId(), s.getCompanyName(), "SPONSORS")));
            // Venues
            venueDAO.search(q).forEach(v -> results.add(new SearchResult("Venue", v.getVenueId(), v.getName(), "VENUES")));
            // Equipment
            equipmentDAO.search(q).forEach(e -> results.add(new SearchResult("Equipment", e.getEquipmentId(), e.getType() + " " + e.getBrand(), "EQUIPMENT")));
            // Games
            gameDAO.search(q).forEach(g -> results.add(new SearchResult("Game", g.getGameId(), g.getName(), "GAMES")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}