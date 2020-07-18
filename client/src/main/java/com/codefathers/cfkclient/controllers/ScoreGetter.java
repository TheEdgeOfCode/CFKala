package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

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

    public int show() {
        result = "0";
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        try {
            Scene scene = new Scene(CFK.loadFXML("ScoreGetter"));
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
        return Integer.parseInt(result);
    }

    @FXML
    public void initialize() {
        makeFade(0, 1).play();
        field.setPromptText("Score");
        submitButt.setOnMouseClicked(e -> {
            if (result.isEmpty()) {
                field.setPromptText("Score" + " : Cannot Be Empty");
                field.setFocusColor(Paint.valueOf("#c0392b"));
                field.requestFocus();
            } else if (!field.getText().matches("[12345]")) {
                field.setPromptText("Score" + " : Enter A Number 1-5");
                field.setFocusColor(Paint.valueOf("#c0392b"));
                field.requestFocus();
            } else
                close();
        });
        cancel.setOnMouseClicked(e -> {
            result = "0";
            close();
        });
        field.textProperty().addListener(e -> {
            result = field.getText();
            field.setPromptText("Score");
            field.setFocusColor(Paint.valueOf("#4c1c1c"));
            field.requestFocus();
        });
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
}
