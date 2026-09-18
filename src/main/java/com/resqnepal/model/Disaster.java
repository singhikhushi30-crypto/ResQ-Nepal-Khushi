package com.resqnepal.model;

import java.time.LocalDate;

public class Disaster {
    private String disasterId;
    private String name;
    private String location;
    private LocalDate dateOccurred;
    private String description;

    public Disaster() {}

    public Disaster(String disasterId, String name, String location, LocalDate dateOccurred, String description) {
        this.disasterId = disasterId;
        this.name = name;
        this.location = location;
        this.dateOccurred = dateOccurred;
        this.description = description;
    }

    public String getDisasterId() { return disasterId; }
    public void setDisasterId(String disasterId) { this.disasterId = disasterId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getDateOccurred() { return dateOccurred; }
    public void setDateOccurred(LocalDate dateOccurred) { this.dateOccurred = dateOccurred; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Disaster{" +
                "disasterId='" + disasterId + '\'' +
                ", name='" + name + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
