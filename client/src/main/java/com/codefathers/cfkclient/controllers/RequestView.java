package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class RequestView {
    @FXML
    private JFXButton backButt;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;

    @FXML private TableView table;
    @FXML private TableColumn id;
    @FXML private TableColumn request;
    @FXML private TableColumn status;
}
