package com.resqnepal.controller;

import com.resqnepal.service.DashboardService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML private Label totalRequestsLabel;
    @FXML private Label criticalRequestsLabel;
    @FXML private Label queuedRequestsLabel;
    @FXML private Label availableTeamsLabel;
    @FXML private Label teamsOnMissionLabel;
    @FXML private Label activeOperationsLabel;
    @FXML private Label peopleRescuedLabel;
    @FXML private Label shelterCapacityLabel;

    private final DashboardService dashboardService = new DashboardService();

    @FXML
    public void initialize() {
        refreshDashboard();
    }

    @FXML
    public void refreshDashboard() {
        totalRequestsLabel.setText(String.valueOf(dashboardService.getTotalRescueRequests()));
        criticalRequestsLabel.setText(String.valueOf(dashboardService.getCriticalRequests()));
        queuedRequestsLabel.setText(String.valueOf(dashboardService.getQueuedRequests()));
        availableTeamsLabel.setText(String.valueOf(dashboardService.getAvailableTeams()));
        teamsOnMissionLabel.setText(String.valueOf(dashboardService.getTeamsOnMission()));
        activeOperationsLabel.setText(String.valueOf(dashboardService.getActiveOperations()));
        peopleRescuedLabel.setText(String.valueOf(dashboardService.getPeopleRescued()));
        shelterCapacityLabel.setText(String.valueOf(dashboardService.getAvailableShelterCapacity()));
    }
}
