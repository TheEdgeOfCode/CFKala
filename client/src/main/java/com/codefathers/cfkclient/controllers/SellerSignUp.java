package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class SellerSignUp {
    @FXML
    private AnchorPane rootPane;
    @FXML private JFXButton back;
    @FXML private JFXTextField Company;
    @FXML private JFXButton signUpSubmit;
    @FXML private JFXButton createCompanyButt;
    private String[] info;
}
