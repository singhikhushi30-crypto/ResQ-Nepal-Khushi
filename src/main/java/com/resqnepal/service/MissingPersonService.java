package com.resqnepal.service;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.model.MissingPerson;
import com.resqnepal.model.enums.MissingPersonStatus;
import com.resqnepal.repository.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MissingPersonService {

    public void registerMissingPerson(MissingPerson person) throws InvalidRequestException {
        if (person == null || person.getName() == null || person.getName().trim().isEmpty()) {
            throw new InvalidRequestException("Missing person name cannot be empty");
        }
        if (person.getStatus() == null) {
            throw new InvalidRequestException("Status must be valid");
        }
        String sql = "INSERT INTO missing_persons (id, name, last_known_location, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, person.getId());
            pstmt.setString(2, person.getName());
            pstmt.setString(3, person.getLastKnownLocation());
            pstmt.setString(4, person.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error registering missing person: " + e.getMessage());
        }
    }

    public MissingPerson findById(String id) {
        String sql = "SELECT * FROM missing_persons WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToPerson(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding missing person: " + e.getMessage());
        }
        return null;
    }

    public List<MissingPerson> searchByName(String name) {
        List<MissingPerson> persons = new ArrayList<>();
        String sql = "SELECT * FROM missing_persons WHERE name LIKE ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + name + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    persons.add(mapRowToPerson(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching missing person: " + e.getMessage());
        }
        return persons;
    }

    public List<MissingPerson> getAllMissingPersons() {
        List<MissingPerson> persons = new ArrayList<>();
        String sql = "SELECT * FROM missing_persons";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                persons.add(mapRowToPerson(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all missing persons: " + e.getMessage());
        }
        return persons;
    }

    public void updateStatus(String id, MissingPersonStatus newStatus) throws InvalidRequestException {
        if (newStatus == null) {
            throw new InvalidRequestException("Status must be valid");
        }
        String sql = "UPDATE missing_persons SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus.name());
            pstmt.setString(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating missing person status: " + e.getMessage());
        }
    }

    private MissingPerson mapRowToPerson(ResultSet rs) throws SQLException {
        return new MissingPerson(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("last_known_location"),
                MissingPersonStatus.valueOf(rs.getString("status"))
        );
    }
}
