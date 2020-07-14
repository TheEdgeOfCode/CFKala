package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class Purchase {
    @FXML private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private TextField card1;
    @FXML private TextField card2;
    @FXML private TextField card3;
    @FXML private TextField card4;
    @FXML private TextField cvv2;
    @FXML private TextField pass;
    @FXML private TextField month;
    @FXML private TextField year;
    @FXML private Button purchaseButt;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Label totalPrice;
    @FXML
    private ComboBox<String> country;
    @FXML
    private ComboBox<String> city;
    @FXML
    private JFXTextArea zipCode;
    @FXML
    private JFXTextArea address;
    @FXML
    private Tab postalInformation;
    @FXML
    private Tab discount;
    @FXML
    private Tab payment;
    @FXML
    private JFXTabPane tabPane;
    @FXML
    private Button nextPagePostalInformation;
    @FXML
    private Button nextPageDiscount;
    @FXML
    private Button previousPageDiscount;
    @FXML
    private JFXTextField discountField;
    @FXML
    private JFXButton useDiscount;
}
