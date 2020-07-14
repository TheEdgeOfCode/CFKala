package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

public class DiscountCodeCustomer {
    @FXML
    private JFXButton back;
    @FXML private JFXButton cartButt;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private TableView codesTable;
    @FXML private TableColumn codeCol;
    @FXML private TableColumn countCol;
    @FXML private Pane disCode;
    @FXML private Label percent;
    @FXML private Label code;
    @FXML private Label max;
    @FXML private Label date;
}
