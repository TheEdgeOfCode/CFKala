package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

public class CreateAuction {
    @FXML public ComboBox products;
    @FXML private JFXButton create;
    @FXML private DatePicker endDate;
    @FXML private DatePicker startDate;

    @FXML
    public void initialize() {

    }

}
