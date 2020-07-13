package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class EditPassDialog {
    @FXML
    private AnchorPane rootPane;
    @FXML private JFXPasswordField current;
    @FXML private JFXPasswordField newPass;
    @FXML private JFXPasswordField repeat;
    @FXML private JFXButton submitButt;
    @FXML private JFXButton cancel;
}
