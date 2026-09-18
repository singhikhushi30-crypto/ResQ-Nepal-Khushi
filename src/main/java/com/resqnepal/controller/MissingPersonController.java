package com.resqnepal.controller;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.model.MissingPerson;
import com.resqnepal.model.enums.MissingPersonStatus;
import com.resqnepal.service.MissingPersonService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class MissingPersonController {

    @FXML private TextField nameField;
    @FXML private TextField locationField;
    @FXML private ComboBox<MissingPersonStatus> statusComboBox;
    @FXML private TextField searchField;

    @FXML private TableView<MissingPerson> personTable;
    @FXML private TableColumn<MissingPerson, String> idCol;
    @FXML private TableColumn<MissingPerson, String> nameCol;
    @FXML private TableColumn<MissingPerson, String> locCol;
    @FXML private TableColumn<MissingPerson, String> statusCol;

    private MissingPersonService personService;

    @FXML
    public void initialize() {
        personService = new MissingPersonService();
        statusComboBox.setItems(FXCollections.observableArrayList(MissingPersonStatus.values()));
        statusComboBox.setValue(MissingPersonStatus.MISSING);

        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        locCol.setCellValueFactory(new PropertyValueFactory<>("lastKnownLocation"));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        refreshTable();
    }

    @FXML
    public void registerPerson() {
        try {
            String name = nameField.getText();
            String loc = locationField.getText();
            MissingPersonStatus status = statusComboBox.getValue();

            MissingPerson p = new MissingPerson("MP-" + System.currentTimeMillis(), name, loc, status);
            personService.registerMissingPerson(p);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Missing person registered successfully.");
            clearForm();
            refreshTable();
        } catch (InvalidRequestException e) {
            showAlert(Alert.AlertType.ERROR, "Business Rule Error", e.getMessage());
        }
    }

    @FXML
    public void searchPerson() {
        String query = searchField.getText();
        if (query == null || query.trim().isEmpty()) {
            refreshTable();
        } else {
            personTable.setItems(FXCollections.observableArrayList(personService.searchByName(query.trim())));
        }
    }

    @FXML
    public void updateStatus() {
        MissingPerson selected = personTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a person.");
            return;
        }
        MissingPersonStatus newStatus = statusComboBox.getValue();
        if (newStatus != null) {
            try {
                personService.updateStatus(selected.getId(), newStatus);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Status updated.");
                refreshTable();
            } catch (InvalidRequestException e) {
                showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
            }
        }
    }

    @FXML
    public void refreshTable() {
        personTable.setItems(FXCollections.observableArrayList(personService.getAllMissingPersons()));
    }

    private void clearForm() {
        nameField.clear();
        locationField.clear();
        statusComboBox.setValue(MissingPersonStatus.MISSING);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
