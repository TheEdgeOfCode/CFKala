package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

public class ScoreGetter {
    private static String result = "";
    private static double xOffset;
    private static double yOffset;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXTextField field;
    @FXML
    private JFXButton submitButt, cancel;
}
