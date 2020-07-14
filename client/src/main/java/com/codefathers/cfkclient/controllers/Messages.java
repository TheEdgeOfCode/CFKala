package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.util.Date;

public class Messages {
    @FXML
    private JFXButton back;
    @FXML private JFXButton cartButt;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private Label subject;
    @FXML private Label date;
    @FXML private Text message;
    @FXML private TableView listTable;
    @FXML private TableColumn subjectColumn;
    @FXML private TableColumn dateColumn;
    @FXML private TableColumn readColumn;
}
