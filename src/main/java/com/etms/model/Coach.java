package com.etms.model;

public class Coach extends Person {
    private int coachId;
    private int experienceYears;
    private String specialization;
    private String certification;
    private int teamId;
    private String status; // NEW field for ACTIVE/INACTIVE/SUSPENDED

    public Coach() {
        super();
        this.status = "ACTIVE";
    }

    public Coach(String firstName, String lastName, String specialization) {
        super(firstName, lastName);
        this.specialization = specialization;
        this.status = "ACTIVE";
    }

    @Override
    public String getRole() {
        return "COACH";
    }

    // Getters and Setters
    public int getCoachId() { return coachId; }
    public void setCoachId(int coachId) { this.coachId = coachId; }

    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) {
        if (experienceYears < 0) {
            throw new IllegalArgumentException("Experience cannot be negative");
        }
        this.experienceYears = experienceYears;
    }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getCertification() { return certification; }
    public void setCertification(String certification) { this.certification = certification; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) {
        if (teamId < 0) {
            throw new IllegalArgumentException("Team ID cannot be negative");
        }
        this.teamId = teamId;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        // Validate status
        if (!"ACTIVE".equals(status) && !"INACTIVE".equals(status) && !"SUSPENDED".equals(status)) {
            throw new IllegalArgumentException("Invalid status: " + status + ". Must be ACTIVE, INACTIVE, or SUSPENDED.");
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName() + " (" + specialization + ")";
    }
}