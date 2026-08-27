package com.etms.controller;

import com.etms.dao.*;
import com.etms.model.*;
import com.etms.service.*;
import javax.swing.*;
import java.sql.SQLException;
import java.util.*;

/**
 * Main controller for dashboard operations.
 * ENCAPSULATION: DAOs are private and lazy-initialized.
 * ABSTRACTION: Hides complexity of services/DAOs from UI.
 * POLYMORPHISM: Uses Tournament subclasses via factory methods.
 */
public class DashboardController {

    // ---------- DAOs (lazy) ----------
    private TournamentDAO tournamentDAO;
    private TeamDAO teamDAO;
    private PlayerDAO playerDAO;
    private MatchDAO matchDAO;
    private UserDAO userDAO;
    private PersonDAO personDAO;
    private NotificationDAO notificationDAO;
    private RegistrationDAO registrationDAO;
    private TournamentService tournamentService;
    private PlayerPerformanceDAO playerPerformanceDAO;
    private EloService eloService;
    private MatchPredictionService predictionService;
    private RefereeService refereeService;
    private EquipmentService equipmentService;
    private VenueDAO venueDAO;
    private RefereeDAO refereeDAO;
    private SponsorDAO sponsorDAO;
    private PrizeDistributionDAO prizeDistributionDAO;

    // ---------- Lazy getters ----------
    private TournamentService getTournamentService() {
        if (tournamentService == null) try { tournamentService = new TournamentService(); } catch (Exception e) { e.printStackTrace(); }
        return tournamentService;
    }
    private TournamentDAO getTournamentDAO() {
        if (tournamentDAO == null) try { tournamentDAO = new TournamentDAO(); } catch (Exception e) { e.printStackTrace(); }
        return tournamentDAO;
    }
    private TeamDAO getTeamDAO() {
        if (teamDAO == null) try { teamDAO = new TeamDAO(); } catch (Exception e) { e.printStackTrace(); }
        return teamDAO;
    }
    private PlayerDAO getPlayerDAO() {
        if (playerDAO == null) try { playerDAO = new PlayerDAO(); } catch (Exception e) { e.printStackTrace(); }
        return playerDAO;
    }
    private MatchDAO getMatchDAO() {
        if (matchDAO == null) try { matchDAO = new MatchDAO(); } catch (Exception e) { e.printStackTrace(); }
        return matchDAO;
    }
    private UserDAO getUserDAO() {
        if (userDAO == null) try { userDAO = new UserDAO(); } catch (Exception e) { e.printStackTrace(); }
        return userDAO;
    }
    private PersonDAO getPersonDAO() {
        if (personDAO == null) try { personDAO = new PersonDAO(); } catch (Exception e) { e.printStackTrace(); }
        return personDAO;
    }
    private NotificationDAO getNotificationDAO() {
        if (notificationDAO == null) try { notificationDAO = new NotificationDAO(); } catch (Exception e) { e.printStackTrace(); }
        return notificationDAO;
    }
    private RegistrationDAO getRegistrationDAO() {
        if (registrationDAO == null) try { registrationDAO = new RegistrationDAO(); } catch (Exception e) { e.printStackTrace(); }
        return registrationDAO;
    }
    private PlayerPerformanceDAO getPlayerPerformanceDAO() {
        if (playerPerformanceDAO == null) try { playerPerformanceDAO = new PlayerPerformanceDAO(); } catch (Exception e) { e.printStackTrace(); }
        return playerPerformanceDAO;
    }
    private EloService getEloService() {
        if (eloService == null) { eloService = new EloService(); }
        return eloService;
    }
    private MatchPredictionService getPredictionService() {
        if (predictionService == null) { predictionService = new MatchPredictionService(); }
        return predictionService;
    }
    private RefereeService getRefereeService() {
        if (refereeService == null) { refereeService = new RefereeService(); }
        return refereeService;
    }
    private EquipmentService getEquipmentService() {
        if (equipmentService == null) { equipmentService = new EquipmentService(); }
        return equipmentService;
    }
    private VenueDAO getVenueDAO() {
        if (venueDAO == null) try { venueDAO = new VenueDAO(); } catch (Exception e) { e.printStackTrace(); }
        return venueDAO;
    }
    private RefereeDAO getRefereeDAO() {
        if (refereeDAO == null) try { refereeDAO = new RefereeDAO(); } catch (Exception e) { e.printStackTrace(); }
        return refereeDAO;
    }
    private SponsorDAO getSponsorDAO() {
        if (sponsorDAO == null) try { sponsorDAO = new SponsorDAO(); } catch (Exception e) { e.printStackTrace(); }
        return sponsorDAO;
    }
    private PrizeDistributionDAO getPrizeDistributionDAO() {
        if (prizeDistributionDAO == null) try { prizeDistributionDAO = new PrizeDistributionDAO(); } catch (Exception e) { e.printStackTrace(); }
        return prizeDistributionDAO;
    }

    // ========== RBAC HELPER ==========
    private User requireRole(String... allowedRoles) {
        User current = UserSession.getCurrentUser();
        if (current == null) {
            throw new SecurityException("Not authenticated.");
        }
        for (String role : allowedRoles) {
            if (current.getRole().equals(role) || current.getRole().equals("ADMIN")) {
                return current;
            }
        }
        throw new SecurityException("Access denied.");
    }

    // ==================== Dashboard Stats ====================
    public int getActiveTournaments() { try { return getTournamentService().getActiveTournaments(); } catch (Exception e) { e.printStackTrace(); return 0; } }
    public int getTotalTeams() { try { return getTeamDAO().getTotalTeams(); } catch (Exception e) { e.printStackTrace(); return 0; } }
    public int getTotalPlayers() { try { return getPlayerDAO().getTotalPlayers(); } catch (Exception e) { e.printStackTrace(); return 0; } }
    public int getScheduledMatches() { try { return getMatchDAO().getScheduledMatches(); } catch (Exception e) { e.printStackTrace(); return 0; } }
    public int getCompletedMatches() { try { return getMatchDAO().getCompletedMatches(); } catch (Exception e) { e.printStackTrace(); return 0; } }
    public int getTotalTournamentsCount() { try { return getTournamentDAO().getAllTournaments().size(); } catch (Exception e) { e.printStackTrace(); return 0; } }
    public int getTotalMatchesCount() { return getScheduledMatches() + getCompletedMatches(); }

    // ==================== Tournament CRUD ====================
    public List<Tournament> getAllTournaments() {
        try { return getTournamentService().getAllTournaments(); } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean createTournament(String name, String game, String type, String start,
                                    String end, int maxTeams, double prizePool, int organizerId,
                                    int venueId, String registrationDeadline, int minPlayers) {
        try {
            requireRole("ORGANIZER");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean success = false;
        try {
            Tournament tournament = new SingleEliminationTournament();
            tournament.setTournamentName(name);
            tournament.setGameTitle(game);
            tournament.setTournamentType(type);
            tournament.setStartDate(start);
            tournament.setEndDate(end);
            tournament.setMaxTeams(maxTeams);
            tournament.setPrizePool(prizePool);
            tournament.setOrganizerId(organizerId);
            tournament.setVenueId(venueId);
            tournament.setRegistrationDeadline(registrationDeadline);
            tournament.setMinPlayersPerTeam(minPlayers);
            tournament.setStatus("UPCOMING");
            success = getTournamentDAO().createTournament(tournament);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (success) notifyAdmins("New tournament created: " + name);
        return success;
    }

    public boolean updateTournament(Tournament tournament) {
        try {
            requireRole("ORGANIZER");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            boolean ok = getTournamentDAO().updateTournament(tournament);
            System.out.println("Update result for tournament " + tournament.getTournamentId() + ": " + ok);
            return ok;
        } catch (SQLException e) {
            System.err.println("SQL error updating tournament: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean deleteTournament(int id) {
        try {
            requireRole("ORGANIZER");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try { return getTournamentService().deleteTournament(id); } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public void updateLastTournamentVenue(int venueId, int organizerId) {
        try {
            requireRole("ORGANIZER");
            List<Tournament> tournaments = getTournamentDAO().getAllTournaments();
            Tournament latest = null;
            for (Tournament t : tournaments) {
                if (t.getOrganizerId() == organizerId) { latest = t; break; }
            }
            if (latest != null) getTournamentDAO().updateVenueId(latest.getTournamentId(), venueId);
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void updateVenueId(int tournamentId, int venueId) {
        try {
            requireRole("ORGANIZER");
            getTournamentDAO().updateVenueId(tournamentId, venueId);
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ==================== Team CRUD ====================
    public List<Team> getAllTeams() {
        try { return getTeamDAO().getAllTeams(); } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean addTeam(Team team) {
        try {
            requireRole("ORGANIZER", "COACH");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            if (team.getStatus() == null || team.getStatus().isEmpty()) {
                team.setStatus("ACTIVE");
            }
            if (team.getEloRating() <= 0) {
                team.setEloRating(1200.0);
            }
            boolean success = getTeamDAO().createTeam(team);
            if (success) {
                logAudit("TEAM_CREATED", "Team: " + team.getTeamName() + " (ID: " + team.getTeamId() + ")");
                JOptionPane.showMessageDialog(null, "Team added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to save team. Please check that the team name and tag are unique.", "Database Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg.contains("duplicate key") || msg.contains("unique constraint")) {
                JOptionPane.showMessageDialog(null, "A team with this name or tag already exists.", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Database error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }

    public boolean updateTeam(Team team) {
        try {
            requireRole("ORGANIZER", "COACH");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            boolean success = getTeamDAO().updateTeam(team);
            if (success) {
                logAudit("TEAM_UPDATED", "Team ID: " + team.getTeamId());
                JOptionPane.showMessageDialog(null, "Team updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg.contains("duplicate key") || msg.contains("unique constraint")) {
                JOptionPane.showMessageDialog(null, "A team with this name or tag already exists.", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Database error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }

    public boolean createTeam(String name, String tag) {
        Team team = new Team(name, tag);
        team.setStatus("ACTIVE");
        return addTeam(team);
    }

    public boolean createTeam(String name, String tag, int coachId) {
        Team team = new Team(name, tag);
        team.setCoachId(coachId);
        team.setStatus("ACTIVE");
        return addTeam(team);
    }

    public boolean deleteTeam(int id) {
        try {
            requireRole("ORGANIZER", "COACH");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            boolean success = getTeamDAO().deleteTeam(id);
            if (success) {
                logAudit("TEAM_DELETED", "Team ID: " + id);
                JOptionPane.showMessageDialog(null, "Team deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public List<Team> getTeamsForTournament(int tournamentId) {
        try { return getTeamDAO().getTeamsByTournament(tournamentId); } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    // ==================== Player CRUD ====================
    public List<Player> getAllPlayers() {
        try { return getPlayerDAO().getAllPlayers(); } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean addPlayer(Player player) {
        try {
            requireRole("ORGANIZER", "COACH");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            GenericPerson person = new GenericPerson(player.getFirstName(), player.getLastName());
            person.setPhone(player.getPhone());
            person.setEmail(player.getEmail());
            int personId = getPersonDAO().createPerson(person);
            if (personId == -1) {
                JOptionPane.showMessageDialog(null, "Failed to create person record.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            player.setPersonId(personId);

            if (player.getGameRank() == null || player.getGameRank().isEmpty()) {
                player.setGameRank("Unranked");
            }
            if (player.getGameRole() == null || player.getGameRole().isEmpty()) {
                player.setGameRole("Flex");
            }
            if (player.getStatus() == null || player.getStatus().isEmpty()) {
                player.setStatus("ACTIVE");
            }

            boolean success = getPlayerDAO().createPlayer(player);
            if (success) {
                logAudit("PLAYER_CREATED", "Player: " + player.getInGameName() + " (ID: " + player.getPlayerId() + ")");
                JOptionPane.showMessageDialog(null, "Player added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to save player. Please check the data.", "Database Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg.contains("duplicate key") || msg.contains("unique constraint")) {
                JOptionPane.showMessageDialog(null, "A player with this in-game name already exists.", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Database error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }

    public boolean updatePlayer(Player player) {
        try {
            requireRole("ORGANIZER", "COACH");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            if (player.getPersonId() > 0) {
                GenericPerson person = new GenericPerson(player.getFirstName(), player.getLastName());
                person.setPersonId(player.getPersonId());
                person.setPhone(player.getPhone());
                person.setEmail(player.getEmail());
                getPersonDAO().updatePerson(person);
            }

            boolean success = getPlayerDAO().updatePlayer(player);
            if (success) {
                logAudit("PLAYER_UPDATED", "Player ID: " + player.getPlayerId());
                JOptionPane.showMessageDialog(null, "Player updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Failed to update player.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg.contains("duplicate key") || msg.contains("unique constraint")) {
                JOptionPane.showMessageDialog(null, "A player with this in-game name already exists.", "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Database error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }

    public boolean addPlayer(String fn, String ln, String ign, String rank, String role, int teamId) {
        Player player = new Player();
        player.setFirstName(fn);
        player.setLastName(ln);
        player.setInGameName(ign);
        player.setGameRank(rank);
        player.setGameRole(role);
        player.setTeamId(teamId);
        return addPlayer(player);
    }

    public boolean deletePlayer(int playerId) {
        try {
            requireRole("ORGANIZER", "COACH");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean success = false;
        try { success = getPlayerDAO().deletePlayer(playerId); } catch (Exception e) { e.printStackTrace(); }
        if (success) logAudit("PLAYER_DELETED", "ID: " + playerId);
        return success;
    }

    // ==================== Match Operations ====================
    public List<Match> getMatchesByTournament(int tid) {
        try { return getMatchDAO().getMatchesByTournament(tid); } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean processMatchResult(int matchId, int team1Score, int team2Score, int winnerId) {
        try {
            requireRole("REFEREE", "ORGANIZER");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean success = false;
        try {
            success = getMatchDAO().updateMatchResult(matchId, team1Score, team2Score, winnerId);
            if (success && winnerId != 0) {
                getEloService().updateRatings(matchId);
            }
        } catch (Exception e) { e.printStackTrace(); }
        if (success) logAudit("MATCH_RESULT_RECORDED", "Match ID: " + matchId);
        return success;
    }

    public boolean updateMatchResult(int mid, int s1, int s2, int wid) {
        return processMatchResult(mid, s1, s2, wid);
    }

    public boolean scheduleMatch(int mid, String time) {
        try {
            requireRole("ORGANIZER", "REFEREE");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean success = false;
        try { success = getMatchDAO().updateMatchSchedule(mid, time); } catch (Exception e) { e.printStackTrace(); }
        if (success) logAudit("MATCH_SCHEDULED", "Match ID: " + mid + " Time: " + time);
        return success;
    }

    public boolean createMatch(Match match) {
        try {
            requireRole("ORGANIZER");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        boolean success = false;
        try { success = getMatchDAO().createMatch(match); } catch (Exception e) { e.printStackTrace(); }
        if (success) logAudit("MATCH_CREATED", "Tournament ID: " + match.getTournamentId());
        return success;
    }

    // ==================== Match Referee Assignment ====================
    public boolean assignRefereeToMatch(int matchId, int refereeId) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            return getMatchDAO().updateMatchReferee(matchId, refereeId);
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ==================== Analytics ====================
    public Map<String, Integer> getPlayerGrowthMonthly() { try { return getPersonDAO().getMonthlyPlayerCount(); } catch (Exception e) { e.printStackTrace(); return new LinkedHashMap<>(); } }
    public Map<String, Integer> getTeamGrowthMonthly() { try { return getTeamDAO().getMonthlyTeamCount(); } catch (Exception e) { e.printStackTrace(); return new LinkedHashMap<>(); } }
    public Map<String, Integer> getTournamentMonthlyCount() { try { return getTournamentDAO().getMonthlyTournamentCount(); } catch (Exception e) { e.printStackTrace(); return new LinkedHashMap<>(); } }
    public double getTotalRevenue() { try { return getTournamentDAO().getAllTournaments().stream().mapToDouble(Tournament::getPrizePool).sum(); } catch (Exception e) { e.printStackTrace(); return 0.0; } }
    public Map<String, Double> getPrizePoolDistribution() { try { return getTournamentDAO().getAllTournaments().stream().filter(t -> t.getPrizePool() > 0).collect(java.util.stream.Collectors.toMap(Tournament::getTournamentName, Tournament::getPrizePool, (a,b)->a, LinkedHashMap::new)); } catch (Exception e) { e.printStackTrace(); return new LinkedHashMap<>(); } }
    public List<Match> getUpcomingMatches(int limit) { try { return getMatchDAO().getUpcomingMatches(limit); } catch (Exception e) { e.printStackTrace(); return new ArrayList<>(); } }
    public List<String> getRecentActivity() { List<String> feed = new ArrayList<>(); try { List<Tournament> rt = getTournamentDAO().getRecentTournaments(5); for (Tournament t : rt) feed.add("Tournament '" + t.getTournamentName() + "' created."); List<Match> rm = getMatchDAO().getRecentCompletedMatches(5); for (Match m : rm) feed.add("Match " + m.getMatchId() + ": " + m.getTeam1Name() + " vs " + m.getTeam2Name() + " completed."); } catch (Exception e) { e.printStackTrace(); } return feed; }
    public int getNotificationCount() { try { User u = UserSession.getCurrentUser(); return u != null ? getNotificationDAO().getUnreadCount(u.getUserId()) : 0; } catch (Exception e) { return 0; } }
    public Map<String, Integer> getTournamentPopularity() { try { return getRegistrationDAO().getTournamentPopularity(5); } catch (Exception e) { e.printStackTrace(); return new LinkedHashMap<>(); } }
    public Map<String, Integer> getMostActiveTeams(int limit) { try { return getMatchDAO().getMostActiveTeams(limit); } catch (Exception e) { e.printStackTrace(); return new LinkedHashMap<>(); } }
    public Map<String, Integer> getMonthlyRegistrations() { try { return getRegistrationDAO().getMonthlyRegistrations(); } catch (Exception e) { e.printStackTrace(); return new LinkedHashMap<>(); } }

    // ==================== Bracket Generation ====================
    public List<Match> generateBracket(int tournamentId) {
        try {
            requireRole("ORGANIZER");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return new ArrayList<>();
        }
        List<Match> matches = new ArrayList<>();
        try {
            Tournament tournament = getTournamentDAO().getTournamentById(tournamentId);
            if (tournament == null) {
                JOptionPane.showMessageDialog(null, "Tournament not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return matches;
            }

            List<Team> teams = getTeamDAO().getTeamsByTournament(tournamentId);

            if (teams.isEmpty()) {
                List<Team> allTeams = getTeamDAO().getAllTeams();
                int max = tournament.getMaxTeams();
                int count = 0;
                for (Team t : allTeams) {
                    if (count >= max) break;
                    getTournamentDAO().registerTeamForTournament(tournamentId, t.getTeamId(), count + 1);
                    count++;
                }
                teams = getTeamDAO().getTeamsByTournament(tournamentId);
            }

            if (teams.size() < 2) {
                JOptionPane.showMessageDialog(null, "You need at least 2 teams to generate a bracket. Please create teams first.", "Bracket Generation", JOptionPane.WARNING_MESSAGE);
                return matches;
            }

            matches = new BracketService().generateBracket(tournament, teams);
            if (!matches.isEmpty()) {
                logAudit("BRACKET_GENERATED", "Tournament ID: " + tournamentId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error during bracket generation: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return matches;
    }

    public void advanceBracket(int matchId) {
        try {
            Match match = getMatchDAO().getMatchById(matchId);
            if (match == null || !"COMPLETED".equals(match.getStatus())) return;
            List<Match> roundMatches = getMatchDAO().getMatchesByRound(match.getTournamentId(), match.getRoundNumber());
            boolean allDone = roundMatches.stream().allMatch(m -> "COMPLETED".equals(m.getStatus()));
            if (allDone) {
                Tournament tournament = getTournamentDAO().getTournamentById(match.getTournamentId());
                List<Team> winners = new ArrayList<>();
                for (Match m : roundMatches) {
                    if (m.getWinnerTeamId() != 0) {
                        Team t = getTeamDAO().getTeamById(m.getWinnerTeamId());
                        if (t != null) winners.add(t);
                    }
                }
                if (winners.size() <= 1) return;
                BracketService service = new BracketService();
                BracketGenerator generator = service.getGenerator(tournament, winners);
                List<Match> nextRound = generator.generateNextRoundMatches(roundMatches);
                for (Match nm : nextRound) {
                    getMatchDAO().createMatch(nm);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ==================== Player Performance ====================
    public Map<String, Integer> getPlayerAggregateStats(int playerId) { try { return getPlayerPerformanceDAO().getAggregateStats(playerId); } catch (SQLException e) { e.printStackTrace(); Map<String, Integer> empty = new HashMap<>(); empty.put("kills",0); empty.put("deaths",0); empty.put("assists",0); empty.put("mvps",0); return empty; } }
    public List<Map<String, Object>> getPlayerPerformance(int playerId) { try { return getPlayerPerformanceDAO().getPerformanceByPlayer(playerId); } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); } }

    // ==================== AI Match Prediction ====================
    public MatchPredictionService.Prediction getPrediction(int matchId) { try { Match match = getMatchDAO().getMatchById(matchId); if (match == null || match.getTeam1Id() == 0 || match.getTeam2Id() == 0) return new MatchPredictionService.Prediction(0,0.0,"Both teams must be set."); return getPredictionService().predict(match); } catch (SQLException e) { e.printStackTrace(); return new MatchPredictionService.Prediction(0,0.0,"Error loading match."); } }

    // ==================== NOTIFICATIONS ====================
    public List<Map<String, Object>> getNotificationsForCurrentUser() { try { User u = UserSession.getCurrentUser(); return u != null ? getNotificationDAO().getNotificationsForUser(u.getUserId()) : new ArrayList<>(); } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); } }
    public void markNotificationRead(int notificationId) { try { getNotificationDAO().markAsRead(notificationId); } catch (SQLException e) { e.printStackTrace(); } }
    public void markAllNotificationsRead() { try { User u = UserSession.getCurrentUser(); if (u != null) getNotificationDAO().markAllAsRead(u.getUserId()); } catch (SQLException e) { e.printStackTrace(); } }
    public void createNotification(int userId, String message) { try { getNotificationDAO().createNotification(userId, message); } catch (SQLException e) { e.printStackTrace(); } }
    public void notifyAdmins(String message) { try { List<User> users = getUserDAO().getAllUsers(); for (User u : users) { if ("ADMIN".equals(u.getRole()) || "ORGANIZER".equals(u.getRole())) createNotification(u.getUserId(), message); } } catch (SQLException e) { e.printStackTrace(); } }

    // ==================== AUDIT LOGGING ====================
    private void logAudit(String action, String details) { try { User current = UserSession.getCurrentUser(); Integer userId = current != null ? current.getUserId() : null; String username = current != null ? current.getUsername() : "System"; new AuditLogDAO().insertLog(userId, username, action, details); } catch (SQLException e) { e.printStackTrace(); } }

    // ==================== Roster Management ====================
    public List<Player> getTeamRoster(int teamId) {
        try { return getPlayerDAO().getPlayersByTeam(teamId); } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean assignPlayerToTeam(int playerId, int teamId, boolean isStarter, boolean isCaptain) {
        try {
            requireRole("ORGANIZER", "COACH");
            return getPlayerDAO().updatePlayerAssignment(playerId, teamId, isStarter, isCaptain);
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean removePlayerFromTeam(int playerId) {
        return assignPlayerToTeam(playerId, 0, false, false);
    }

    public boolean setCaptain(int teamId, int playerId) {
        try {
            requireRole("ORGANIZER", "COACH");
            return getTeamDAO().updateCaptain(teamId, playerId);
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    // ==================== Coach CRUD ====================
    public List<Coach> getAllCoaches() {
        try { return new CoachDAO().getAllCoaches(); } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean createCoach(Coach coach) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            if (coach.getPersonId() == 0) {
                GenericPerson person = new GenericPerson(coach.getFirstName(), coach.getLastName());
                person.setPhone(coach.getPhone());
                person.setEmail(coach.getEmail());
                int personId = getPersonDAO().createPerson(person);
                if (personId == -1) {
                    JOptionPane.showMessageDialog(null, "Failed to create person record.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                coach.setPersonId(personId);
            }
            boolean success = new CoachDAO().createCoach(coach);
            if (success) {
                logAudit("COACH_CREATED", "Coach: " + coach.getFirstName() + " " + coach.getLastName() + " (ID: " + coach.getCoachId() + ")");
                JOptionPane.showMessageDialog(null, "Coach added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to add coach.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateCoach(Coach coach) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            if (coach.getPersonId() > 0) {
                GenericPerson person = new GenericPerson(coach.getFirstName(), coach.getLastName());
                person.setPersonId(coach.getPersonId());
                person.setPhone(coach.getPhone());
                person.setEmail(coach.getEmail());
                getPersonDAO().updatePerson(person);
            }
            boolean success = new CoachDAO().updateCoach(coach);
            if (success) {
                logAudit("COACH_UPDATED", "Coach ID: " + coach.getCoachId());
                JOptionPane.showMessageDialog(null, "Coach updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to update coach.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean deleteCoach(int coachId) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            boolean success = new CoachDAO().deleteCoach(coachId);
            if (success) {
                logAudit("COACH_DELETED", "Coach ID: " + coachId);
                JOptionPane.showMessageDialog(null, "Coach deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to delete coach.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ==================== Staff CRUD ====================
    public List<Staff> getAllStaff() {
        try { return new StaffDAO().getAllStaff(); } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean createStaff(Staff staff) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            GenericPerson person = new GenericPerson(staff.getFirstName(), staff.getLastName());
            person.setPhone(staff.getPhone());
            person.setEmail(staff.getEmail());
            int personId = getPersonDAO().createPerson(person);
            if (personId == -1) {
                JOptionPane.showMessageDialog(null, "Failed to create person record.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            staff.setPersonId(personId);

            if (staff.getStatus() == null || staff.getStatus().isEmpty()) {
                staff.setStatus("ACTIVE");
            }

            boolean success = new StaffDAO().createStaff(staff);
            if (success) {
                logAudit("STAFF_CREATED", "Staff: " + staff.getFirstName() + " " + staff.getLastName() + " (ID: " + staff.getStaffId() + ")");
                JOptionPane.showMessageDialog(null, "Staff added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to add staff.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateStaff(Staff staff) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            if (staff.getPersonId() > 0) {
                GenericPerson person = new GenericPerson(staff.getFirstName(), staff.getLastName());
                person.setPersonId(staff.getPersonId());
                person.setPhone(staff.getPhone());
                person.setEmail(staff.getEmail());
                getPersonDAO().updatePerson(person);
            }

            boolean success = new StaffDAO().updateStaff(staff);
            if (success) {
                logAudit("STAFF_UPDATED", "Staff ID: " + staff.getStaffId());
                JOptionPane.showMessageDialog(null, "Staff updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to update staff.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean deleteStaff(int staffId) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            boolean success = new StaffDAO().deleteStaff(staffId);
            if (success) {
                logAudit("STAFF_DELETED", "Staff ID: " + staffId);
                JOptionPane.showMessageDialog(null, "Staff deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to delete staff.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ==================== Register team for tournament (2-arg overload) ====================
    public boolean registerTeamForTournament(int tournamentId, int teamId) {
        try {
            requireRole("ORGANIZER");
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            int seed = getTeamDAO().getTeamsByTournament(tournamentId).size() + 1;
            return getTournamentDAO().registerTeamForTournament(tournamentId, teamId, seed);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== REFEREE MANAGEMENT ====================
    
    public List<Referee> getAllReferees() {
        try { return getRefereeDAO().getAllReferees(); } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public Referee getRefereeById(int id) {
        try { return getRefereeDAO().getRefereeById(id); } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    public List<Referee> getActiveReferees() {
        try { return getRefereeDAO().getActiveReferees(); } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean createReferee(Referee referee) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            if (referee.getStatus() == null || referee.getStatus().isEmpty()) {
                referee.setStatus("ACTIVE");
            }
            if (referee.getPersonId() == 0) {
                GenericPerson person = new GenericPerson(referee.getFirstName(), referee.getLastName());
                person.setPhone(referee.getPhone());
                person.setEmail(referee.getEmail());
                int personId = getPersonDAO().createPerson(person);
                if (personId == -1) {
                    JOptionPane.showMessageDialog(null, "Failed to create person record.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                referee.setPersonId(personId);
            }
            boolean success = getRefereeDAO().createReferee(referee);
            if (success) {
                logAudit("REFEREE_CREATED", "Referee: " + referee.getFirstName() + " " + referee.getLastName() + " (ID: " + referee.getRefereeId() + ")");
                JOptionPane.showMessageDialog(null, "Referee added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to add referee.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateReferee(Referee referee) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            if (referee.getPersonId() > 0) {
                GenericPerson person = new GenericPerson(referee.getFirstName(), referee.getLastName());
                person.setPersonId(referee.getPersonId());
                person.setPhone(referee.getPhone());
                person.setEmail(referee.getEmail());
                getPersonDAO().updatePerson(person);
            }
            boolean success = getRefereeDAO().updateReferee(referee);
            if (success) {
                logAudit("REFEREE_UPDATED", "Referee ID: " + referee.getRefereeId());
                JOptionPane.showMessageDialog(null, "Referee updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to update referee.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean deleteReferee(int id) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            boolean success = getRefereeDAO().deleteReferee(id);
            if (success) {
                logAudit("REFEREE_DELETED", "Referee ID: " + id);
                JOptionPane.showMessageDialog(null, "Referee deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to delete referee.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ==================== VENUE MANAGEMENT ====================

    public List<Venue> getAllVenues() {
        try { return getVenueDAO().getAllVenues(); } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean addVenue(Venue venue) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            boolean success = getVenueDAO().createVenue(venue);
            if (success) {
                logAudit("VENUE_CREATED", "Venue: " + venue.getName() + " (ID: " + venue.getVenueId() + ")");
                JOptionPane.showMessageDialog(null, "Venue added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to add venue.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateVenue(Venue venue) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            boolean success = getVenueDAO().updateVenue(venue);
            if (success) {
                logAudit("VENUE_UPDATED", "Venue ID: " + venue.getVenueId());
                JOptionPane.showMessageDialog(null, "Venue updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to update venue.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean deleteVenue(int id) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            boolean success = getVenueDAO().deleteVenue(id);
            if (success) {
                logAudit("VENUE_DELETED", "Venue ID: " + id);
                JOptionPane.showMessageDialog(null, "Venue deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to delete venue.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ==================== EQUIPMENT MANAGEMENT ====================
    public List<Equipment> getAllEquipment() {
        try { return getEquipmentService().getAllEquipment(); } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean createEquipment(Equipment eq) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            return getEquipmentService().createEquipment(eq);
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateEquipment(Equipment eq) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            return getEquipmentService().updateEquipment(eq);
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean deleteEquipment(int id) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            return getEquipmentService().deleteEquipment(id);
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public Map<String, Integer> getEquipmentStatusCounts() {
        try { return getEquipmentService().getStatusCounts(); } catch (SQLException e) { e.printStackTrace(); return new LinkedHashMap<>(); }
    }

    public List<Equipment> searchEquipment(String query) {
        try { return getEquipmentService().searchEquipment(query); } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public List<Equipment> getEquipmentByStatus(String status) {
        try { return getEquipmentService().getEquipmentByStatus(status); } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    // ==================== SPONSOR MANAGEMENT ====================

    public List<Sponsor> getAllSponsors() {
        try { return getSponsorDAO().getAllSponsors(); } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    public boolean createSponsor(Sponsor sponsor) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            if (sponsor.getStatus() == null || sponsor.getStatus().isEmpty()) {
                sponsor.setStatus("ACTIVE");
            }
            if (sponsor.getCategory() == null || sponsor.getCategory().isEmpty()) {
                sponsor.setCategory("BRONZE");
            }
            boolean success = getSponsorDAO().createSponsor(sponsor);
            if (success) {
                logAudit("SPONSOR_CREATED", "Sponsor: " + sponsor.getCompanyName() + " (ID: " + sponsor.getSponsorId() + ")");
                JOptionPane.showMessageDialog(null, "Sponsor added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to add sponsor.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updateSponsor(Sponsor sponsor) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            boolean success = getSponsorDAO().updateSponsor(sponsor);
            if (success) {
                logAudit("SPONSOR_UPDATED", "Sponsor ID: " + sponsor.getSponsorId());
                JOptionPane.showMessageDialog(null, "Sponsor updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to update sponsor.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean deleteSponsor(int id) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            boolean success = getSponsorDAO().deleteSponsor(id);
            if (success) {
                logAudit("SPONSOR_DELETED", "Sponsor ID: " + id);
                JOptionPane.showMessageDialog(null, "Sponsor deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to delete sponsor.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ==================== PRIZE POOL MANAGEMENT ====================

    public List<PrizeDistribution> getPrizeDistributions(int tournamentId) {
        try {
            return getPrizeDistributionDAO().getDistributionsByTournament(tournamentId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean createPrizeDistribution(PrizeDistribution distribution) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            boolean success = getPrizeDistributionDAO().createDistribution(distribution);
            if (success) {
                logAudit("PRIZE_DISTRIBUTION_CREATED", "Tournament ID: " + distribution.getTournamentId() +
                        ", Position: " + distribution.getPosition());
                JOptionPane.showMessageDialog(null, "Prize distribution added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to add prize distribution.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean updatePrizeDistribution(PrizeDistribution distribution) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            boolean success = getPrizeDistributionDAO().updateDistribution(distribution);
            if (success) {
                logAudit("PRIZE_DISTRIBUTION_UPDATED", "Distribution ID: " + distribution.getDistributionId());
                JOptionPane.showMessageDialog(null, "Prize distribution updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to update prize distribution.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean deletePrizeDistribution(int id) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            boolean success = getPrizeDistributionDAO().deleteDistribution(id);
            if (success) {
                logAudit("PRIZE_DISTRIBUTION_DELETED", "Distribution ID: " + id);
                JOptionPane.showMessageDialog(null, "Prize distribution deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
            JOptionPane.showMessageDialog(null, "Failed to delete prize distribution.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ==================== PRIZE POOL GENERATION (Custom) ====================

    /**
     * Generates prize distributions for a tournament using a custom list.
     * Deletes any existing distributions for the tournament first.
     */
    public boolean generatePrizeDistributions(int tournamentId, List<PrizeDistribution> distributions) {
        try {
            requireRole("ORGANIZER", "ADMIN");
            // Validate tournament exists
            Tournament tournament = getTournamentDAO().getTournamentById(tournamentId);
            if (tournament == null) {
                JOptionPane.showMessageDialog(null, "Tournament not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Delete existing distributions
            PrizeDistributionDAO pdDAO = getPrizeDistributionDAO();
            pdDAO.deleteDistributionsByTournament(tournamentId);

            // Insert new distributions
            for (PrizeDistribution pd : distributions) {
                pd.setTournamentId(tournamentId);
                pd.setAwarded(false);
                boolean success = pdDAO.createDistribution(pd);
                if (!success) {
                    throw new SQLException("Failed to create distribution for position " + pd.getPosition());
                }
            }
            logAudit("PRIZE_DISTRIBUTIONS_GENERATED", "Tournament ID: " + tournamentId + " with " + distributions.size() + " ranks");
            return true;
        } catch (SecurityException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Access Denied", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}