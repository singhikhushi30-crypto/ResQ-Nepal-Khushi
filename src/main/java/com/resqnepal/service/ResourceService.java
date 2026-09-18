package com.resqnepal.service;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.exception.ResourceUnavailableException;
import com.resqnepal.model.Resource;
import com.resqnepal.model.enums.ResourceStatus;
import com.resqnepal.repository.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResourceService {

    public void addResource(Resource resource) throws InvalidRequestException {
        if (resource == null || resource.getName() == null || resource.getName().trim().isEmpty()) {
            throw new InvalidRequestException("Resource name cannot be empty");
        }
        String sql = "INSERT INTO resources (resource_id, name, category, quantity, loc_x, loc_y, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resource.getResourceId());
            pstmt.setString(2, resource.getName());
            pstmt.setString(3, resource.getCategory());
            pstmt.setInt(4, resource.getQuantity());
            pstmt.setDouble(5, resource.getLocX());
            pstmt.setDouble(6, resource.getLocY());
            pstmt.setString(7, resource.getStatus().name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding resource: " + e.getMessage());
        }
    }

    public Resource findResource(String resourceId) {
        String sql = "SELECT * FROM resources WHERE resource_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resourceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToResource(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding resource: " + e.getMessage());
        }
        return null;
    }

    public List<Resource> getAllResources() {
        List<Resource> resources = new ArrayList<>();
        String sql = "SELECT * FROM resources";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                resources.add(mapRowToResource(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all resources: " + e.getMessage());
        }
        return resources;
    }

    public void updateResource(Resource resource) {
        String sql = "UPDATE resources SET name = ?, category = ?, quantity = ?, loc_x = ?, loc_y = ?, status = ? WHERE resource_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, resource.getName());
            pstmt.setString(2, resource.getCategory());
            pstmt.setInt(3, resource.getQuantity());
            pstmt.setDouble(4, resource.getLocX());
            pstmt.setDouble(5, resource.getLocY());
            pstmt.setString(6, resource.getStatus().name());
            pstmt.setString(7, resource.getResourceId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating resource: " + e.getMessage());
        }
    }

    public void allocateResource(String resourceId, int requestedQuantity) throws ResourceUnavailableException, InvalidRequestException {
        if (requestedQuantity <= 0) {
            throw new InvalidRequestException("Requested quantity must be greater than 0.");
        }
        
        Resource resource = findResource(resourceId);
        if (resource == null) {
            throw new ResourceUnavailableException("Resource not found");
        }
        if (resource.getStatus() == ResourceStatus.DAMAGED) {
            throw new ResourceUnavailableException("Cannot allocate a DAMAGED resource.");
        }
        if (resource.getStatus() == ResourceStatus.IN_USE) {
            throw new ResourceUnavailableException("Cannot allocate an IN_USE resource.");
        }
        if (requestedQuantity > resource.getQuantity()) {
            throw new ResourceUnavailableException("Requested quantity exceeds available quantity.");
        }
        
        resource.setQuantity(resource.getQuantity() - requestedQuantity);
        if (resource.getQuantity() == 0) {
            resource.setStatus(ResourceStatus.IN_USE);
        }
        updateResource(resource);
    }

    public void releaseResource(String resourceId, int releasedQuantity) throws InvalidRequestException {
        if (releasedQuantity <= 0) {
            throw new InvalidRequestException("Released quantity must be greater than 0.");
        }
        Resource resource = findResource(resourceId);
        if (resource != null) {
            resource.setQuantity(resource.getQuantity() + releasedQuantity);
            if (resource.getStatus() == ResourceStatus.IN_USE) {
                resource.setStatus(ResourceStatus.AVAILABLE);
            }
            updateResource(resource);
        }
    }

    public void changeResourceStatus(String resourceId, ResourceStatus newStatus) {
        Resource resource = findResource(resourceId);
        if (resource != null) {
            resource.setStatus(newStatus);
            updateResource(resource);
        }
    }

    private Resource mapRowToResource(ResultSet rs) throws SQLException {
        return new Resource(
                rs.getString("resource_id"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getDouble("loc_x"),
                rs.getDouble("loc_y"),
                ResourceStatus.valueOf(rs.getString("status"))
        );
    }
}
