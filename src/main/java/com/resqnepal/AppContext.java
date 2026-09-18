package com.resqnepal;

import com.resqnepal.service.RescueService;
import com.resqnepal.service.TeamService;

/**
 * Holds service instances used across JavaFX screens so data stays in sync.
 */
public final class AppContext {

    private static RescueService rescueService;
    private static TeamService teamService;

    private AppContext() {
    }

    public static void init() {
        teamService = new TeamService();
        rescueService = new RescueService();
        rescueService.attachTeamService(teamService);
        rescueService.loadPendingWorkFromDatabase();
    }

    public static RescueService getRescueService() {
        return rescueService;
    }

    public static TeamService getTeamService() {
        return teamService;
    }
}
