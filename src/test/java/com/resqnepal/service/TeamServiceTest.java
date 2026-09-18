package com.resqnepal.service;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.model.RescueTeam;
import com.resqnepal.model.enums.TeamStatus;
import com.resqnepal.repository.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TeamServiceTest {
    private TeamService teamService;

    @BeforeEach
    public void setUp() {
        DatabaseManager.setTestMode(true);
        DatabaseManager.initializeDatabase();
        teamService = new TeamService();
    }

    @AfterEach
    public void tearDown() {
        File file = new File("data/resqnepal_test.db");
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    public void testAddValidTeam() throws Exception {
        RescueTeam team = new RescueTeam("T1", "Alpha", "Medical", 5, 10.0, 10.0, TeamStatus.AVAILABLE);
        teamService.addTeam(team);

        RescueTeam found = teamService.findTeamById("T1");
        assertNotNull(found);
        assertEquals("Alpha", found.getName());
    }

    @Test
    public void testRejectInvalidTeam() {
        RescueTeam team = new RescueTeam("T2", "", "Medical", 5, 10.0, 10.0, TeamStatus.AVAILABLE);
        assertThrows(InvalidRequestException.class, () -> {
            teamService.addTeam(team);
        });
    }

    @Test
    public void testChangeTeamStatus() throws Exception {
        RescueTeam team = new RescueTeam("T3", "Beta", "Medical", 5, 10.0, 10.0, TeamStatus.AVAILABLE);
        teamService.addTeam(team);
        
        teamService.changeTeamStatus("T3", TeamStatus.ON_MISSION);
        
        RescueTeam found = teamService.findTeamById("T3");
        assertEquals(TeamStatus.ON_MISSION, found.getStatus());
    }

    @Test
    public void testFindAvailableTeams() throws Exception {
        teamService.addTeam(new RescueTeam("T4", "Charlie", "Fire", 5, 10.0, 10.0, TeamStatus.AVAILABLE));
        teamService.addTeam(new RescueTeam("T5", "Delta", "Fire", 5, 10.0, 10.0, TeamStatus.UNAVAILABLE));
        
        List<RescueTeam> available = teamService.findAvailableTeams();
        boolean hasCharlie = available.stream().anyMatch(t -> t.getTeamId().equals("T4"));
        boolean hasDelta = available.stream().anyMatch(t -> t.getTeamId().equals("T5"));
        
        assertTrue(hasCharlie);
        assertFalse(hasDelta);
    }
}
