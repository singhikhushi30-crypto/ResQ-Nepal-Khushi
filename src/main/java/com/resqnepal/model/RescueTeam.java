package com.resqnepal.model;

import com.resqnepal.model.enums.TeamStatus;

public class RescueTeam {
    private String teamId;
    private String name;
    private String specialization;
    private int memberCount;
    private double locX;
    private double locY;
    private TeamStatus status;

    public RescueTeam() {}

    public RescueTeam(String teamId, String name, String specialization, int memberCount, double locX, double locY, TeamStatus status) {
        this.teamId = teamId;
        this.name = name;
        this.specialization = specialization;
        this.memberCount = memberCount;
        this.locX = locX;
        this.locY = locY;
        this.status = status;
    }

    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public int getMemberCount() { return memberCount; }
    public void setMemberCount(int memberCount) { this.memberCount = memberCount; }

    public double getLocX() { return locX; }
    public void setLocX(double locX) { this.locX = locX; }

    public double getLocY() { return locY; }
    public void setLocY(double locY) { this.locY = locY; }

    public TeamStatus getStatus() { return status; }
    public void setStatus(TeamStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "RescueTeam{" +
                "teamId='" + teamId + '\'' +
                ", name='" + name + '\'' +
                ", specialization='" + specialization + '\'' +
                ", status=" + status +
                '}';
    }
}
