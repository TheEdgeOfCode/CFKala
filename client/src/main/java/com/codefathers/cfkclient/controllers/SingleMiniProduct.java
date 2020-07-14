package com.codefathers.cfkclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class SingleMiniProduct {
    @FXML
    private ImageView productPreViewImage;
    @FXML private ImageView specialOffer;
    @FXML private Label productName;
    @FXML private Label price;
    @FXML private Label offPrice;
    @FXML private Pane pane;
    @FXML private ImageView soldOut;
}
