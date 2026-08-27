package com.etms.model;

public class Referee extends Person {
    private int refereeId;
    private String qualification;
    private int yearsExperience;
    private String status; // ACTIVE, INACTIVE

    public Referee() {
        super();
    }

    public Referee(String firstName, String lastName, String email, String phone,
                   String qualification, int yearsExperience, String status) {
        super(firstName, lastName);
        setEmail(email);
        setPhone(phone);
        this.qualification = qualification;
        this.yearsExperience = yearsExperience;
        this.status = status;
    }

    @Override
    public String getRole() {
        return "REFEREE";
    }

    // Getters and Setters
    public int getRefereeId() { return refereeId; }
    public void setRefereeId(int refereeId) { this.refereeId = refereeId; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public int getYearsExperience() { return yearsExperience; }
    public void setYearsExperience(int yearsExperience) {
        if (yearsExperience < 0) {
            throw new IllegalArgumentException("Experience cannot be negative");
        }
        this.yearsExperience = yearsExperience;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (!"ACTIVE".equals(status) && !"INACTIVE".equals(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        this.status = status;
    }

    @Override
    public String toString() {
        return getFullName() + " (" + qualification + ")";
    }
}