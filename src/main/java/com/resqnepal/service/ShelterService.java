package com.resqnepal.service;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.exception.ShelterFullException;
import com.resqnepal.model.Shelter;
import com.resqnepal.repository.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShelterService {

    public void addShelter(Shelter shelter) throws InvalidRequestException {
        if (shelter == null || shelter.getName() == null || shelter.getName().trim().isEmpty()) {
            throw new InvalidRequestException("Shelter name cannot be empty");
        }
        String sql = "INSERT INTO shelters (shelter_id, name, loc_x, loc_y, max_capacity, current_occupancy, medical_support, food_available, water_available) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shelter.getShelterId());
            pstmt.setString(2, shelter.getName());
            pstmt.setDouble(3, shelter.getLocX());
            pstmt.setDouble(4, shelter.getLocY());
            pstmt.setInt(5, shelter.getMaxCapacity());
            pstmt.setInt(6, shelter.getCurrentOccupancy());
            pstmt.setInt(7, shelter.isMedicalSupport() ? 1 : 0);
            pstmt.setInt(8, shelter.isFoodAvailable() ? 1 : 0);
            pstmt.setInt(9, shelter.isWaterAvailable() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding shelter: " + e.getMessage());
        }
    }

    public Shelter findShelter(String shelterId) {
        String sql = "SELECT * FROM shelters WHERE shelter_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shelterId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToShelter(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding shelter: " + e.getMessage());
        }
        return null;
    }

    public List<Shelter> getAllShelters() {
        List<Shelter> shelters = new ArrayList<>();
        String sql = "SELECT * FROM shelters";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                shelters.add(mapRowToShelter(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all shelters: " + e.getMessage());
        }
        return shelters;
    }

    public void updateShelter(Shelter shelter) {
        String sql = "UPDATE shelters SET name = ?, loc_x = ?, loc_y = ?, max_capacity = ?, current_occupancy = ?, medical_support = ?, food_available = ?, water_available = ? WHERE shelter_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, shelter.getName());
            pstmt.setDouble(2, shelter.getLocX());
            pstmt.setDouble(3, shelter.getLocY());
            pstmt.setInt(4, shelter.getMaxCapacity());
            pstmt.setInt(5, shelter.getCurrentOccupancy());
            pstmt.setInt(6, shelter.isMedicalSupport() ? 1 : 0);
            pstmt.setInt(7, shelter.isFoodAvailable() ? 1 : 0);
            pstmt.setInt(8, shelter.isWaterAvailable() ? 1 : 0);
            pstmt.setString(9, shelter.getShelterId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating shelter: " + e.getMessage());
        }
    }

    public int calculateAvailableCapacity(String shelterId) {
        Shelter shelter = findShelter(shelterId);
        if (shelter != null) {
            return shelter.getMaxCapacity() - shelter.getCurrentOccupancy();
        }
        return 0;
    }

    public void admitRescuedPeople(String shelterId, int numberOfPeople) throws ShelterFullException, InvalidRequestException {
        if (numberOfPeople <= 0) {
            throw new InvalidRequestException("Number of people must be greater than 0.");
        }
        Shelter shelter = findShelter(shelterId);
        if (shelter == null) {
            throw new InvalidRequestException("Shelter not found");
        }
        
        int availableCapacity = shelter.getMaxCapacity() - shelter.getCurrentOccupancy();
        if (numberOfPeople > availableCapacity) {
            throw new ShelterFullException("Insufficient capacity. Available: " + availableCapacity + ", Requested: " + numberOfPeople);
        }
        
        shelter.setCurrentOccupancy(shelter.getCurrentOccupancy() + numberOfPeople);
        updateShelter(shelter);
    }
    
    public void releaseShelterCapacity(String shelterId, int numberOfPeople) throws InvalidRequestException {
        if (numberOfPeople <= 0) {
            throw new InvalidRequestException("Number of people must be greater than 0.");
        }
        Shelter shelter = findShelter(shelterId);
        if (shelter != null) {
            int newOccupancy = Math.max(0, shelter.getCurrentOccupancy() - numberOfPeople);
            shelter.setCurrentOccupancy(newOccupancy);
            updateShelter(shelter);
        }
    }

    private Shelter mapRowToShelter(ResultSet rs) throws SQLException {
        return new Shelter(
                rs.getString("shelter_id"),
                rs.getString("name"),
                rs.getDouble("loc_x"),
                rs.getDouble("loc_y"),
                rs.getInt("max_capacity"),
                rs.getInt("current_occupancy"),
                rs.getInt("medical_support") == 1,
                rs.getInt("food_available") == 1,
                rs.getInt("water_available") == 1
        );
    }
}
