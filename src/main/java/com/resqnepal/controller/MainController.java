package com.resqnepal.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    public void initialize() {
        loadPage("dashboard.fxml");
    }

    @FXML
    public void showDashboard() { loadPage("dashboard.fxml"); }
    @FXML
    public void showRescueRequests() { loadPage("rescue.fxml"); }
    @FXML
    public void showRescueTeams() { loadPage("teams.fxml"); }
    @FXML
    public void showResources() { loadPage("resources.fxml"); }
    @FXML
    public void showShelters() { loadPage("shelters.fxml"); }
    @FXML
    public void showMissingPersons() { loadPage("missing-persons.fxml"); }
    @FXML
    public void showRescueOperations() { loadPage("operations.fxml"); }
    @FXML
    public void showCaseStudy() { loadPage("case-study.fxml"); }

    private void loadPage(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/resqnepal/" + fxmlFile));
            mainBorderPane.setCenter(root);
        } catch (IOException e) {
            System.err.println("Error loading " + fxmlFile);
            e.printStackTrace();
        }
    }
}
