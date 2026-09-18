package com.resqnepal.service;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.model.RescueTeam;
import com.resqnepal.model.enums.TeamStatus;
import com.resqnepal.repository.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeamService {

    public void addTeam(RescueTeam team) throws InvalidRequestException {
        validateTeam(team);
        String sql = "INSERT INTO rescue_teams (team_id, name, specialization, member_count, loc_x, loc_y, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, team.getTeamId());
            pstmt.setString(2, team.getName());
            pstmt.setString(3, team.getSpecialization());
            pstmt.setInt(4, team.getMemberCount());
            pstmt.setDouble(5, team.getLocX());
            pstmt.setDouble(6, team.getLocY());
            pstmt.setString(7, team.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding team: " + e.getMessage());
        }
    }

    public RescueTeam findTeamById(String teamId) {
        String sql = "SELECT * FROM rescue_teams WHERE team_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teamId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToTeam(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding team: " + e.getMessage());
        }
        return null;
    }

    public List<RescueTeam> getAllTeams() {
        List<RescueTeam> teams = new ArrayList<>();
        String sql = "SELECT * FROM rescue_teams";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                teams.add(mapRowToTeam(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all teams: " + e.getMessage());
        }
        return teams;
    }

    public List<RescueTeam> findAvailableTeams() {
        List<RescueTeam> teams = new ArrayList<>();
        String sql = "SELECT * FROM rescue_teams WHERE status = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, TeamStatus.AVAILABLE.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    teams.add(mapRowToTeam(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding available teams: " + e.getMessage());
        }
        return teams;
    }

    public void updateTeam(RescueTeam team) throws InvalidRequestException {
        validateTeam(team);
        String sql = "UPDATE rescue_teams SET name = ?, specialization = ?, member_count = ?, loc_x = ?, loc_y = ?, status = ? WHERE team_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, team.getName());
            pstmt.setString(2, team.getSpecialization());
            pstmt.setInt(3, team.getMemberCount());
            pstmt.setDouble(4, team.getLocX());
            pstmt.setDouble(5, team.getLocY());
            pstmt.setString(6, team.getStatus().name());
            pstmt.setString(7, team.getTeamId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating team: " + e.getMessage());
        }
    }

    public void changeTeamStatus(String teamId, TeamStatus newStatus) {
        String sql = "UPDATE rescue_teams SET status = ? WHERE team_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.name());
            pstmt.setString(2, teamId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error changing team status: " + e.getMessage());
        }
    }

    private void validateTeam(RescueTeam team) throws InvalidRequestException {
        if (team == null) throw new InvalidRequestException("Team cannot be null");
        if (team.getName() == null || team.getName().trim().isEmpty()) throw new InvalidRequestException("Team name cannot be empty");
        if (team.getSpecialization() == null || team.getSpecialization().trim().isEmpty()) throw new InvalidRequestException("Specialization cannot be empty");
        if (team.getMemberCount() <= 0) throw new InvalidRequestException("Member count must be greater than 0");
        if (Double.isNaN(team.getLocX()) || Double.isNaN(team.getLocY())) throw new InvalidRequestException("Coordinates must be valid numbers");
    }

    private RescueTeam mapRowToTeam(ResultSet rs) throws SQLException {
        return new RescueTeam(
                rs.getString("team_id"),
                rs.getString("name"),
                rs.getString("specialization"),
                rs.getInt("member_count"),
                rs.getDouble("loc_x"),
                rs.getDouble("loc_y"),
                TeamStatus.valueOf(rs.getString("status"))
        );
    }
}
