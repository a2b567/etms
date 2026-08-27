package com.etms.service;

import com.etms.dao.TeamDAO;
import com.etms.model.Team;
import java.sql.SQLException;
import java.util.List;

public class TeamService {

    private final TeamDAO dao = new TeamDAO();

    public boolean createTeam(String name, String tag) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name is required");
        }
        Team team = new Team(name, tag);
        return dao.createTeam(team);
    }

    public List<Team> getAllTeams() throws SQLException {
        return dao.getAllTeams();
    }

    public Team getTeamById(int id) throws SQLException {
        return dao.getTeamById(id);
    }

    public boolean updateTeam(Team team) throws SQLException {
        if (team.getTeamId() <= 0) throw new IllegalArgumentException("Invalid team ID");
        return dao.updateTeam(team);
    }

    public boolean deleteTeam(int id) throws SQLException {
        if (id <= 0) throw new IllegalArgumentException("Invalid team ID");
        return dao.deleteTeam(id);
    }

    public int getTotalTeams() throws SQLException {
        return dao.getTotalTeams();
    }

    public List<Team> getTeamsForTournament(int tournamentId) throws SQLException {
        return dao.getTeamsByTournament(tournamentId);
    }
}