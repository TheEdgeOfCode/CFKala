package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class OffManager {
    @FXML
    private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private JFXButton create;

    @FXML private JFXSlider createPercentage;
    @FXML private DatePicker crStartDt;
    @FXML private DatePicker crEndDate;

    @FXML private JFXSlider percent;
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    @FXML private Label offStatus;

    @FXML private ListView productsList;
    @FXML private ComboBox availableProducts;
    @FXML private Button deleteProduct;
    @FXML private Button addProduct;

    @FXML private JFXButton confirm;
    @FXML private JFXButton reset;
    @FXML private JFXButton removeOff;

    @FXML private ListView offsList;
}
