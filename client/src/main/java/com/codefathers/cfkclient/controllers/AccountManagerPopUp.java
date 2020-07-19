package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.utils.Connector;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;

public class AccountManagerPopUp {
    public Circle image;
    public Label username;
    public Label role;
    public Button gotoAccount;

    private static final String userPhoto = "/Images/user-png-icon-male-user-icon-512.png";

    @FXML
    public void initialize() {
        username.setText(CacheData.getInstance().getUsername());
        role.setText(CacheData.getInstance().getRole());
        loadImage();
        gotoAccount.setOnAction(event -> gotoManager());
    }

    private void gotoManager() {
        Stage stage = ((Stage) Stage.getWindows().filtered(Window::isFocused).get(0));
        Scene scene = null;
        try {
            switch (CacheData.getInstance().getRole()) {
                case "Manager":
                case "manager":
                    scene = new Scene(CFK.loadFXML("ManagerAccount", "MainPage"));
                    break;
                case "Customer":
                case "customer":
                    scene = new Scene(CFK.loadFXML("CustomerAccount", "MainPage"));
                    break;
                case "Seller":
                case "seller":
                    scene = new Scene(CFK.loadFXML("SellerAccount", "MainPage"));
                    break;
            }
            CFK.moveSceneOnMouse(scene, stage);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadImage() {
        Image image = null;
        try {
            image = Connector.getInstance().userImage(username.getText());
        } catch (Exception ignore) {}
        if (image == null) {
            image = new Image(userPhoto);
        }
        this.image.setFill(new ImagePattern(image));
    }
}
