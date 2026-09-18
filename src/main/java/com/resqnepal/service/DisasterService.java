package com.resqnepal.service;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.model.Disaster;
import com.resqnepal.repository.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DisasterService {

    public void createDisaster(Disaster disaster) throws InvalidRequestException {
        if (disaster == null || disaster.getName() == null || disaster.getName().trim().isEmpty()) {
            throw new InvalidRequestException("Disaster name cannot be empty");
        }
        String sql = "INSERT INTO disasters (disaster_id, name, location, date_occurred, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, disaster.getDisasterId());
            pstmt.setString(2, disaster.getName());
            pstmt.setString(3, disaster.getLocation());
            pstmt.setString(4, disaster.getDateOccurred() != null ? disaster.getDateOccurred().toString() : null);
            pstmt.setString(5, disaster.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error creating disaster: " + e.getMessage());
        }
    }

    public Disaster findDisaster(String disasterId) {
        String sql = "SELECT * FROM disasters WHERE disaster_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, disasterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToDisaster(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding disaster: " + e.getMessage());
        }
        return null;
    }

    public List<Disaster> getDisasters() {
        List<Disaster> disasters = new ArrayList<>();
        String sql = "SELECT * FROM disasters";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                disasters.add(mapRowToDisaster(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all disasters: " + e.getMessage());
        }
        return disasters;
    }

    public void updateDisaster(Disaster disaster) {
        String sql = "UPDATE disasters SET name = ?, location = ?, date_occurred = ?, description = ? WHERE disaster_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, disaster.getName());
            pstmt.setString(2, disaster.getLocation());
            pstmt.setString(3, disaster.getDateOccurred() != null ? disaster.getDateOccurred().toString() : null);
            pstmt.setString(4, disaster.getDescription());
            pstmt.setString(5, disaster.getDisasterId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating disaster: " + e.getMessage());
        }
    }

    private Disaster mapRowToDisaster(ResultSet rs) throws SQLException {
        String dateStr = rs.getString("date_occurred");
        LocalDate date = (dateStr != null && !dateStr.isEmpty()) ? LocalDate.parse(dateStr) : null;
        
        return new Disaster(
                rs.getString("disaster_id"),
                rs.getString("name"),
                rs.getString("location"),
                date,
                rs.getString("description")
        );
    }
}
