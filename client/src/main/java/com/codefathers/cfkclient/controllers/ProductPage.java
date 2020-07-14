package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class ProductPage {
    @FXML
    private ToggleGroup ADOrder;
    @FXML private ToggleGroup type;
    @FXML private JFXButton back ;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private VBox panelVbox;
    @FXML private JFXComboBox<String> color;
    @FXML private JFXSlider minPrice;
    @FXML private JFXSlider maxPrice;
}
