package com.resqnepal.service;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.exception.NoTeamAvailableException;
import com.resqnepal.model.RescueOperation;
import com.resqnepal.model.RescueRequest;
import com.resqnepal.model.RescueTeam;
import com.resqnepal.model.Victim;
import com.resqnepal.model.enums.RequestStatus;
import com.resqnepal.model.enums.Severity;
import com.resqnepal.model.enums.TeamStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RescueServiceTest {

    private RescueService rescueService;

    @BeforeEach
    public void setUp() {
        com.resqnepal.repository.DatabaseManager.setTestMode(true);
        com.resqnepal.repository.DatabaseManager.initializeDatabase();
        rescueService = new RescueService();
    }

    @Test
    public void testPriorityQueueOrdering() throws Exception {
        // TEST 1
        RescueRequest r1 = new RescueRequest("RQ-001", 1, "Type", Severity.LOW, 1, "Flood Rescue", RequestStatus.REPORTED, "");
        RescueRequest r2 = new RescueRequest("RQ-002", 2, "Type", Severity.CRITICAL, 1, "Flood Rescue", RequestStatus.REPORTED, "");
        RescueRequest r3 = new RescueRequest("RQ-003", 3, "Type", Severity.MEDIUM, 1, "Flood Rescue", RequestStatus.REPORTED, "");
        RescueRequest r4 = new RescueRequest("RQ-004", 4, "Type", Severity.HIGH, 1, "Flood Rescue", RequestStatus.REPORTED, "");

        rescueService.addRequest(r1);
        rescueService.addRequest(r2);
        rescueService.addRequest(r3);
        rescueService.addRequest(r4);

        List<RescueRequest> ordered = rescueService.getQueuedRequests();
        assertEquals(Severity.CRITICAL, ordered.get(0).getSeverity());
        assertEquals(Severity.HIGH, ordered.get(1).getSeverity());
        assertEquals(Severity.MEDIUM, ordered.get(2).getSeverity());
        assertEquals(Severity.LOW, ordered.get(3).getSeverity());
    }

    @Test
    public void testNearestTeamSelected() throws Exception {
        // TEST 2
        RescueTeam tA = new RescueTeam("T-A", "Team A", "Flood Rescue", 5, 10, 10, TeamStatus.AVAILABLE);
        RescueTeam tB = new RescueTeam("T-B", "Team B", "Flood Rescue", 5, 20, 20, TeamStatus.AVAILABLE);
        
        com.resqnepal.service.TeamService ts = new com.resqnepal.service.TeamService();
        ts.addTeam(tA);
        ts.addTeam(tB);
        rescueService.setAllTeams(Arrays.asList(tA, tB));

        Victim v = new Victim(1, "Test", 30, "123", 11, 11);
        rescueService.addVictim(v);

        RescueRequest req = new RescueRequest("RQ-1", 1, "Flood", Severity.HIGH, 2, "Flood Rescue", RequestStatus.REPORTED, "");
        rescueService.addRequest(req);

        RescueOperation op = rescueService.processNextRequest();
        
        assertNotNull(op);
        assertEquals("T-A", op.getTeamId());
        assertEquals(RequestStatus.ASSIGNED, req.getStatus());
        assertEquals(TeamStatus.ON_MISSION, tA.getStatus());
    }

    @Test
    public void testMatchingTeamOnMissionThrowsException() throws Exception {
        // TEST 3
        RescueTeam tA = new RescueTeam("T-A", "Team A", "Flood Rescue", 5, 10, 10, TeamStatus.ON_MISSION);
        rescueService.setAllTeams(Arrays.asList(tA));

        Victim v = new Victim(1, "Test", 30, "123", 11, 11);
        rescueService.addVictim(v);

        RescueRequest req = new RescueRequest("RQ-1", 1, "Flood", Severity.HIGH, 2, "Flood Rescue", RequestStatus.REPORTED, "");
        rescueService.addRequest(req);

        assertThrows(NoTeamAvailableException.class, () -> {
            rescueService.processNextRequest();
        });
    }

    @Test
    public void testNoTeamWithSpecializationThrowsException() throws Exception {
        // TEST 4
        RescueTeam tA = new RescueTeam("T-A", "Team A", "Medical", 5, 10, 10, TeamStatus.AVAILABLE);
        rescueService.setAllTeams(Arrays.asList(tA));

        Victim v = new Victim(1, "Test", 30, "123", 11, 11);
        rescueService.addVictim(v);

        RescueRequest req = new RescueRequest("RQ-1", 1, "Flood", Severity.HIGH, 2, "Flood Rescue", RequestStatus.REPORTED, "");
        rescueService.addRequest(req);

        assertThrows(NoTeamAvailableException.class, () -> {
            rescueService.processNextRequest();
        });
    }

    @Test
    public void testInvalidRequestPeopleAffected() {
        // TEST 5
        RescueRequest req = new RescueRequest("RQ-1", 1, "Flood", Severity.HIGH, 0, "Flood Rescue", RequestStatus.REPORTED, "");
        
        assertThrows(InvalidRequestException.class, () -> {
            rescueService.addRequest(req);
        });
    }

    @Test
    public void testRequestRemainsQueuedOnNoTeam() throws Exception {
        // TEST 6
        // 1. Create a valid HIGH-priority rescue request.
        RescueRequest req = new RescueRequest("RQ-6", 1, "Flood", Severity.HIGH, 2, "Flood Rescue", RequestStatus.REPORTED, "");
        
        // 2. Add it to the RescueService queue.
        rescueService.addRequest(req);
        
        // 3. Provide no suitable available team (empty list)
        rescueService.setAllTeams(Arrays.asList());

        Victim v = new Victim(1, "Test", 30, "123", 11, 11);
        rescueService.addVictim(v);

        // 4. Call processNextRequest() -> Expect NoTeamAvailableException
        assertThrows(NoTeamAvailableException.class, () -> {
            rescueService.processNextRequest();
        });

        // 6. Verify:
        //    queue size is still 1
        assertEquals(1, rescueService.getQueueSize());
        //    request status is still QUEUED
        assertEquals(RequestStatus.QUEUED, req.getStatus());
        //    peekNextRequest() returns the same request
        assertEquals(req, rescueService.peekNextRequest());

        // Then add a second suitable team.
        RescueTeam tA = new RescueTeam("T-A2", "Team A2", "Flood Rescue", 5, 10, 10, TeamStatus.AVAILABLE);
        com.resqnepal.service.TeamService ts = new com.resqnepal.service.TeamService();
        ts.addTeam(tA);
        rescueService.setAllTeams(Arrays.asList(tA));

        // Call processNextRequest() again.
        RescueOperation op = rescueService.processNextRequest();

        // Verify:
        assertNotNull(op);
        // request status = ASSIGNED
        assertEquals(RequestStatus.ASSIGNED, req.getStatus());
        // team status = ON_MISSION
        assertEquals(TeamStatus.ON_MISSION, tA.getStatus());
        // queue size = 0
        assertEquals(0, rescueService.getQueueSize());
    }
}
