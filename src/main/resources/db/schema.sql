-- ETMS PostgreSQL Schema
-- Use with Supabase: db.ggouoywuruqzfnelozqr.supabase.co / postgres

-- USERS
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
);

-- PERSONS (base table for players, coaches, referees, staff)
CREATE TABLE IF NOT EXISTS persons (
    person_id SERIAL PRIMARY KEY,
    user_id INT UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    phone VARCHAR(20),
    country VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- COACHES
CREATE TABLE IF NOT EXISTS coaches (
    coach_id SERIAL PRIMARY KEY,
    person_id INT UNIQUE REFERENCES persons(person_id) ON DELETE CASCADE,
    experience_years INT DEFAULT 0,
    specialization VARCHAR(100),
    certification VARCHAR(100),
    team_id INT
);

-- REFEREES
CREATE TABLE IF NOT EXISTS referees (
    referee_id SERIAL PRIMARY KEY,
    person_id INT UNIQUE REFERENCES persons(person_id) ON DELETE CASCADE,
    certification_level VARCHAR(50),
    matches_officiated INT DEFAULT 0,
    rating DOUBLE PRECISION DEFAULT 5.0
);

-- TEAMS
CREATE TABLE IF NOT EXISTS teams (
    team_id SERIAL PRIMARY KEY,
    team_name VARCHAR(100) UNIQUE NOT NULL,
    tag VARCHAR(10) UNIQUE NOT NULL,
    coach_id INT REFERENCES persons(person_id) ON DELETE SET NULL,
    date_created DATE DEFAULT CURRENT_DATE,
    total_wins INT DEFAULT 0,
    total_losses INT DEFAULT 0,
    tournament_points INT DEFAULT 0,
    ranking INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    elo_rating DOUBLE PRECISION NOT NULL DEFAULT 1200.0,
    elo_rating_updated TIMESTAMP NULL
);

-- VENUES
CREATE TABLE IF NOT EXISTS venues (
    venue_id SERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    location VARCHAR(255),
    capacity INT,
    internet_speed VARCHAR(50),
    description TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- PLAYERS
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
    status VARCHAR(20) DEFAULT 'ACTIVE',
    is_captain BOOLEAN DEFAULT FALSE,
    is_starter BOOLEAN DEFAULT FALSE,
    jersey_number INT DEFAULT 0
);

-- TOURNAMENTS
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
);

-- TOURNAMENT TEAMS (registration)
CREATE TABLE IF NOT EXISTS tournament_teams (
    tournament_id INT REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    team_id INT REFERENCES teams(team_id) ON DELETE CASCADE,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    seed_number INT,
    status VARCHAR(20) DEFAULT 'PENDING',
    PRIMARY KEY (tournament_id, team_id)
);

-- MATCHES
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
);

-- SPONSORS
CREATE TABLE IF NOT EXISTS sponsors (
    sponsor_id SERIAL PRIMARY KEY,
    company_name VARCHAR(200) NOT NULL,
    contact_email VARCHAR(100),
    sponsorship_amount DECIMAL(12,2) DEFAULT 0.00,
    category VARCHAR(20) DEFAULT 'BRONZE',
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- SPONSORSHIPS
CREATE TABLE IF NOT EXISTS sponsorships (
    sponsorship_id SERIAL PRIMARY KEY,
    sponsor_id INT NOT NULL REFERENCES sponsors(sponsor_id) ON DELETE CASCADE,
    tournament_id INT NOT NULL REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    amount DECIMAL(12,2) NOT NULL,
    start_date DATE,
    end_date DATE,
    contract_signed BOOLEAN DEFAULT FALSE
);

-- PRIZE DISTRIBUTION
CREATE TABLE IF NOT EXISTS prize_distribution (
    distribution_id SERIAL PRIMARY KEY,
    tournament_id INT NOT NULL REFERENCES tournaments(tournament_id) ON DELETE CASCADE,
    position INT NOT NULL,
    team_id INT REFERENCES teams(team_id) ON DELETE SET NULL,
    percentage DECIMAL(5,2) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    awarded BOOLEAN DEFAULT FALSE
);

-- NOTIFICATIONS
CREATE TABLE IF NOT EXISTS notifications (
    notification_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    message VARCHAR(500) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PLAYER MATCH PERFORMANCE (KDA/MVP)
CREATE TABLE IF NOT EXISTS player_match_performance (
    id SERIAL PRIMARY KEY,
    player_id INT NOT NULL REFERENCES players(player_id) ON DELETE CASCADE,
    match_id INT NOT NULL REFERENCES matches(match_id) ON DELETE CASCADE,
    kills INT DEFAULT 0,
    deaths INT DEFAULT 0,
    assists INT DEFAULT 0,
    is_mvp BOOLEAN DEFAULT FALSE
);

-- FINANCIAL TRANSACTIONS
CREATE TABLE IF NOT EXISTS financial_transactions (
    transaction_id SERIAL PRIMARY KEY,
    tournament_id INT REFERENCES tournaments(tournament_id) ON DELETE SET NULL,
    type VARCHAR(30) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    description VARCHAR(255),
    transaction_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- EQUIPMENT INVENTORY
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
);

-- AUDIT LOGS
CREATE TABLE IF NOT EXISTS audit_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES users(user_id) ON DELETE SET NULL,
    username VARCHAR(50),
    action VARCHAR(100) NOT NULL,
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CHECK-INS (QR)
CREATE TABLE IF NOT EXISTS check_ins (
    id SERIAL PRIMARY KEY,
    entity_type VARCHAR(20) NOT NULL,
    entity_id INT NOT NULL,
    checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- GAMES
CREATE TABLE IF NOT EXISTS games (
    game_id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    default_player_count INT DEFAULT 5,
    description TEXT
);

-- STAFF
CREATE TABLE IF NOT EXISTS staff (
    staff_id SERIAL PRIMARY KEY,
    person_id INT UNIQUE REFERENCES persons(person_id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    team_id INT REFERENCES teams(team_id) ON DELETE SET NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- Optional indexes for performance
CREATE INDEX IF NOT EXISTS idx_players_team_status ON players(team_id, status);
CREATE INDEX IF NOT EXISTS idx_matches_tournament_round ON matches(tournament_id, round_number, match_number);
CREATE INDEX IF NOT EXISTS idx_tournament_teams_tournament_status ON tournament_teams(tournament_id, status);