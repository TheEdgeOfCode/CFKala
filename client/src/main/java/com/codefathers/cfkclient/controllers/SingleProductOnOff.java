package com.codefathers.cfkclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

public class SingleProductOnOff {
    @FXML
    private ImageView image;
    @FXML private Label name;
    @FXML private Label percent;
    @FXML private Label hour;
    @FXML private Label min;
    @FXML private Label sec;
    @FXML private Label price;
    @FXML private Label days;
    @FXML private Rectangle root;
    @FXML private ImageView soldOut;
    @FXML private AnchorPane pane;
}
