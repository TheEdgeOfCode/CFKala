package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.dtos.user.RequestDTO;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class requestManager extends BackAbleController {
    @FXML
    private JFXButton back;
    @FXML
    private JFXButton minimize;
    @FXML
    private JFXButton close;
    @FXML
    private TableColumn<RequestDTO, Integer> requestId;
    @FXML
    private TableColumn<RequestDTO, String> requestType;
    @FXML
    private TableColumn<RequestDTO, String> user;
    @FXML
    private TableColumn<RequestDTO, String> request;
    @FXML
    private Button decline;
    @FXML
    private Button accept;
    @FXML
    private TableView<RequestDTO> table;

    private final Connector connector = Connector.getInstance();

    @FXML
    public void initialize() {
        initButtons();
        initTable();
    }

    private void initButtons() {
        back.setOnAction(e -> handleBackButt());
        minimize.setOnAction(e -> minimize());
        close.setOnAction(e -> close());
        accept.setOnAction(e -> acceptRequest());
        decline.setOnAction(e -> declineRequest());
        accept.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
        decline.disableProperty().bind(Bindings.isEmpty(table.getSelectionModel().getSelectedItems()));
    }

    private void initTable() {
        try {
            List<RequestDTO> list = connector.showRequests();
            ObservableList<RequestDTO> data = FXCollections.observableArrayList(list);
            requestId.setCellValueFactory(new PropertyValueFactory<>("requestId"));
            requestType.setCellValueFactory(new PropertyValueFactory<>("requestType"));
            user.setCellValueFactory(new PropertyValueFactory<>("requesterUserName"));
            request.setCellValueFactory(new PropertyValueFactory<>("request"));
            table.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void declineRequest() {
        RequestDTO selected = table.getSelectionModel().getSelectedItem();
        int id = selected.getRequestId();
        try {
            connector.declineRequest(Integer.toString(id));
            table.getItems().remove(selected);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void acceptRequest() {
        RequestDTO selected = table.getSelectionModel().getSelectedItem();
        int id = selected.getRequestId();
        try {
            connector.acceptRequest(Integer.toString(id));
            table.getItems().remove(selected);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }

    private void minimize() {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }

    private void handleBackButt() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
