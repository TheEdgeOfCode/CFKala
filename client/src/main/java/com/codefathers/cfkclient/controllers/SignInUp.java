package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

@Component
public class SignInUp {
    @FXML private JFXButton back;
    @FXML private JFXButton signInButton;
    @FXML private JFXButton signUpButton;
    @FXML private Pane signInPage;
    @FXML private Pane signUpPage;
    @FXML private JFXTextField usernameUp;
    @FXML private JFXPasswordField passwordUp;
    @FXML private JFXPasswordField rePasswordUp;
    @FXML private JFXTextField firstName;
    @FXML private JFXTextField lastName;
    @FXML private JFXTextField email;
    @FXML private JFXTextField phone;
    @FXML private JFXTextField balance;
    @FXML private JFXButton sellerBut;
    @FXML private JFXButton signUpSubmit;
    @FXML private JFXButton signInSubmit;
    @FXML private JFXTextField usernameIn;
    @FXML private JFXPasswordField passwordIn;

    private final Connector connector;

    public SignInUp(Connector connector) {
        this.connector = connector;
    }


}
