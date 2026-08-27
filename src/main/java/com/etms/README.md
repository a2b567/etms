# ETMS - Esports Tournament Management System

## Overview

ETMS is a comprehensive desktop application built with Java Swing for managing esports tournaments. It provides tournament organizers with a complete suite of tools to manage tournaments, teams, players, matches, venues, and more.

## Features

### 🏆 Tournament Management
- Create, edit, and delete tournaments
- Configure tournament details (format, dates, prize pool, venue)
- Tournament status tracking (Upcoming, Registration, Ongoing, Completed, Cancelled)
- Multi-step tournament creation wizard

### 👥 Team Management
- Register and manage teams with tags, coaches, and players
- Track team statistics (wins, losses, Elo rating)
- Team roster management with starter/bench assignments

### 👤 Player Management
- Player profiles with in-game names, ranks, and roles
- Player statistics tracking (kills, deaths, assists, MVPs)
- Team assignment and roster management

### 🎮 Match Operations
- Create and schedule matches with round/match numbering
- Record match results with scores and winner selection
- Live match monitoring and bracket progression
- AI-powered match predictions using Elo ratings

### 🏟️ Bracket System
- Automatic single-elimination bracket generation
- Round progression and winner advancement
- View brackets with visual representation

### 👨‍🏫 Staff & Personnel
- Coach management with specialization and certifications
- Referee management with qualifications and experience
- Staff management (managers, coordinators, analysts)
- Venue management for tournament locations

### 📊 Financial Management
- Prize pool distribution with configurable percentages
- Sponsor management with tiered categories
- Automatic prize distribution calculation

### 📈 Analytics
- Dashboard with key metrics (active tournaments, total teams, upcoming matches)
- Player performance analytics
- Match prediction using machine learning (Elo-based)

### 🔐 Security
- Role-based access control (Admin, Organizer, Referee, Coach)
- Secure authentication with password hashing
- Audit logging for all system actions

### 🎨 UI/UX
- Modern, dark-themed user interface with consistent design
- Responsive layout with collapsible sidebar
- Status badges for entity states (Active, Pending, Completed, etc.)
- Interactive data tables with search and filter capabilities

## Technology Stack

### Frontend
- **Java Swing** - UI framework
- **MigLayout** - Advanced layout manager
- **Custom Components** - ETMSButton, ETMSTable, ETMSCard, etc.

### Backend
- **Java 8+** - Programming language
- **JDBC** - Database connectivity
- **PostgreSQL** - Relational database (via Supabase)

### Database
- **Supabase** - Hosted PostgreSQL database
- **PostgreSQL 14+** - Database engine
- **Row Level Security (RLS)** - Database-level security

### Build & Deployment
- **Maven** - Dependency management and build automation
- **JAR** - Single executable file

## Architecture

The application follows a layered architecture:

### 1. Presentation Layer (View)
- `com.etms.view` - All panel classes for the UI
- `com.etms.ui.components` - Reusable Swing components
- `com.etms.theme` - Theme management for consistent styling

### 2. Business Logic Layer (Controller)
- `com.etms.controller` - Controllers that mediate between View and Model
- `com.etms.service` - Business services (EloService, RefereeService, etc.)

### 3. Data Access Layer (DAO)
- `com.etms.dao` - Data Access Objects for database operations
- `com.etms.config` - Database configuration and initialization

### 4. Model Layer
- `com.etms.model` - Entity classes representing database tables
- `com.etms.util` - Utility classes (validation, password hashing, etc.)

### 5. Security Layer
- `com.etms.service` - UserSession for managing current user
- RBAC enforcement at controller level

## Database Schema

Key tables:
- `users` - User accounts with roles (ADMIN, ORGANIZER, REFEREE, COACH)
- `persons` - Personal information for all person types
- `teams` - Registered teams with statistics
- `players` - Players with in-game profiles
- `tournaments` - Tournament configurations
- `matches` - Match schedules and results
- `venues` - Venue information
- `referees` - Referee profiles
- `coaches` - Coach profiles
- `staff` - Staff profiles
- `sponsors` - Sponsor information
- `prize_distribution` - Prize pool distributions
- `notifications` - System notifications
- `audit_logs` - Audit trail

## Setup Instructions

### Prerequisites
- Java 8 or higher
- Maven 3.6+
- Internet connection (for Supabase database)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/etms.git
cd etms