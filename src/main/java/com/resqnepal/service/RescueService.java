package com.resqnepal.service;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.exception.NoTeamAvailableException;
import com.resqnepal.model.RescueOperation;
import com.resqnepal.model.RescueRequest;
import com.resqnepal.model.RescueTeam;
import com.resqnepal.model.Victim;
import com.resqnepal.model.enums.OperationStatus;
import com.resqnepal.model.enums.RequestStatus;
import com.resqnepal.model.enums.TeamStatus;
import com.resqnepal.util.DistanceUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import com.resqnepal.repository.DatabaseManager;

public class RescueService {
    
    private PriorityQueue<RescueRequest> queue;
    private Map<RescueRequest, Long> arrivalSequence;
    private long sequenceCounter;
    
    private List<RescueTeam> allTeams;
    private List<RescueOperation> operations;
    private Map<Integer, Victim> victimsCache;
    private TeamService teamService;

    public RescueService() {
        arrivalSequence = new HashMap<>();
        sequenceCounter = 0;
        
        // Custom comparator: Priority first (higher severity level), then arrival sequence (lower sequence)
        queue = new PriorityQueue<>((r1, r2) -> {
            int severityCompare = Integer.compare(r2.getSeverity().getLevel(), r1.getSeverity().getLevel());
            if (severityCompare != 0) {
                return severityCompare;
            }
            Long seq1 = arrivalSequence.getOrDefault(r1, Long.MAX_VALUE);
            Long seq2 = arrivalSequence.getOrDefault(r2, Long.MAX_VALUE);
            return Long.compare(seq1, seq2);
        });
        
        allTeams = new ArrayList<>();
        operations = new ArrayList<>();
        victimsCache = new HashMap<>();
    }
    
    public void setAllTeams(List<RescueTeam> teams) {
        this.allTeams = teams;
    }

    public void attachTeamService(TeamService teamService) {
        this.teamService = teamService;
        refreshTeams();
    }

    public void refreshTeams() {
        if (teamService != null) {
            allTeams = teamService.getAllTeams();
        }
    }

    public void loadPendingWorkFromDatabase() {
        loadVictimsFromDatabase();
        String sql = "SELECT * FROM rescue_requests WHERE status = 'QUEUED' OR status = 'REPORTED' ORDER BY request_id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                RescueRequest request = new RescueRequest(
                        rs.getString("request_id"),
                        rs.getInt("victim_id"),
                        rs.getString("type"),
                        com.resqnepal.model.enums.Severity.valueOf(rs.getString("severity")),
                        rs.getInt("people_affected"),
                        rs.getString("required_specialization"),
                        RequestStatus.QUEUED,
                        rs.getString("description")
                );
                arrivalSequence.put(request, sequenceCounter++);
                queue.add(request);
            }
        } catch (SQLException e) {
            System.err.println("Error loading pending requests: " + e.getMessage());
        }
    }

    public int getNextVictimId() {
        String sql = "SELECT MAX(id) FROM victims";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int maxId = rs.getInt(1);
                return rs.wasNull() ? 1 : maxId + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error reading next victim id: " + e.getMessage());
        }
        return 1;
    }

    private void loadVictimsFromDatabase() {
        String sql = "SELECT * FROM victims";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Victim victim = new Victim(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("phone"),
                        rs.getDouble("location_x"),
                        rs.getDouble("location_y")
                );
                victimsCache.put(victim.getId(), victim);
            }
        } catch (SQLException e) {
            System.err.println("Error loading victims: " + e.getMessage());
        }
    }
    
    public void addVictim(Victim victim) {
        if (victim != null) {
            victimsCache.put(victim.getId(), victim);
            
            // Save to DB
            String sql = "INSERT INTO victims (id, name, age, phone, location_x, location_y) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseManager.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, victim.getId());
                pstmt.setString(2, victim.getName());
                pstmt.setInt(3, victim.getAge());
                pstmt.setString(4, victim.getPhone());
                pstmt.setDouble(5, victim.getLocationX());
                pstmt.setDouble(6, victim.getLocationY());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error saving victim: " + e.getMessage());
            }
        }
    }

    public void addRequest(RescueRequest request) throws InvalidRequestException {
        // Request validation
        if (request == null) {
            throw new InvalidRequestException("Request cannot be null.");
        }
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new InvalidRequestException("Emergency type cannot be empty.");
        }
        if (request.getSeverity() == null) {
            throw new InvalidRequestException("Severity is required.");
        }
        if (request.getPeopleAffected() <= 0) {
            throw new InvalidRequestException("People affected must be greater than 0.");
        }
        if (request.getRequiredSpecialization() == null || request.getRequiredSpecialization().trim().isEmpty()) {
            throw new InvalidRequestException("Required specialization cannot be empty.");
        }
        
        request.setStatus(RequestStatus.QUEUED);
        arrivalSequence.put(request, sequenceCounter++);
        queue.add(request);
        
        // Save to DB
        String sql = "INSERT INTO rescue_requests (request_id, victim_id, type, severity, people_affected, required_specialization, status, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, request.getRequestId());
            pstmt.setInt(2, request.getVictimId());
            pstmt.setString(3, request.getType());
            pstmt.setString(4, request.getSeverity().name());
            pstmt.setInt(5, request.getPeopleAffected());
            pstmt.setString(6, request.getRequiredSpecialization());
            pstmt.setString(7, request.getStatus().name());
            pstmt.setString(8, request.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error saving request: " + e.getMessage());
        }
    }
    
    public RescueRequest peekNextRequest() {
        return queue.peek();
    }
    
    public int getQueueSize() {
        return queue.size();
    }
    
    public List<RescueRequest> getQueuedRequests() {
        // Return a safe copy representing the queue order
        List<RescueRequest> copy = new ArrayList<>(queue);
        copy.sort(queue.comparator());
        return copy;
    }
    
    public List<RescueRequest> getAllRequests() {
        List<RescueRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM rescue_requests";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                RescueRequest req = new RescueRequest(
                        rs.getString("request_id"),
                        rs.getInt("victim_id"),
                        rs.getString("type"),
                        com.resqnepal.model.enums.Severity.valueOf(rs.getString("severity")),
                        rs.getInt("people_affected"),
                        rs.getString("required_specialization"),
                        RequestStatus.valueOf(rs.getString("status")),
                        rs.getString("description")
                );
                list.add(req);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all requests: " + e.getMessage());
        }
        return list;
    }
    
    public List<RescueOperation> getAllOperations() {
        List<RescueOperation> list = new ArrayList<>();
        String sql = "SELECT * FROM rescue_operations";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             java.sql.ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String endStr = rs.getString("end_time");
                LocalDateTime endTime = endStr != null ? LocalDateTime.parse(endStr) : null;
                RescueOperation op = new RescueOperation(
                        rs.getString("op_id"),
                        rs.getString("request_id"),
                        rs.getString("team_id"),
                        LocalDateTime.parse(rs.getString("start_time")),
                        endTime,
                        OperationStatus.valueOf(rs.getString("status"))
                );
                list.add(op);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all operations: " + e.getMessage());
        }
        return list;
    }
    
    public boolean hasPendingRequests() {
        return !queue.isEmpty();
    }
    
    public RescueOperation processNextRequest() throws NoTeamAvailableException {
        if (!hasPendingRequests()) {
            return null;
        }

        refreshTeams();
        
        RescueRequest nextRequest = queue.peek();
        
        // Lookup victim location for distance calculation
        Victim victim = victimsCache.get(nextRequest.getVictimId());
        double locX = victim != null ? victim.getLocationX() : 0.0;
        double locY = victim != null ? victim.getLocationY() : 0.0;
        
        RescueTeam suitableTeam = findSuitableTeam(nextRequest.getRequiredSpecialization(), locX, locY);
        
        // If we reach here, a team was found. Now assign it.
        // Remove from queue
        queue.poll();
        arrivalSequence.remove(nextRequest);
        
        return assignTeam(nextRequest, suitableTeam);
    }
    
    private RescueTeam findSuitableTeam(String requiredSpecialization, double locX, double locY) throws NoTeamAvailableException {
        RescueTeam bestTeam = null;
        double shortestDistance = Double.MAX_VALUE;
        
        for (RescueTeam team : allTeams) {
            if (team != null && team.getStatus() == TeamStatus.AVAILABLE && 
                team.getSpecialization().equalsIgnoreCase(requiredSpecialization)) {
                
                double distance = DistanceUtil.calculateDistance(locX, locY, team.getLocX(), team.getLocY());
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    bestTeam = team;
                }
            }
        }
        
        if (bestTeam == null) {
            throw new NoTeamAvailableException("No suitable available team found for specialization: " + requiredSpecialization);
        }
        
        return bestTeam;
    }
    
    private RescueOperation assignTeam(RescueRequest request, RescueTeam team) {
        request.setStatus(RequestStatus.ASSIGNED);
        team.setStatus(TeamStatus.ON_MISSION);
        
        RescueOperation operation = new RescueOperation(
                "OP-" + System.currentTimeMillis(),
                request.getRequestId(),
                team.getTeamId(),
                LocalDateTime.now(),
                null,
                OperationStatus.ASSIGNED
        );
        
        operations.add(operation);
        
        // Update DB
        try (Connection conn = DatabaseManager.getConnection()) {
            // Update request status
            try (PreparedStatement pstmt = conn.prepareStatement("UPDATE rescue_requests SET status = ? WHERE request_id = ?")) {
                pstmt.setString(1, RequestStatus.ASSIGNED.name());
                pstmt.setString(2, request.getRequestId());
                pstmt.executeUpdate();
            }
            // Update team status
            try (PreparedStatement pstmt = conn.prepareStatement("UPDATE rescue_teams SET status = ? WHERE team_id = ?")) {
                pstmt.setString(1, TeamStatus.ON_MISSION.name());
                pstmt.setString(2, team.getTeamId());
                pstmt.executeUpdate();
            }
            // Insert operation
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO rescue_operations (op_id, request_id, team_id, start_time, status) VALUES (?, ?, ?, ?, ?)")) {
                pstmt.setString(1, operation.getOpId());
                pstmt.setString(2, operation.getRequestId());
                pstmt.setString(3, operation.getTeamId());
                pstmt.setString(4, operation.getStartTime().toString());
                pstmt.setString(5, OperationStatus.ASSIGNED.name());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error assigning team in DB: " + e.getMessage());
        }
        
        return operation;
    }
}
