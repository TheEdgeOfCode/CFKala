package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class EditPassDialog {
    private static String result = "";
    private static double xOffset;
    private static double yOffset;
    private Connector connector = Connector.getInstance();

    @FXML
    private AnchorPane rootPane;
    @FXML private JFXPasswordField current;
    @FXML private JFXPasswordField newPass;
    @FXML private JFXPasswordField repeat;
    @FXML private JFXButton submitButt;
    @FXML private JFXButton cancel;

    public String show() {
        result = null;
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        try {
            Scene scene = new Scene(CFK.loadFXML("EditPassDialog"));
            scene.setFill(Color.TRANSPARENT);
            scene.setOnMousePressed(e -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });
            scene.setOnMouseDragged(e -> {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            });
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @FXML
    public void initialize() {
        makeFade(0, 1).play();
        submitButt.setOnMouseClicked(this::submit);
        cancel.setOnMouseClicked(e -> {
            result = null;
            close();
        });
        current.textProperty().addListener(e -> {
            current.setPromptText("Current Password");
            current.setFocusColor(Paint.valueOf("#4059a9"));
        });
        newPass.textProperty().addListener(e -> {
            result = newPass.getText();
            newPass.setPromptText("New Password");
            newPass.setFocusColor(Paint.valueOf("#4059a9"));
        });
        repeat.textProperty().addListener(e -> {
            repeat.setPromptText("Current Password");
            repeat.setFocusColor(Paint.valueOf("#4059a9"));
        });
        PasswordStrength.bindPassField(newPass);
    }

    private FadeTransition makeFade(int from, int to) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(300));
        fadeTransition.setNode(rootPane);
        fadeTransition.setFromValue(from);
        fadeTransition.setToValue(to);
        return fadeTransition;
    }

    private void close() {
        Stage window = (Stage) rootPane.getScene().getWindow();
        FadeTransition fadeTransition = makeFade(1, 0);
        fadeTransition.setOnFinished(event -> window.close());
        fadeTransition.play();
    }

    private boolean isSame() {
        return newPass.getText().equals(repeat.getText());
    }

    private boolean isCorrectPass() {
        try {
            return connector.viewPersonalInfo().getPassword().equals(current.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void submit(MouseEvent e) {
        if (!isCorrectPass()) {
            current.setPromptText("Incorrect Password");
            current.setFocusColor(Paint.valueOf("#c0392b"));
            current.requestFocus();
        } else {
            if (newPass.getText().isEmpty()) {
                newPass.setFocusColor(Paint.valueOf("#c0392b"));
                newPass.setPromptText("Cannot Be Empty");
                newPass.requestFocus();
            } else if (PasswordStrength.calculatePasswordStrength(newPass.getText()) < 4) {
            } else {
                if (!isSame()) {
                    repeat.setPromptText("Doesn't Match Above");
                    repeat.setFocusColor(Paint.valueOf("#c0392b"));
                    repeat.requestFocus();
                } else {
                    close();
                }
            }
        }
    }
}
