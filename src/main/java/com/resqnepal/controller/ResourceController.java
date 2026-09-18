package com.resqnepal.controller;

import com.resqnepal.exception.InvalidRequestException;
import com.resqnepal.exception.ResourceUnavailableException;
import com.resqnepal.model.Resource;
import com.resqnepal.model.enums.ResourceStatus;
import com.resqnepal.service.ResourceService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class ResourceController {

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField quantityField;
    @FXML private TextField locXField;
    @FXML private TextField locYField;
    @FXML private ComboBox<ResourceStatus> statusComboBox;

    @FXML private TableView<Resource> resourceTable;
    @FXML private TableColumn<Resource, String> idCol;
    @FXML private TableColumn<Resource, String> nameCol;
    @FXML private TableColumn<Resource, String> catCol;
    @FXML private TableColumn<Resource, Integer> qtyCol;
    @FXML private TableColumn<Resource, Double> locXCol;
    @FXML private TableColumn<Resource, Double> locYCol;
    @FXML private TableColumn<Resource, String> statusCol;

    private ResourceService resourceService;

    @FXML
    public void initialize() {
        resourceService = new ResourceService();
        statusComboBox.setItems(FXCollections.observableArrayList(ResourceStatus.values()));
        statusComboBox.setValue(ResourceStatus.AVAILABLE);

        idCol.setCellValueFactory(new PropertyValueFactory<>("resourceId"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        locXCol.setCellValueFactory(new PropertyValueFactory<>("locX"));
        locYCol.setCellValueFactory(new PropertyValueFactory<>("locY"));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        refreshTable();
    }

    @FXML
    public void addResource() {
        try {
            String name = nameField.getText();
            String cat = categoryField.getText();
            int qty = Integer.parseInt(quantityField.getText());
            double locX = Double.parseDouble(locXField.getText());
            double locY = Double.parseDouble(locYField.getText());
            ResourceStatus status = statusComboBox.getValue();

            Resource r = new Resource("RES-" + System.currentTimeMillis(), name, cat, qty, locX, locY, status);
            resourceService.addResource(r);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Resource added successfully.");
            clearForm();
            refreshTable();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please ensure numbers are valid.");
        } catch (InvalidRequestException e) {
            showAlert(Alert.AlertType.ERROR, "Business Rule Error", e.getMessage());
        }
    }

    @FXML
    public void allocateResource() {
        Resource selected = resourceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a resource to allocate.");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Allocate Resource");
        dialog.setHeaderText("Allocating: " + selected.getName());
        dialog.setContentText("Enter quantity to allocate:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(qtyStr -> {
            try {
                int qty = Integer.parseInt(qtyStr);
                resourceService.allocateResource(selected.getResourceId(), qty);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Resource allocated successfully.");
                refreshTable();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Quantity must be a valid integer.");
            } catch (ResourceUnavailableException | InvalidRequestException e) {
                showAlert(Alert.AlertType.ERROR, "Allocation Failed", e.getMessage());
            }
        });
    }

    @FXML
    public void releaseResource() {
        Resource selected = resourceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a resource to release.");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Release Resource");
        dialog.setHeaderText("Releasing: " + selected.getName());
        dialog.setContentText("Enter quantity to return to inventory:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(qtyStr -> {
            try {
                int qty = Integer.parseInt(qtyStr);
                resourceService.releaseResource(selected.getResourceId(), qty);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Resource released successfully.");
                refreshTable();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Quantity must be a valid integer.");
            } catch (InvalidRequestException e) {
                showAlert(Alert.AlertType.ERROR, "Release Failed", e.getMessage());
            }
        });
    }

    @FXML
    public void changeStatus() {
        Resource selected = resourceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a resource.");
            return;
        }
        ResourceStatus newStatus = statusComboBox.getValue();
        if (newStatus != null) {
            resourceService.changeResourceStatus(selected.getResourceId(), newStatus);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Resource status updated.");
            refreshTable();
        }
    }

    @FXML
    public void refreshTable() {
        resourceTable.setItems(FXCollections.observableArrayList(resourceService.getAllResources()));
    }

    private void clearForm() {
        nameField.clear();
        categoryField.clear();
        quantityField.clear();
        locXField.clear();
        locYField.clear();
        statusComboBox.setValue(ResourceStatus.AVAILABLE);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
