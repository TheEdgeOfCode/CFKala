package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class SlideShow {
    @FXML
    private AnchorPane container;
    @FXML private JFXButton previous;
    @FXML private JFXButton next;
    @FXML private Label noContent;
}
