package com.resqnepal.controller;

import com.resqnepal.AppContext;
import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.model.RescueTeam;
import com.resqnepal.model.enums.TeamStatus;
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

public class TeamController {

    @FXML private TextField teamNameField;
    @FXML private TextField specializationField;
    @FXML private TextField membersField;
    @FXML private TextField locXField;
    @FXML private TextField locYField;
    @FXML private ComboBox<TeamStatus> statusComboBox;

    @FXML private TableView<RescueTeam> teamTable;
    @FXML private TableColumn<RescueTeam, String> idCol;
    @FXML private TableColumn<RescueTeam, String> nameCol;
    @FXML private TableColumn<RescueTeam, String> specCol;
    @FXML private TableColumn<RescueTeam, Integer> memberCol;
    @FXML private TableColumn<RescueTeam, Double> locXCol;
    @FXML private TableColumn<RescueTeam, Double> locYCol;
    @FXML private TableColumn<RescueTeam, String> statusCol;

    private TeamService teamService;

    @FXML
    public void initialize() {
        teamService = AppContext.getTeamService();
        statusComboBox.setItems(FXCollections.observableArrayList(TeamStatus.values()));
        statusComboBox.setValue(TeamStatus.AVAILABLE);

        idCol.setCellValueFactory(new PropertyValueFactory<>("teamId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        specCol.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        memberCol.setCellValueFactory(new PropertyValueFactory<>("memberCount"));
        locXCol.setCellValueFactory(new PropertyValueFactory<>("locX"));
        locYCol.setCellValueFactory(new PropertyValueFactory<>("locY"));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        teamTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        refreshTable();
    }

    @FXML
    public void addTeam() {
        try {
            RescueTeam team = getTeamFromForm();
            team.setTeamId("T-" + System.currentTimeMillis());
            teamService.addTeam(team);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Team added successfully.");
            clearForm();
            refreshTable();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please ensure numbers are valid.");
        } catch (InvalidRequestException e) {
            showAlert(Alert.AlertType.ERROR, "Business Rule Error", e.getMessage());
        }
    }

    @FXML
    public void updateTeam() {
        RescueTeam selected = teamTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a team to update.");
            return;
        }
        try {
            RescueTeam updated = getTeamFromForm();
            updated.setTeamId(selected.getTeamId());
            teamService.updateTeam(updated);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Team updated successfully.");
            clearForm();
            refreshTable();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please ensure numbers are valid.");
        } catch (InvalidRequestException e) {
            showAlert(Alert.AlertType.ERROR, "Business Rule Error", e.getMessage());
        }
    }

    @FXML
    public void changeStatus() {
        RescueTeam selected = teamTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a team.");
            return;
        }
        TeamStatus newStatus = statusComboBox.getValue();
        if (newStatus != null) {
            teamService.changeTeamStatus(selected.getTeamId(), newStatus);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Team status updated.");
            refreshTable();
        }
    }

    @FXML
    public void refreshTable() {
        teamTable.setItems(FXCollections.observableArrayList(teamService.getAllTeams()));
        AppContext.getRescueService().refreshTeams();
    }

    private RescueTeam getTeamFromForm() {
        String name = teamNameField.getText();
        String spec = specializationField.getText();
        int members = Integer.parseInt(membersField.getText());
        double locX = Double.parseDouble(locXField.getText());
        double locY = Double.parseDouble(locYField.getText());
        TeamStatus status = statusComboBox.getValue();

        return new RescueTeam("", name, spec, members, locX, locY, status);
    }

    private void populateForm(RescueTeam team) {
        teamNameField.setText(team.getName());
        specializationField.setText(team.getSpecialization());
        membersField.setText(String.valueOf(team.getMemberCount()));
        locXField.setText(String.valueOf(team.getLocX()));
        locYField.setText(String.valueOf(team.getLocY()));
        statusComboBox.setValue(team.getStatus());
    }

    private void clearForm() {
        teamNameField.clear();
        specializationField.clear();
        membersField.clear();
        locXField.clear();
        locYField.clear();
        statusComboBox.setValue(TeamStatus.AVAILABLE);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
