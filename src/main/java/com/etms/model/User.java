package com.etms.model;

public class User extends Person {
    private int userId;
    private String username;
    private String passwordHash;
    private String role;
    private boolean active;
    private String lastLogin;

    public User() {
        super();
    }

    public User(String firstName, String lastName, String username, String email, String role) {
        super(firstName, lastName);
        setEmail(email);
        this.username = username;
        this.role = role;
        this.active = true;
    }

    @Override
    public String getRole() {
        return role;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    // FIX: made public to allow access from other packages
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public void setRole(String role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

    public boolean isAdmin() { return "ADMIN".equals(role); }
    public boolean isOrganizer() { return "ORGANIZER".equals(role) || isAdmin(); }
    public boolean isReferee() { return "REFEREE".equals(role); }
    public boolean isCoach() { return "COACH".equals(role); }
    public boolean isPlayer() { return "PLAYER".equals(role); }
}