package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

public class OopsAlert {
    @FXML
    private AnchorPane rootPane;
    @FXML
    private Text text;
    @FXML
    private JFXButton close;

    private static double xOffset;
    private static double yOffset;
    private static String message;
}
