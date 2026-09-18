package com.resqnepal.service;

import com.resqnepal.repository.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DashboardService {
    
    public int getTotalRescueRequests() {
        return getCount("SELECT COUNT(*) FROM rescue_requests");
    }
    
    public int getCriticalRequests() {
        return getCount("SELECT COUNT(*) FROM rescue_requests WHERE severity = 'CRITICAL'");
    }
    
    public int getQueuedRequests() {
        return getCount("SELECT COUNT(*) FROM rescue_requests WHERE status = 'QUEUED'");
    }
    
    public int getAvailableTeams() {
        return getCount("SELECT COUNT(*) FROM rescue_teams WHERE status = 'AVAILABLE'");
    }
    
    public int getTeamsOnMission() {
        return getCount("SELECT COUNT(*) FROM rescue_teams WHERE status = 'ON_MISSION'");
    }
    
    public int getActiveOperations() {
        return getCount("SELECT COUNT(*) FROM rescue_operations WHERE status = 'ASSIGNED' OR status = 'STARTED' OR status = 'IN_PROGRESS'");
    }
    
    public int getPeopleRescued() {
        return getSum("SELECT SUM(r.people_affected) FROM rescue_requests r JOIN rescue_operations o ON r.request_id = o.request_id WHERE o.status = 'COMPLETED'");
    }
    
    public int getAvailableShelterCapacity() {
        return getSum("SELECT SUM(max_capacity - current_occupancy) FROM shelters");
    }

    private int getCount(String query) {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    private int getSum(String query) {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
