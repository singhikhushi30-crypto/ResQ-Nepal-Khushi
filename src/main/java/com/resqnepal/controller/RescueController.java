package com.resqnepal.controller;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.exception.NoTeamAvailableException;
import com.resqnepal.model.RescueOperation;
import com.resqnepal.model.RescueRequest;
import com.resqnepal.model.Victim;
import com.resqnepal.model.enums.Severity;
import com.resqnepal.AppContext;
import com.resqnepal.service.RescueService;
import com.resqnepal.service.TeamService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class RescueController {

    @FXML private TextField victimNameField;
    @FXML private TextField victimAgeField;
    @FXML private TextField victimPhoneField;
    @FXML private TextField locXField;
    @FXML private TextField locYField;
    @FXML private TextField emergencyTypeField;
    @FXML private TextField peopleAffectedField;
    @FXML private ComboBox<Severity> severityComboBox;
    @FXML private TextField specializationField;
    @FXML private TextField descriptionField;

    @FXML private TableView<RescueRequest> requestTable;
    @FXML private TableColumn<RescueRequest, String> idCol;
    @FXML private TableColumn<RescueRequest, String> victimCol;
    @FXML private TableColumn<RescueRequest, String> typeCol;
    @FXML private TableColumn<RescueRequest, String> severityCol;
    @FXML private TableColumn<RescueRequest, Integer> peopleCol;
    @FXML private TableColumn<RescueRequest, String> specCol;
    @FXML private TableColumn<RescueRequest, String> statusCol;
    @FXML private TableColumn<RescueRequest, String> descCol;

    private RescueService rescueService;
    private TeamService teamService;

    @FXML
    public void initialize() {
        rescueService = AppContext.getRescueService();
        teamService = AppContext.getTeamService();

        severityComboBox.setItems(FXCollections.observableArrayList(Severity.values()));

        idCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        victimCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getVictimId())));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        severityCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSeverity().name()));
        peopleCol.setCellValueFactory(new PropertyValueFactory<>("peopleAffected"));
        specCol.setCellValueFactory(new PropertyValueFactory<>("requiredSpecialization"));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        refreshTable();
    }

    @FXML
    public void registerRequest() {
        try {
            String name = victimNameField.getText();
            int age = Integer.parseInt(victimAgeField.getText());
            String phone = victimPhoneField.getText();
            double locX = Double.parseDouble(locXField.getText());
            double locY = Double.parseDouble(locYField.getText());
            
            Victim victim = new Victim(rescueService.getNextVictimId(), name, age, phone, locX, locY);
            rescueService.addVictim(victim);

            String type = emergencyTypeField.getText();
            int affected = Integer.parseInt(peopleAffectedField.getText());
            Severity severity = severityComboBox.getValue();
            String spec = specializationField.getText();
            String desc = descriptionField.getText();

            RescueRequest request = new RescueRequest("REQ-" + System.currentTimeMillis(), victim.getId(), type, severity, affected, spec, com.resqnepal.model.enums.RequestStatus.REPORTED, desc);
            rescueService.addRequest(request);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Rescue Request registered successfully.");
            clearForm();
            refreshTable();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please ensure numbers are valid.");
        } catch (InvalidRequestException e) {
            showAlert(Alert.AlertType.ERROR, "Business Rule Error", e.getMessage());
        }
    }

    @FXML
    public void processNextRequest() {
        try {
            RescueOperation op = rescueService.processNextRequest();
            if (op != null) {
                showAlert(Alert.AlertType.INFORMATION, "Team Assigned", 
                    "Request assigned!\nOperation ID: " + op.getOpId() + "\nTeam ID: " + op.getTeamId());
                refreshTable();
            } else {
                showAlert(Alert.AlertType.WARNING, "Queue Empty", "No pending rescue requests in the queue.");
            }
        } catch (NoTeamAvailableException e) {
            showAlert(Alert.AlertType.WARNING, "No Team Available", "No suitable rescue team is currently available. The request remains queued.");
        }
    }

    @FXML
    public void refreshTable() {
        rescueService.refreshTeams();
        requestTable.setItems(FXCollections.observableArrayList(rescueService.getAllRequests()));
    }

    private void clearForm() {
        victimNameField.clear();
        victimAgeField.clear();
        victimPhoneField.clear();
        locXField.clear();
        locYField.clear();
        emergencyTypeField.clear();
        peopleAffectedField.clear();
        severityComboBox.setValue(null);
        specializationField.clear();
        descriptionField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
