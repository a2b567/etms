package com.etms.model;

import java.time.LocalDateTime;

public class AuditLog {
    private int logId;
    private Integer userId;
    private String username;
    private String action;
    private String details;
    private LocalDateTime createdAt;

    public AuditLog() {}

    public AuditLog(int logId, Integer userId, String username, String action, String details, LocalDateTime createdAt) {
        this.logId = logId;
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.details = details;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}