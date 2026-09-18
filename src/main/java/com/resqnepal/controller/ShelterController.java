package com.resqnepal.controller;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.exception.ShelterFullException;
import com.resqnepal.model.Shelter;
import com.resqnepal.service.ShelterService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class ShelterController {

    @FXML private TextField nameField;
    @FXML private TextField locXField;
    @FXML private TextField locYField;
    @FXML private TextField maxCapacityField;
    @FXML private CheckBox medicalCheck;
    @FXML private CheckBox foodCheck;
    @FXML private CheckBox waterCheck;

    @FXML private TableView<Shelter> shelterTable;
    @FXML private TableColumn<Shelter, String> idCol;
    @FXML private TableColumn<Shelter, String> nameCol;
    @FXML private TableColumn<Shelter, Integer> maxCapCol;
    @FXML private TableColumn<Shelter, Integer> currentOccCol;
    @FXML private TableColumn<Shelter, Integer> availCapCol;
    @FXML private TableColumn<Shelter, String> medicalCol;
    @FXML private TableColumn<Shelter, String> foodCol;
    @FXML private TableColumn<Shelter, String> waterCol;

    private ShelterService shelterService;

    @FXML
    public void initialize() {
        shelterService = new ShelterService();

        idCol.setCellValueFactory(new PropertyValueFactory<>("shelterId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        maxCapCol.setCellValueFactory(new PropertyValueFactory<>("maxCapacity"));
        currentOccCol.setCellValueFactory(new PropertyValueFactory<>("currentOccupancy"));
        
        availCapCol.setCellValueFactory(cellData -> {
            int avail = cellData.getValue().getMaxCapacity() - cellData.getValue().getCurrentOccupancy();
            return new SimpleIntegerProperty(avail).asObject();
        });
        
        medicalCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isMedicalSupport() ? "Yes" : "No"));
        foodCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isFoodAvailable() ? "Yes" : "No"));
        waterCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isWaterAvailable() ? "Yes" : "No"));

        refreshTable();
    }

    @FXML
    public void addShelter() {
        try {
            String name = nameField.getText();
            double locX = Double.parseDouble(locXField.getText());
            double locY = Double.parseDouble(locYField.getText());
            int maxCap = Integer.parseInt(maxCapacityField.getText());
            boolean hasMedical = medicalCheck.isSelected();
            boolean hasFood = foodCheck.isSelected();
            boolean hasWater = waterCheck.isSelected();

            Shelter s = new Shelter("SH-" + System.currentTimeMillis(), name, locX, locY, maxCap, 0, hasMedical, hasFood, hasWater);
            shelterService.addShelter(s);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Shelter added successfully.");
            clearForm();
            refreshTable();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please ensure numbers are valid.");
        } catch (InvalidRequestException e) {
            showAlert(Alert.AlertType.ERROR, "Business Rule Error", e.getMessage());
        }
    }

    @FXML
    public void admitPeople() {
        Shelter selected = shelterTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a shelter.");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Admit People");
        dialog.setHeaderText("Admitting to: " + selected.getName());
        dialog.setContentText("Enter number of people to admit:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(qtyStr -> {
            try {
                int qty = Integer.parseInt(qtyStr);
                shelterService.admitRescuedPeople(selected.getShelterId(), qty);
                showAlert(Alert.AlertType.INFORMATION, "Success", "People admitted successfully.");
                refreshTable();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Number must be a valid integer.");
            } catch (ShelterFullException | InvalidRequestException e) {
                showAlert(Alert.AlertType.ERROR, "Admission Failed", e.getMessage());
            }
        });
    }

    @FXML
    public void refreshTable() {
        shelterTable.setItems(FXCollections.observableArrayList(shelterService.getAllShelters()));
    }

    private void clearForm() {
        nameField.clear();
        locXField.clear();
        locYField.clear();
        maxCapacityField.clear();
        medicalCheck.setSelected(false);
        foodCheck.setSelected(false);
        waterCheck.setSelected(false);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
