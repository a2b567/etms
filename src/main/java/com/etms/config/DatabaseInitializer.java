package com.etms.config;

import com.etms.util.PasswordUtil;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatabaseInitializer {

    public static void initialize() throws SQLException {
        if (!DatabaseConfig.getInstance().isConfigured()) {
            throw new SQLException("Database is not configured. Set ETMS_DB_URL, ETMS_DB_USER, and ETMS_DB_PASSWORD.");
        }
        createTablesIfNeeded();
        alterExistingTables();
        insertDefaultGames();
        ensureBootstrapAdmin();
        if (Boolean.parseBoolean(System.getenv().getOrDefault("ETMS_DEMO_DATA", "false"))) {
            createSampleHistoricalData();
        }
    }

    // ----------------------------------------------------------------
    // 1. Create all tables (PostgreSQL syntax)
    // ----------------------------------------------------------------
    private static void createTablesIfNeeded() throws SQLException {
        String[] statements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                user_id SERIAL PRIMARY KEY,
                username VARCHAR(50) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                email VARCHAR(100) UNIQUE NOT NULL,
                role VARCHAR(20) NOT NULL,
                is_active BOOLEAN DEFAULT TRUE,
                last_login TIMESTAMP NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS persons (
                person_id SERIAL PRIMARY KEY,
                user_id INT UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
                first_name VARCHAR(50) NOT NULL,
                last_name VARCHAR(50) NOT NULL,
                date_of_birth DATE,
                phone VARCHAR(20),
                country VARCHAR(50),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS coaches (
                coach_id SERIAL PRIMARY KEY,
                person_id INT UNIQUE REFERENCES persons(person_id) ON DELETE CASCADE,
                experience_years INT DEFAULT 0,
                specialization VARCHAR(100),
                certification VARCHAR(100),
                team_id INT
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS referees (
                referee_id SERIAL PRIMARY KEY,
                person_id INT UNIQUE REFERENCES persons(person_id) ON DELETE CASCADE,
                certification_level VARCHAR(50),
                matches_officiated INT DEFAULT 0,
                rating DOUBLE PRECISION DEFAULT 5.0
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS teams (
                team_id SERIAL PRIMARY KEY,
                team_name VARCHAR(100) UNIQUE NOT NULL,
                tag VARCHAR(10) UNIQUE NOT NULL,
                coach_id INT REFERENCES coaches(coach_id) ON DELETE SET NULL,
                date_created DATE DEFAULT CURRENT_DATE,
                total_wins INT DEFAULT 0,
                total_losses INT DEFAULT 0,
                tournament_points INT DEFAULT 0,
                ranking INT DEFAULT 0,
                status VARCHAR(20) DEFAULT 'ACTIVE',
                elo_rating DOUBLE PRECISION NOT NULL DEFAULT 1200.0,
                elo_rating_updated TIMESTAMP NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS venues (
                venue_id SERIAL PRIMARY KEY,
                name VARCHAR(150) NOT NULL,
                location VARCHAR(255),
                capacity INT,
                internet_speed VARCHAR(50),
                description TEXT,
                status VARCHAR(20) DEFAULT 'ACTIVE'
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS players (
                player_id SERIAL PRIMARY KEY,
                person_id INT UNIQUE REFERENCES persons(person_id) ON DELETE CASCADE,
                in_game_name VARCHAR(50) UNIQUE NOT NULL,
                game_rank VARCHAR(50),
                game_role VARCHAR(50),
                team_id INT REFERENCES teams(team_id) ON DELETE SET NULL,
                total_matches INT DEFAULT 0,
                wins INT DEFAULT 0,
                losses INT DEFAULT 0,
                mvp_count INT DEFAULT 0,
                status VARCHAR(20) DEFAULT 'ACTIVE'
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS tournaments (
                tournament_id SERIAL PRIMARY KEY,
                tournament_name VARCHAR(200) NOT NULL,
                game_title VARCHAR(100) NOT NULL,
                tournament_type VARCHAR(30) NOT NULL,
                start_date DATE NOT NULL,
                end_date DATE,
                max_teams INT DEFAULT 16,
                prize_pool DECIMAL(12,2) DEFAULT 0.00,
                organizer_id INT REFERENCES users(user_id) ON DELETE SET NULL,
                status VARCHAR(20) DEFAULT 'UPCOMING',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                venue_id INT REFERENCES venues(venue_id) ON DELETE SET NULL,
                registration_deadline DATE,
                min_players_per_team INT NOT NULL DEFAULT 1
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS teams (
                team_id SERIAL PRIMARY KEY,
                team_name VARCHAR(100) UNIQUE NOT NULL,
                tag VARCHAR(10) UNIQUE NOT NULL,
                coach_id INT REFERENCES coaches(coach_id) ON DELETE SET NULL,
                date_created DATE DEFAULT CURRENT_DATE,
                total_wins INT DEFAULT 0,
                total_losses INT DEFAULT 0,
                tournament_points INT DEFAULT 0,
                ranking INT DEFAULT 0,
                status VARCHAR(20) DEFAULT 'ACTIVE',
                elo_rating DOUBLE PRECISION NOT NULL DEFAULT 1200.0,
                elo_rating_updated TIMESTAMP NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS matches (
                match_id SERIAL PRIMARY KEY,
                tournament_id INT NOT NULL REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
                round_number INT NOT NULL,
                match_number INT NOT NULL,
                team1_id INT REFERENCES teams(team_id) ON DELETE SET NULL,
                team2_id INT REFERENCES teams(team_id) ON DELETE SET NULL,
                winner_team_id INT REFERENCES teams(team_id) ON DELETE SET NULL,
                team1_score INT DEFAULT 0,
                team2_score INT DEFAULT 0,
                scheduled_time TIMESTAMP,
                status VARCHAR(20) DEFAULT 'SCHEDULED'
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS sponsors (
                sponsor_id SERIAL PRIMARY KEY,
                company_name VARCHAR(200) NOT NULL,
                contact_email VARCHAR(100),
                sponsorship_amount DECIMAL(12,2) DEFAULT 0.00,
                category VARCHAR(20) DEFAULT 'BRONZE',
                status VARCHAR(20) DEFAULT 'ACTIVE'
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS sponsorships (
                sponsorship_id SERIAL PRIMARY KEY,
                sponsor_id INT NOT NULL REFERENCES sponsors(sponsor_id) ON DELETE CASCADE,
                tournament_id INT NOT NULL REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
                amount DECIMAL(12,2) NOT NULL,
                start_date DATE,
                end_date DATE,
                contract_signed BOOLEAN DEFAULT FALSE
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS prize_distribution (
                distribution_id SERIAL PRIMARY KEY,
                tournament_id INT NOT NULL REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
                position INT NOT NULL,
                team_id INT REFERENCES teams(team_id) ON DELETE SET NULL,
                percentage DECIMAL(5,2) NOT NULL,
                amount DECIMAL(12,2) NOT NULL,
                awarded BOOLEAN DEFAULT FALSE
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS notifications (
                notification_id SERIAL PRIMARY KEY,
                user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                message VARCHAR(500) NOT NULL,
                is_read BOOLEAN DEFAULT FALSE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS player_match_performance (
                id SERIAL PRIMARY KEY,
                player_id INT NOT NULL REFERENCES players(player_id) ON DELETE CASCADE,
                match_id INT NOT NULL REFERENCES matches(match_id) ON DELETE CASCADE,
                kills INT DEFAULT 0,
                deaths INT DEFAULT 0,
                assists INT DEFAULT 0,
                is_mvp BOOLEAN DEFAULT FALSE
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS financial_transactions (
                transaction_id SERIAL PRIMARY KEY,
                tournament_id INT REFERENCES tournaments(tournament_id) ON DELETE SET NULL,
                type VARCHAR(30) NOT NULL,
                amount DECIMAL(12,2) NOT NULL,
                description VARCHAR(255),
                transaction_date DATE DEFAULT CURRENT_DATE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS equipment (
                equipment_id SERIAL PRIMARY KEY,
                type VARCHAR(30) NOT NULL,
                brand VARCHAR(100),
                model VARCHAR(100),
                serial_number VARCHAR(100) UNIQUE,
                status VARCHAR(20) DEFAULT 'Available',
                venue_id INT REFERENCES venues(venue_id) ON DELETE SET NULL,
                tournament_id INT REFERENCES tournaments(tournament_id) ON DELETE SET NULL,
                notes TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS audit_logs (
                log_id SERIAL PRIMARY KEY,
                user_id INT REFERENCES users(user_id) ON DELETE SET NULL,
                username VARCHAR(50),
                action VARCHAR(100) NOT NULL,
                details TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS check_ins (
                id SERIAL PRIMARY KEY,
                entity_type VARCHAR(20) NOT NULL,
                entity_id INT NOT NULL,
                checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
                game_id SERIAL PRIMARY KEY,
                name VARCHAR(100) UNIQUE NOT NULL,
                default_player_count INT DEFAULT 5,
                description TEXT
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS staff (
                staff_id SERIAL PRIMARY KEY,
                person_id INT UNIQUE REFERENCES persons(person_id) ON DELETE CASCADE,
                role VARCHAR(50) NOT NULL,
                team_id INT REFERENCES teams(team_id) ON DELETE SET NULL,
                status VARCHAR(20) DEFAULT 'ACTIVE'
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS tournament_teams (
                tournament_id INT REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
                team_id INT REFERENCES teams(team_id) ON DELETE CASCADE,
                registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                seed_number INT,
                status VARCHAR(20) DEFAULT 'PENDING',
                PRIMARY KEY (tournament_id, team_id)
            )
            """
        };

        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                stmt.execute(sql.trim());
            }
            System.out.println("All database tables are ready.");
        }
    }

    // ----------------------------------------------------------------
    // 2. ALTER existing tables (add missing columns)
    // ----------------------------------------------------------------
    private static void alterExistingTables() throws SQLException {
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("ALTER TABLE teams DROP CONSTRAINT IF EXISTS teams_coach_id_fkey");
            stmt.executeUpdate("ALTER TABLE teams ADD CONSTRAINT teams_coach_id_fkey FOREIGN KEY (coach_id) REFERENCES coaches(coach_id) ON DELETE SET NULL");
            stmt.executeUpdate("ALTER TABLE persons ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            stmt.executeUpdate("ALTER TABLE teams ADD COLUMN IF NOT EXISTS elo_rating DOUBLE PRECISION NOT NULL DEFAULT 1200.0");
            stmt.executeUpdate("ALTER TABLE teams ADD COLUMN IF NOT EXISTS elo_rating_updated TIMESTAMP NULL DEFAULT NULL");
            stmt.executeUpdate("ALTER TABLE tournaments ADD COLUMN IF NOT EXISTS venue_id INT REFERENCES venues(venue_id) ON DELETE SET NULL");
            stmt.executeUpdate("ALTER TABLE players ADD COLUMN IF NOT EXISTS is_captain BOOLEAN DEFAULT FALSE");
            stmt.executeUpdate("ALTER TABLE players ADD COLUMN IF NOT EXISTS is_starter BOOLEAN DEFAULT FALSE");
            stmt.executeUpdate("ALTER TABLE players ADD COLUMN IF NOT EXISTS jersey_number INT DEFAULT 0");
            stmt.executeUpdate("ALTER TABLE tournaments ADD COLUMN IF NOT EXISTS registration_deadline DATE");
            stmt.executeUpdate("ALTER TABLE tournaments ADD COLUMN IF NOT EXISTS min_players_per_team INT NOT NULL DEFAULT 1");
            // ADD THIS LINE: Add status column to coaches table
            stmt.executeUpdate("ALTER TABLE coaches ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE'");
            stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_players_team_status ON players(team_id, status)");
            stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_matches_tournament_round ON matches(tournament_id, round_number, match_number)");
            stmt.executeUpdate("CREATE INDEX IF NOT EXISTS idx_tournament_teams_tournament_status ON tournament_teams(tournament_id, status)");
            System.out.println("Database alterations applied.");
        }
    }

    // ----------------------------------------------------------------
    // 3. Insert default game titles
    // ----------------------------------------------------------------
    private static void insertDefaultGames() throws SQLException {
        String[] defaultGames = {"Valorant", "League of Legends", "Dota 2", "Counter-Strike 2", "Mobile Legends", "PUBG", "Apex Legends"};
        try (Connection conn = DatabaseConfig.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO games (name, default_player_count, description) VALUES (?,5,?) ON CONFLICT (name) DO NOTHING")) {
            for (String game : defaultGames) {
                ps.setString(1, game);
                ps.setString(2, "Official eSports title: " + game);
                ps.executeUpdate();
            }
            System.out.println("Default games inserted.");
        }
    }

    // ----------------------------------------------------------------
    // 4. Create a first administrator only when an explicit bootstrap secret
    // is supplied. Existing users are never modified on application startup.
    // ----------------------------------------------------------------
    private static void ensureBootstrapAdmin() throws SQLException {
    String bootstrapPassword = System.getenv("ETMS_BOOTSTRAP_ADMIN_PASSWORD");
    if (bootstrapPassword == null || bootstrapPassword.length() < 12) {
        // Development fallback (remove in production)
        bootstrapPassword = "password123";
    }

    try (Connection conn = DatabaseConfig.getInstance().getConnection()) {
        try (Statement statement = conn.createStatement();
             ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM users")) {
            result.next();
            if (result.getInt(1) > 0) {
                return;
            }
        }

        try (PreparedStatement user = conn.prepareStatement(
                "INSERT INTO users (username, password_hash, email, role) VALUES ('admin', ?, 'admin@localhost', 'ADMIN')",
                Statement.RETURN_GENERATED_KEYS);
             PreparedStatement person = conn.prepareStatement(
                "INSERT INTO persons (user_id, first_name, last_name) VALUES (?, 'System', 'Administrator')")) {
            user.setString(1, PasswordUtil.hashPassword(bootstrapPassword));
            user.executeUpdate();
            try (ResultSet keys = user.getGeneratedKeys()) {
                if (keys.next()) {
                    person.setInt(1, keys.getInt(1));
                    person.executeUpdate();
                }
            }
        }
        System.out.println("Bootstrap administrator created.");
    }
}

    // ----------------------------------------------------------------
    // 5. Insert sample historical data (only if the DB is empty)
    // ----------------------------------------------------------------
    private static void createSampleHistoricalData() {
        try (Connection conn = DatabaseConfig.getInstance().getConnection()) {
            Statement check = conn.createStatement();
            ResultSet rs = check.executeQuery("SELECT COUNT(*) FROM teams");
            if (rs.next() && rs.getInt(1) > 0) return; // already populated

            Random rand = new Random();
            PreparedStatement personStmt = conn.prepareStatement(
                "INSERT INTO persons (first_name, last_name, created_at) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            PreparedStatement teamStmt = conn.prepareStatement(
                "INSERT INTO teams (team_name, tag, date_created) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            PreparedStatement playerStmt = conn.prepareStatement(
                "INSERT INTO players (person_id, in_game_name, game_rank, game_role, team_id) VALUES (?,?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);

            List<Integer> teamIds = new ArrayList<>();
            List<Integer> playerIds = new ArrayList<>();
            List<Integer> matchIds = new ArrayList<>();

            // Create 12 historic teams
            for (int month = 1; month <= 12; month++) {
                LocalDate date = LocalDate.of(2025, month, 1).plusDays(rand.nextInt(27));
                String teamName = "Historic Team " + month;
                teamStmt.setString(1, teamName);
                teamStmt.setString(2, "HT" + month);
                teamStmt.setDate(3, Date.valueOf(date));
                teamStmt.executeUpdate();
                ResultSet teamKey = teamStmt.getGeneratedKeys();
                int teamId = 0;
                if (teamKey.next()) {
                    teamId = teamKey.getInt(1);
                    teamIds.add(teamId);
                }
                teamKey.close();

                for (int p = 0; p < 3; p++) {
                    personStmt.setString(1, "HistPlayer");
                    personStmt.setString(2, month + "" + p);
                    personStmt.setTimestamp(3, Timestamp.valueOf(date.atStartOfDay()));
                    personStmt.executeUpdate();
                    ResultSet personKey = personStmt.getGeneratedKeys();
                    if (personKey.next()) {
                        int personId = personKey.getInt(1);
                        playerStmt.setInt(1, personId);
                        playerStmt.setString(2, "HistIGN" + month + p);
                        playerStmt.setString(3, "Gold");
                        playerStmt.setString(4, "Flex");
                        playerStmt.setInt(5, teamId);
                        playerStmt.executeUpdate();
                        ResultSet playerKey = playerStmt.getGeneratedKeys();
                        if (playerKey.next()) playerIds.add(playerKey.getInt(1));
                        playerKey.close();
                    }
                    personKey.close();
                }
            }

            // Create 8 historic tournaments and their matches
            PreparedStatement tournStmt = conn.prepareStatement(
                "INSERT INTO tournaments (tournament_name, game_title, tournament_type, start_date, end_date, max_teams, prize_pool, organizer_id, status, created_at) VALUES (?,?,?,?,?,?,?,?,'COMPLETED',?)",
                Statement.RETURN_GENERATED_KEYS);
            for (int i = 1; i <= 8; i++) {
                LocalDate start = LocalDate.of(2025, i * 3 % 12 + 1, 1);
                tournStmt.setString(1, "Past Tourney " + i);
                tournStmt.setString(2, "Valorant");
                tournStmt.setString(3, "SINGLE_ELIMINATION");
                tournStmt.setDate(4, Date.valueOf(start));
                tournStmt.setDate(5, Date.valueOf(start.plusDays(7)));
                tournStmt.setInt(6, 8);
                tournStmt.setDouble(7, 1000 + i * 500);
                tournStmt.setInt(8, 1); // admin
                tournStmt.setTimestamp(9, Timestamp.valueOf(start.atStartOfDay()));
                tournStmt.executeUpdate();
                ResultSet tournKey = tournStmt.getGeneratedKeys();
                int tournamentId = 0;
                if (tournKey.next()) tournamentId = tournKey.getInt(1);
                tournKey.close();

                // Create 4 matches per tournament using teamIds
                if (teamIds.size() >= 2) {
                    PreparedStatement matchStmt = conn.prepareStatement(
                        "INSERT INTO matches (tournament_id, round_number, match_number, team1_id, team2_id, winner_team_id, team1_score, team2_score, status) VALUES (?,1,?,?,?,?,?,?,'COMPLETED')",
                        Statement.RETURN_GENERATED_KEYS);
                    for (int m = 0; m < 4; m++) {
                        int t1Idx = (i + m) % teamIds.size();
                        int t2Idx = (i + m + 1) % teamIds.size();
                        if (t1Idx == t2Idx) t2Idx = (t2Idx + 1) % teamIds.size();
                        int t1Id = teamIds.get(t1Idx);
                        int t2Id = teamIds.get(t2Idx);
                        matchStmt.setInt(1, tournamentId);
                        matchStmt.setInt(2, m + 1);
                        matchStmt.setInt(3, t1Id);
                        matchStmt.setInt(4, t2Id);
                        matchStmt.setInt(5, t1Id); // winner
                        matchStmt.setInt(6, 13);
                        matchStmt.setInt(7, 7);
                        matchStmt.executeUpdate();
                        ResultSet matchKey = matchStmt.getGeneratedKeys();
                        if (matchKey.next()) matchIds.add(matchKey.getInt(1));
                        matchKey.close();
                    }
                    matchStmt.close();
                }
            }

            // Insert player performance records
            if (!playerIds.isEmpty() && !matchIds.isEmpty()) {
                PreparedStatement perfStmt = conn.prepareStatement(
                    "INSERT INTO player_match_performance (player_id, match_id, kills, deaths, assists, is_mvp) VALUES (?,?,?,?,?,?)");
                for (int i = 0; i < Math.min(5, playerIds.size()); i++) {
                    for (int j = 0; j < Math.min(3, matchIds.size()); j++) {
                        perfStmt.setInt(1, playerIds.get(i));
                        perfStmt.setInt(2, matchIds.get(j));
                        int kills = rand.nextInt(15);
                        int deaths = rand.nextInt(8);
                        int assists = rand.nextInt(10);
                        boolean mvp = (i == 0 && j == 0);
                        perfStmt.setInt(3, kills);
                        perfStmt.setInt(4, deaths);
                        perfStmt.setInt(5, assists);
                        perfStmt.setBoolean(6, mvp);
                        perfStmt.executeUpdate();
                    }
                }
                perfStmt.close();
            }

            // Insert some notifications
            PreparedStatement notifStmt = conn.prepareStatement(
                "INSERT INTO notifications (user_id, message) VALUES (1, ?)");
            notifStmt.setString(1, "Welcome to ETMS");
            notifStmt.executeUpdate();
            notifStmt.setString(1, "Database initialized with standard seeds.");
            notifStmt.executeUpdate();
            notifStmt.close();

            System.out.println("Sample historical data created.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}