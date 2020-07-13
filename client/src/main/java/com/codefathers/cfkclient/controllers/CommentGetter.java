package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;

public class CommentGetter {
    @FXML
    private JFXTextField title;
    @FXML private JFXTextArea comment;
    @FXML private JFXButton cancel;
    @FXML private JFXButton submit;
}
