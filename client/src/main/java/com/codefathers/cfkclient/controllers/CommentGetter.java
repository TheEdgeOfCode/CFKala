package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.exeptions.CanceledException;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class CommentGetter {
    @FXML
    private JFXTextField title;
    @FXML private JFXTextArea comment;
    @FXML private JFXButton cancel;
    @FXML private JFXButton submit;


    private static double xOffset;
    private static double yOffset;
    private static boolean canceled;
    private static String titleStr;
    private static String commentStr;

    public String[] returnAComment() throws CanceledException {
        Stage stage = new Stage(StageStyle.UNDECORATED);
        try {
            Scene scene = new Scene(CFK.loadFXML("CommentGetter"));
            scene.setFill(Color.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            scene.setOnMousePressed(e -> {
                xOffset = e.getSceneX();
                yOffset = e.getSceneY();
            });
            scene.setOnMouseDragged(e -> {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            });
            stage.setOnCloseRequest(e -> canceled = true);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!canceled) {
            return new String[]{titleStr, commentStr};
        } else {
            throw new CanceledException();
        }
    }

    @FXML
    public void initialize() {
        buttons();
        binds();
    }

    private void binds() {
        submit.disableProperty().bind(Bindings.isEmpty(title.textProperty()).or(Bindings.isEmpty(comment.textProperty())));
    }

    private void buttons() {
        submit.setOnAction(event -> handleSubmit());
        cancel.setOnAction(event -> handleCancel());
    }

    private void handleCancel() {
        canceled = true;
        Stage stage = (Stage) comment.getScene().getWindow();
        stage.close();
    }

    private void handleSubmit() {
        canceled = false;
        titleStr = title.getText();
        commentStr = comment.getText();
        Stage stage = (Stage) comment.getScene().getWindow();
        stage.close();
    }
}
