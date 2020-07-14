package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class CreateCompany {
    @FXML
    private AnchorPane rootPane;
    @FXML private JFXButton back;
    @FXML private JFXTextField name;
    @FXML private JFXTextField category;
    @FXML private JFXTextField phone;
    @FXML private JFXButton createButt;
}
