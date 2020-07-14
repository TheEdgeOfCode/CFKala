package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

public class ManagerAccount {
    @FXML
    private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private Circle imageCircle;
    @FXML private JFXButton chooseProf;
    @FXML private JFXButton usersButt;
    @FXML private JFXButton productsButt;
    @FXML private JFXButton categoriesButt;
    @FXML private JFXButton discountButt;
    @FXML private JFXButton requestButt;
    @FXML private JFXButton mainContent;
    @FXML private JFXButton changePassButt;
    @FXML private Label username;
    @FXML private JFXTextField fNameText;
    @FXML private Label fName;
    @FXML private JFXButton editFNameButt;
    @FXML private JFXTextField lNameText;
    @FXML private Label lName;
    @FXML private JFXButton editLNameButt;
    @FXML private JFXTextField emailText;
    @FXML private Label email;
    @FXML private JFXButton editEMailButt;
    @FXML private JFXTextField phoneText;
    @FXML private Label phone;
    @FXML private JFXButton editPhoneButt;
    @FXML private JFXButton cancelButt;
    @FXML private JFXButton confirmButt;
    @FXML private JFXButton addManagerButt;
    @FXML private JFXButton logoutButt;
}
