package com.etms.dao;

import com.etms.config.DatabaseConfig;
import com.etms.model.Game;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameDAO {

    private final DatabaseConfig dbConfig = DatabaseConfig.getInstance();

    // ------------------ Existing Methods ------------------
    public List<Game> getAllGames() throws SQLException {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM games ORDER BY name";
        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Game g = new Game();
                g.setGameId(rs.getInt("game_id"));
                g.setName(rs.getString("name"));
                g.setDefaultPlayerCount(rs.getInt("default_player_count"));
                g.setDescription(rs.getString("description"));
                games.add(g);
            }
        }
        return games;
    }

    public boolean addGame(Game game) throws SQLException {
        String sql = "INSERT INTO games (name, default_player_count, description) VALUES (?,?,?)";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, game.getName());
            ps.setInt(2, game.getDefaultPlayerCount());
            ps.setString(3, game.getDescription());
            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    game.setGameId(rs.getInt(1));
                    return true;
                }
            }
        }
        return false;
    }

    public boolean deleteGame(int gameId) throws SQLException {
        String sql = "DELETE FROM games WHERE game_id=?";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameId);
            return ps.executeUpdate() > 0;
        }
    }

    // ------------------ NEW: Search ------------------
    public List<Game> search(String query) throws SQLException {
        List<Game> list = new ArrayList<>();
        String sql = "SELECT * FROM games WHERE name ILIKE ? ORDER BY name";
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Game g = new Game();
                g.setGameId(rs.getInt("game_id"));
                g.setName(rs.getString("name"));
                g.setDefaultPlayerCount(rs.getInt("default_player_count"));
                g.setDescription(rs.getString("description"));
                list.add(g);
            }
        }
        return list;
    }
}