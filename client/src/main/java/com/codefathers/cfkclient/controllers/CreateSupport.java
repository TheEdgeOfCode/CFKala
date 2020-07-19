package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.dtos.user.ManagerDTO;
import com.codefathers.cfkclient.dtos.user.UserDTO;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;

import java.io.IOException;

public class CreateSupport extends BackAbleController {
    @FXML private JFXTextField username;
    @FXML private JFXPasswordField password;
    @FXML private JFXPasswordField rePassword;
    @FXML private JFXTextField firstName;
    @FXML private JFXTextField lastName;
    @FXML private JFXTextField email;
    @FXML private JFXTextField phone;
    @FXML private JFXButton submit;
    @FXML private JFXButton back;

    private Connector connector = Connector.getInstance();

    private static final Paint redColor = Paint.valueOf("#c0392b");
    private static final Paint blueColor = Paint.valueOf("#405aa8");

    @FXML
    public void initialize() {
        initButtons();
        initFields();
    }

    private void initFields() {
        resetSettingForFields(username, "Username");
        resetSettingForFields(firstName, "First Name");
        resetSettingForFields(lastName, "Last Name");
        resetSettingForFields(email, "Email");
        resetSettingForFields(phone, "Phone Number");
        resetSettingForFields(password, "Password");
        resetSettingForFields(rePassword, "Repeat Password");
        PasswordStrength.bindPassField(password);
    }

    private void resetSettingForFields(JFXTextField field, String prompt) {
        field.textProperty().addListener(e -> {
            field.setFocusColor(blueColor);
            field.setPromptText(prompt);
        });
    }

    private void resetSettingForFields(JFXPasswordField field, String prompt) {
        field.textProperty().addListener(e -> {
            field.setFocusColor(blueColor);
            field.setPromptText(prompt);
        });
    }


    private void initButtons() {
        back.setOnAction(event -> handleBack());
        submit.setOnAction(event -> handleSubmit());
    }

    private void handleSubmit() {
        if (checkForEmptyValues()) {
            sendSignUpRequest();
            Notification.show("Successful", "The New Manager was Created!!!", back.getScene().getWindow(), false);
            handleBack();
        } else {
            Notification.show("Error", "Please Check The Fields.", back.getScene().getWindow(), true);
        }
    }

    private void sendSignUpRequest() {
        try {
            connector.createSupport(getManagerDTO());
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private UserDTO getManagerDTO() {
        return new UserDTO(
                username.getText(),
                password.getText(),
                firstName.getText(),
                lastName.getText(),
                email.getText(),
                phone.getText()
        );
    }

    private boolean checkForEmptyValues() {
        if (username.getText().isEmpty()) {
            errorField(username, "Username Is Required");
            return false;
        } else if (password.getText().isEmpty()) {
            return false;
        } else if (PasswordStrength.calculatePasswordStrength(password.getText()) < 4) {
            return false;
        } else if (!rePassword.getText().equals(password.getText())) {
            errorField(rePassword, "Doesn't Match Above");
            return false;
        } else if (firstName.getText().isEmpty()) {
            errorField(firstName, "First Name Is Required");
            return false;
        } else if (lastName.getText().isEmpty()) {
            errorField(lastName, "Last Name Is Required");
            return false;
        } else if (email.getText().isEmpty()) {
            errorField(email, "Email Is Required");
            return false;
        } else if (!email.getText().matches(("\\S+@\\S+\\.(org|net|ir|com|uk|site)"))) {
            errorField(email, "Wrong Email Format");
            return false;
        } else if (phone.getText().isEmpty()) {
            errorField(phone, "Phone Number Is Required");
            return false;
        } else return true;
    }

    private void errorField(JFXTextField field, String prompt) {
        field.setPromptText(prompt);
        field.setFocusColor(redColor);
        field.requestFocus();
    }

    private void errorField(JFXPasswordField field, String prompt) {
        field.setPromptText(prompt);
        field.setFocusColor(redColor);
        field.requestFocus();
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
