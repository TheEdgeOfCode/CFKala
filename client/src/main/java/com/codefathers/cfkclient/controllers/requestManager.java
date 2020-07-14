package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class requestManager {
    @FXML
    private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private TableColumn requestId;
    @FXML private TableColumn requestType;
    @FXML private TableColumn user;
    @FXML private TableColumn request;
    @FXML private Button decline;
    @FXML private Button accept;
    @FXML private TableView table;
}
