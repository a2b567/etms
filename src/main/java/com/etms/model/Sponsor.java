package com.etms.model;

public class Sponsor {
    private int sponsorId;
    private String companyName;
    private String contactEmail;
    private double sponsorshipAmount;
    private String category;
    private String status;

    public Sponsor() {}

    public Sponsor(String companyName, String category, double amount) {
        this.companyName = companyName;
        this.category = category;
        this.sponsorshipAmount = amount;
        this.status = "ACTIVE";
    }

    public int getSponsorId() { return sponsorId; }
    public void setSponsorId(int sponsorId) { this.sponsorId = sponsorId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public double getSponsorshipAmount() { return sponsorshipAmount; }
    public void setSponsorshipAmount(double sponsorshipAmount) { this.sponsorshipAmount = sponsorshipAmount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return companyName + " (" + category + ")";
    }
}