package com.etms.model;

/**
 * Represents equipment allocated to venues and tournaments.
 * Demonstrates Encapsulation.
 */
public class Equipment {
    private int equipmentId;
    private String type;
    private String brand;
    private String model;
    private String serialNumber;
    private String status;
    private int venueId;
    private String venueName;       // for display
    private int tournamentId;
    private String tournamentName;  // for display
    private String notes;

    public Equipment() {
        this.status = "Available";
    }

    public Equipment(String type, String brand, String model, String serialNumber) {
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.serialNumber = serialNumber;
        this.status = "Available";
    }

    // Getters and Setters
    public int getEquipmentId() { return equipmentId; }
    public void setEquipmentId(int equipmentId) { this.equipmentId = equipmentId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) { this.venueId = venueId; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public int getTournamentId() { return tournamentId; }
    public void setTournamentId(int tournamentId) { this.tournamentId = tournamentId; }

    public String getTournamentName() { return tournamentName; }
    public void setTournamentName(String tournamentName) { this.tournamentName = tournamentName; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
