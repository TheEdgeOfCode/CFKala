package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;

public class ManageUsers {
    @FXML
    private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private TableView table;
    @FXML private TableColumn usernameCol;
    @FXML private TableColumn roleCol;
    @FXML private ImageView userImage;
    @FXML private Label username;
    @FXML private Label role;
    @FXML private Label lastname;
    @FXML private Label firstname;
    @FXML private Label email;
    @FXML private Label phone;
    @FXML private JFXButton deleteBtn;
}
