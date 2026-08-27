package com.etms.model;

public class Staff extends Person {
    private int staffId;
    private String role;          // Manager, Analyst, Assistant Coach, Coordinator
    private int teamId;
    private String status;

    public Staff() {
        super();
        this.status = "ACTIVE";
    }

    public Staff(String firstName, String lastName, String role) {
        super(firstName, lastName);
        this.role = role;
        this.status = "ACTIVE";
    }

    @Override
    public String getRole() {
        return "STAFF";
    }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public String getStaffRole() { return role; }
    public void setStaffRole(String role) { this.role = role; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}