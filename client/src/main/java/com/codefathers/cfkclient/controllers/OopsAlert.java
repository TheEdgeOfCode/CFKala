package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

import static com.codefathers.cfkclient.CFK.loadFXML;

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


    @FXML
    public void initialize() {
        fade(0, 1).play();
        text.setText(message);
        close.setOnAction(e -> close());
    }

    public void show(String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.initStyle(StageStyle.UNDECORATED);
        try {
            OopsAlert.message = message;
            Scene scene = new Scene(loadFXML("OopsAlert"));
            scene.setFill(Color.TRANSPARENT);
            scene.setOnMousePressed(e -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });
            scene.setOnMouseDragged(e ->{
                window.setX(e.getScreenX() - xOffset);
                window.setY(e.getScreenY() - yOffset);
            });
            window.setScene(scene);
            window.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        Stage window = (Stage) rootPane.getScene().getWindow();
        FadeTransition fadeTransition = fade(1,0);
        fadeTransition.setOnFinished(event -> window.close());
        fadeTransition.play();
    }

    private FadeTransition fade(int from, int to) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setDuration(Duration.millis(300));
        fadeTransition.setNode(rootPane);
        fadeTransition.setFromValue(from);
        fadeTransition.setToValue(to);
        return fadeTransition;
    }
}
