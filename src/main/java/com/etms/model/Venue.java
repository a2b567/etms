package com.etms.model;

/**
 * Represents a physical or online venue where tournaments and matches take place.
 * Demonstrates Encapsulation with private fields and accessor methods.
 */
public class Venue {
    private int venueId;
    private String name;
    private String location;
    private int capacity;
    private String internetSpeed;
    private String description;
    private String status;

    public Venue() {
        this.status = "ACTIVE";
    }

    public Venue(String name, String location, int capacity) {
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.status = "ACTIVE";
    }

    public int getVenueId() { return venueId; }
    public void setVenueId(int venueId) { this.venueId = venueId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getInternetSpeed() { return internetSpeed; }
    public void setInternetSpeed(String internetSpeed) { this.internetSpeed = internetSpeed; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return name; }
}
