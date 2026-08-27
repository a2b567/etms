package com.etms.service;

import com.etms.dao.TournamentDAO;
import com.etms.model.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.sql.SQLException;
import java.util.List;

public class TournamentService {

    private final TournamentDAO tournamentDAO = new TournamentDAO();

    public List<Tournament> getAllTournaments() throws SQLException {
        return tournamentDAO.getAllTournaments();
    }

    public Tournament getTournamentById(int id) throws SQLException {
        return tournamentDAO.getTournamentById(id);
    }

    public boolean createTournament(String name, String game, String type,
                                    String startDate, String endDate,
                                    int maxTeams, double prizePool,
                                    int organizerId) throws SQLException {
        return createTournament(name, game, type, startDate, endDate, maxTeams, prizePool, organizerId, 0);
    }

    public boolean createTournament(String name, String game, String type,
                                    String startDate, String endDate,
                                    int maxTeams, double prizePool,
                                    int organizerId, int venueId) throws SQLException {
        return createTournament(name, game, type, startDate, endDate, maxTeams,
                prizePool, organizerId, venueId, null, 1);
    }

    public boolean createTournament(String name, String game, String type,
                                    String startDate, String endDate,
                                    int maxTeams, double prizePool,
                                    int organizerId, int venueId,
                                    String registrationDeadline, int minPlayersPerTeam) throws SQLException {
        TournamentRules.validateTournament(name, game, type, startDate, endDate, maxTeams, prizePool);
        if (registrationDeadline != null && !registrationDeadline.isBlank()) {
            try {
                if (LocalDate.parse(registrationDeadline).isAfter(LocalDate.parse(startDate))) {
                    throw new IllegalArgumentException("Registration deadline cannot be after the tournament starts.");
                }
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Registration deadline must use YYYY-MM-DD.");
            }
        }
        if (minPlayersPerTeam < 1 || minPlayersPerTeam > RosterRules.MAX_ROSTER_SIZE) {
            throw new IllegalArgumentException("Minimum players per team must be between 1 and " + RosterRules.MAX_ROSTER_SIZE + ".");
        }
        Tournament tournament = tournamentDAO.createTournamentByType(type);

        tournament.setTournamentName(name);
        tournament.setGameTitle(game);
        tournament.setTournamentType(type);
        tournament.setStartDate(startDate);
        tournament.setEndDate(endDate);
        tournament.setMaxTeams(maxTeams);
        tournament.setPrizePool(prizePool);
        tournament.setOrganizerId(organizerId);
        tournament.setStatus("UPCOMING");
        tournament.setVenueId(venueId);
        tournament.setRegistrationDeadline(registrationDeadline);
        tournament.setMinPlayersPerTeam(minPlayersPerTeam);

        return tournamentDAO.createTournament(tournament);
    }

    public boolean updateTournament(Tournament t) throws SQLException {
        return tournamentDAO.updateTournament(t);
    }

    public boolean deleteTournament(int id) throws SQLException {
        return tournamentDAO.deleteTournament(id);
    }

    public int getActiveTournaments() throws SQLException {
        return tournamentDAO.getActiveTournaments();
    }

    public boolean updateTournamentStatus(int id, String status) throws SQLException {
        return tournamentDAO.updateTournamentStatus(id, status);
    }

    public void updateVenueId(int tournamentId, int venueId) throws SQLException {
        tournamentDAO.updateVenueId(tournamentId, venueId);
    }

    public List<Tournament> search(String query) throws SQLException {
        return tournamentDAO.search(query);
    }
}