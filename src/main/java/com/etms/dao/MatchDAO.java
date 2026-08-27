package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.Match;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class MatchDAO {

    private final DatabaseConfig db = DatabaseConfig.getInstance();

    public boolean createMatch(Match match) throws SQLException {
        String sql = "INSERT INTO matches (tournament_id, round_number, match_number, team1_id, team2_id, winner_team_id, team1_score, team2_score, scheduled_time, status, referee_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, match.getTournamentId());
            ps.setInt(2, match.getRoundNumber());
            ps.setInt(3, match.getMatchNumber());
            if (match.getTeam1Id() > 0) ps.setInt(4, match.getTeam1Id());
            else ps.setNull(4, Types.INTEGER);
            if (match.getTeam2Id() > 0) ps.setInt(5, match.getTeam2Id());
            else ps.setNull(5, Types.INTEGER);
            if (match.getWinnerTeamId() > 0) ps.setInt(6, match.getWinnerTeamId());
            else ps.setNull(6, Types.INTEGER);
            ps.setInt(7, match.getTeam1Score());
            ps.setInt(8, match.getTeam2Score());
            if (match.getScheduledTime() != null) ps.setTimestamp(9, Timestamp.valueOf(match.getScheduledTime()));
            else ps.setNull(9, Types.TIMESTAMP);
            ps.setString(10, match.getStatus());
            if (match.getRefereeId() > 0) ps.setInt(11, match.getRefereeId());
            else ps.setNull(11, Types.INTEGER);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) match.setMatchId(rs.getInt(1));
                    return true;
                }
            }
            return false;
        }
    }

    public List<Match> getAllMatches() throws SQLException {
        List<Match> list = new ArrayList<>();
        String sql = "SELECT m.*, " +
                     "r.first_name, r.last_name, " +
                     "t1.team_name AS team1_name, " +
                     "t2.team_name AS team2_name, " +
                     "tw.team_name AS winner_team_name " +
                     "FROM matches m " +
                     "LEFT JOIN referees r ON m.referee_id = r.referee_id " +
                     "LEFT JOIN teams t1 ON m.team1_id = t1.team_id " +
                     "LEFT JOIN teams t2 ON m.team2_id = t2.team_id " +
                     "LEFT JOIN teams tw ON m.winner_team_id = tw.team_id " +
                     "ORDER BY m.scheduled_time";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Match getMatchById(int id) throws SQLException {
        String sql = "SELECT m.*, " +
                     "r.first_name, r.last_name, " +
                     "t1.team_name AS team1_name, " +
                     "t2.team_name AS team2_name, " +
                     "tw.team_name AS winner_team_name " +
                     "FROM matches m " +
                     "LEFT JOIN referees r ON m.referee_id = r.referee_id " +
                     "LEFT JOIN teams t1 ON m.team1_id = t1.team_id " +
                     "LEFT JOIN teams t2 ON m.team2_id = t2.team_id " +
                     "LEFT JOIN teams tw ON m.winner_team_id = tw.team_id " +
                     "WHERE m.match_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Match> getMatchesByTournament(int tournamentId) throws SQLException {
        List<Match> list = new ArrayList<>();
        String sql = "SELECT m.*, " +
                     "r.first_name, r.last_name, " +
                     "t1.team_name AS team1_name, " +
                     "t2.team_name AS team2_name, " +
                     "tw.team_name AS winner_team_name " +
                     "FROM matches m " +
                     "LEFT JOIN referees r ON m.referee_id = r.referee_id " +
                     "LEFT JOIN teams t1 ON m.team1_id = t1.team_id " +
                     "LEFT JOIN teams t2 ON m.team2_id = t2.team_id " +
                     "LEFT JOIN teams tw ON m.winner_team_id = tw.team_id " +
                     "WHERE m.tournament_id = ? " +
                     "ORDER BY m.round_number, m.match_number";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Match> getMatchesByRound(int tournamentId, int round) throws SQLException {
        List<Match> list = new ArrayList<>();
        String sql = "SELECT m.*, " +
                     "r.first_name, r.last_name, " +
                     "t1.team_name AS team1_name, " +
                     "t2.team_name AS team2_name, " +
                     "tw.team_name AS winner_team_name " +
                     "FROM matches m " +
                     "LEFT JOIN referees r ON m.referee_id = r.referee_id " +
                     "LEFT JOIN teams t1 ON m.team1_id = t1.team_id " +
                     "LEFT JOIN teams t2 ON m.team2_id = t2.team_id " +
                     "LEFT JOIN teams tw ON m.winner_team_id = tw.team_id " +
                     "WHERE m.tournament_id = ? AND m.round_number = ? " +
                     "ORDER BY m.match_number";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tournamentId);
            ps.setInt(2, round);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public boolean updateMatchResult(int matchId, int team1Score, int team2Score, int winnerId) throws SQLException {
        String sql = "UPDATE matches SET team1_score=?, team2_score=?, winner_team_id=?, status='COMPLETED' WHERE match_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, team1Score);
            ps.setInt(2, team2Score);
            if (winnerId > 0) ps.setInt(3, winnerId);
            else ps.setNull(3, Types.INTEGER);
            ps.setInt(4, matchId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateMatchSchedule(int matchId, String scheduledTime) throws SQLException {
        String sql = "UPDATE matches SET scheduled_time=?, status='SCHEDULED' WHERE match_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (scheduledTime != null) ps.setTimestamp(1, Timestamp.valueOf(scheduledTime));
            else ps.setNull(1, Types.TIMESTAMP);
            ps.setInt(2, matchId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean updateMatchReferee(int matchId, int refereeId) throws SQLException {
        String sql = "UPDATE matches SET referee_id=? WHERE match_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (refereeId > 0) ps.setInt(1, refereeId);
            else ps.setNull(1, Types.INTEGER);
            ps.setInt(2, matchId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteMatch(int matchId) throws SQLException {
        String sql = "DELETE FROM matches WHERE match_id=?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, matchId);
            return ps.executeUpdate() > 0;
        }
    }

    public int getScheduledMatches() throws SQLException {
        String sql = "SELECT COUNT(*) FROM matches WHERE status='SCHEDULED'";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getCompletedMatches() throws SQLException {
        String sql = "SELECT COUNT(*) FROM matches WHERE status='COMPLETED'";
        try (Connection conn = db.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<Match> getUpcomingMatches(int limit) throws SQLException {
        List<Match> list = new ArrayList<>();
        String sql = "SELECT m.*, " +
                     "r.first_name, r.last_name, " +
                     "t1.team_name AS team1_name, " +
                     "t2.team_name AS team2_name, " +
                     "tw.team_name AS winner_team_name " +
                     "FROM matches m " +
                     "LEFT JOIN referees r ON m.referee_id = r.referee_id " +
                     "LEFT JOIN teams t1 ON m.team1_id = t1.team_id " +
                     "LEFT JOIN teams t2 ON m.team2_id = t2.team_id " +
                     "LEFT JOIN teams tw ON m.winner_team_id = tw.team_id " +
                     "WHERE m.status='SCHEDULED' AND m.scheduled_time IS NOT NULL " +
                     "ORDER BY m.scheduled_time ASC LIMIT ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Match> getRecentCompletedMatches(int limit) throws SQLException {
        List<Match> list = new ArrayList<>();
        String sql = "SELECT m.*, " +
                     "r.first_name, r.last_name, " +
                     "t1.team_name AS team1_name, " +
                     "t2.team_name AS team2_name, " +
                     "tw.team_name AS winner_team_name " +
                     "FROM matches m " +
                     "LEFT JOIN referees r ON m.referee_id = r.referee_id " +
                     "LEFT JOIN teams t1 ON m.team1_id = t1.team_id " +
                     "LEFT JOIN teams t2 ON m.team2_id = t2.team_id " +
                     "LEFT JOIN teams tw ON m.winner_team_id = tw.team_id " +
                     "WHERE m.status='COMPLETED' " +
                     "ORDER BY m.scheduled_time DESC LIMIT ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Map<String, Integer> getMostActiveTeams(int limit) throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT t.team_name, COUNT(*) as cnt FROM matches m " +
                     "JOIN teams t ON t.team_id = m.team1_id OR t.team_id = m.team2_id " +
                     "GROUP BY t.team_name ORDER BY cnt DESC LIMIT ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) map.put(rs.getString("team_name"), rs.getInt("cnt"));
        }
        return map;
    }

    // ========== NEW METHODS ==========
    public Map<String, Integer> getHeadToHeadRecord(int team1Id, int team2Id) throws SQLException {
        Map<String, Integer> record = new LinkedHashMap<>();
        int winsA = 0, winsB = 0;
        String sql = "SELECT winner_team_id FROM matches WHERE (team1_id = ? AND team2_id = ?) OR (team1_id = ? AND team2_id = ?) AND status = 'COMPLETED'";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, team1Id);
            ps.setInt(2, team2Id);
            ps.setInt(3, team2Id);
            ps.setInt(4, team1Id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int winner = rs.getInt("winner_team_id");
                    if (winner == team1Id) winsA++;
                    else if (winner == team2Id) winsB++;
                }
            }
        }
        record.put("team1Wins", winsA);
        record.put("team2Wins", winsB);
        return record;
    }

    public List<Match> search(String query) throws SQLException {
        List<Match> list = new ArrayList<>();
        String sql = "SELECT m.*, " +
                     "r.first_name, r.last_name, " +
                     "t1.team_name AS team1_name, " +
                     "t2.team_name AS team2_name, " +
                     "tw.team_name AS winner_team_name " +
                     "FROM matches m " +
                     "LEFT JOIN tournaments t ON m.tournament_id = t.tournament_id " +
                     "LEFT JOIN teams t1 ON m.team1_id = t1.team_id " +
                     "LEFT JOIN teams t2 ON m.team2_id = t2.team_id " +
                     "LEFT JOIN teams tw ON m.winner_team_id = tw.team_id " +
                     "LEFT JOIN referees r ON m.referee_id = r.referee_id " +
                     "WHERE t.tournament_name ILIKE ? OR t1.team_name ILIKE ? OR t2.team_name ILIKE ? OR m.status ILIKE ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String like = "%" + query + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public String getTeamName(int teamId) throws SQLException {
        String sql = "SELECT team_name FROM teams WHERE team_id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teamId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("team_name");
            }
        }
        return "Unknown";
    }

    private Match mapRow(ResultSet rs) throws SQLException {
        Match m = new Match();
        m.setMatchId(rs.getInt("match_id"));
        m.setTournamentId(rs.getInt("tournament_id"));
        m.setRoundNumber(rs.getInt("round_number"));
        m.setMatchNumber(rs.getInt("match_number"));
        m.setTeam1Id(rs.getInt("team1_id"));
        m.setTeam2Id(rs.getInt("team2_id"));
        m.setWinnerTeamId(rs.getInt("winner_team_id"));
        m.setTeam1Score(rs.getInt("team1_score"));
        m.setTeam2Score(rs.getInt("team2_score"));
        Timestamp ts = rs.getTimestamp("scheduled_time");
        m.setScheduledTime(ts != null ? ts.toLocalDateTime().toString() : null);
        m.setStatus(rs.getString("status"));
        m.setRefereeId(rs.getInt("referee_id"));
        // Names from joins
        m.setTeam1Name(rs.getString("team1_name"));
        m.setTeam2Name(rs.getString("team2_name"));
        m.setWinnerTeamName(rs.getString("winner_team_name"));
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        m.setRefereeName((firstName != null && lastName != null) ? firstName + " " + lastName : "None");
        return m;
    }
}