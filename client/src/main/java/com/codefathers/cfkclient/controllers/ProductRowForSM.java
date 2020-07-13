package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class ProductRowForSM {
    @FXML
    private ImageView image;
    @FXML private Label name;
    @FXML private Label id;
    @FXML private JFXButton showBut;
    @FXML private JFXButton editButt;
    @FXML private JFXButton delete;
}
