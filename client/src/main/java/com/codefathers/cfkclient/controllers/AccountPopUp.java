package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.user.LoginDto;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.List;

import static com.codefathers.cfkclient.controllers.Notification.show;

public class AccountPopUp {
    public JFXTextField username;
    public JFXPasswordField password;
    public JFXButton signIn;
    public JFXButton login;


    private static final Paint redColor = Paint.valueOf("#c0392b");
    private static final Paint blueColor = Paint.valueOf("#405aa8");

    @FXML
    public void initialize() {
        normalize();
        signIn.setOnAction(event -> signInAction());
        login.setOnAction(event -> loginAction());
    }

    private void normalize() {
        resetSettingForFields(username, "Username");
        resetSettingForFields(password, "PassWord");
    }

    private void loginAction() {
        if (username.getText().isBlank()) {
            errorField(username, "Username : Required");
        } else if (password.getText().isBlank()) {
            errorField(password, "Password : Required");
        } else {
            try {
                String role = Connector.getInstance().login(new LoginDto(username.getText(),password.getText()));
                CacheData.getInstance().setUsername(username.getText());
                CacheData.getInstance().setRole(role);
                List<Window> stages = Stage.getWindows().filtered(Window::isFocused);
                show("Successful", "Logged In Successfully", stages.get(0), false);
            } catch (Exception e) {
                List<Window> stages = Stage.getWindows().filtered(Window::isFocused);
                show("Error", e.getMessage(), stages.get(0), true);
            }
        }
    }

    private void signInAction() {
        try {
            Scene scene = new Scene(CFK.loadFXML("SignInUp"));
            Stage stage = ((Stage) Stage.getWindows().filtered(Window::isFocused).get(0));
            CFK.moveSceneOnMouse(scene, stage);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
