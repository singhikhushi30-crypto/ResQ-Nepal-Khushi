package com.resqnepal.model;

import com.resqnepal.model.enums.MissingPersonStatus;

public class MissingPerson {
    private String id;
    private String name;
    private String lastKnownLocation;
    private MissingPersonStatus status;

    public MissingPerson() {}

    public MissingPerson(String id, String name, String lastKnownLocation, MissingPersonStatus status) {
        this.id = id;
        this.name = name;
        this.lastKnownLocation = lastKnownLocation;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastKnownLocation() { return lastKnownLocation; }
    public void setLastKnownLocation(String lastKnownLocation) { this.lastKnownLocation = lastKnownLocation; }

    public MissingPersonStatus getStatus() { return status; }
    public void setStatus(MissingPersonStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "MissingPerson{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
