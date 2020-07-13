package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

public class ContentManager {
    @FXML private JFXButton back;
    @FXML private JFXButton refresh;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private JFXTextField crTitle;
    @FXML private JFXTextArea crContent;
    @FXML private JFXButton submit;
    @FXML private Label title;
    @FXML private Text content;
    @FXML private JFXButton remove;
    @FXML private ListView contentList;
}
