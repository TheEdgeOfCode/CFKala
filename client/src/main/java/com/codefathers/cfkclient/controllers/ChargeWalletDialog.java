package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
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

public class ChargeWalletDialog {

    private static long result = 0;
    private static double xOffset;
    private static double yOffset;
    private Connector connector = Connector.getInstance();

    @FXML
    private AnchorPane rootPane;
    public JFXPasswordField money;
    @FXML private JFXButton submitButt;
    @FXML private JFXButton cancel;

    public long show() {
        result = 0;
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        try {
            Scene scene = new Scene(CFK.loadFXML("chargeWalletDialog"));
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
            result = 0;
            close();
        });
        money.textProperty().addListener(e -> {
            money.setPromptText("Money");
            money.setFocusColor(Paint.valueOf("#4059a9"));
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

    private void submit(MouseEvent e) {
        if (money.getText().isEmpty()){
            money.setFocusColor(Paint.valueOf("#c0392b"));
            money.setPromptText("Cannot Be Empty");
            money.requestFocus();
        } else if (!money.getText().matches("//d+")){
            money.setFocusColor(Paint.valueOf("#c0392b"));
            money.setPromptText("Should Be Numerical");
            money.requestFocus();
        } else {
            result = Long.parseLong(money.getText());
            close();
        }
    }
}
