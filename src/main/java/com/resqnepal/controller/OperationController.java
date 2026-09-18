package com.resqnepal.controller;

import com.resqnepal.AppContext;
import com.resqnepal.model.RescueOperation;
import com.resqnepal.service.RescueService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class OperationController {

    @FXML private TableView<RescueOperation> operationTable;
    @FXML private TableColumn<RescueOperation, String> opIdCol;
    @FXML private TableColumn<RescueOperation, String> reqIdCol;
    @FXML private TableColumn<RescueOperation, String> teamIdCol;
    @FXML private TableColumn<RescueOperation, String> startCol;
    @FXML private TableColumn<RescueOperation, String> endCol;
    @FXML private TableColumn<RescueOperation, String> statusCol;

    private RescueService rescueService;

    @FXML
    public void initialize() {
        rescueService = AppContext.getRescueService();

        opIdCol.setCellValueFactory(new PropertyValueFactory<>("opId"));
        reqIdCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));
        teamIdCol.setCellValueFactory(new PropertyValueFactory<>("teamId"));
        startCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().toString()));
        endCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEndTime() != null) {
                return new SimpleStringProperty(cellData.getValue().getEndTime().toString());
            }
            return new SimpleStringProperty("-");
        });
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().name()));

        refreshTable();
    }

    @FXML
    public void refreshTable() {
        operationTable.setItems(FXCollections.observableArrayList(rescueService.getAllOperations()));
    }
}
