package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.content.AdPM;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Random;

public class Advertise {
    public AnchorPane root;
    // TODO: 7/21/2020 Change to circle
    public ImageView image;
    public Label name;
    public Label seller;

    static final String[] COLORS = {
            "#ff9ff3",
            "#feca57",
            "#ff6b6b",
            "#48dbfb",
            "#1dd1a1",
            "#706fd3",
            "#ffda79",
            "#63cdda"
    };

    static Parent createAdvertise(AdPM pm) {
        FXMLLoader loader = CFK.getFXMLLoader("Advertise");
        try {
            Parent parent = loader.load();
            Advertise controller = loader.getController();
            controller.initData(pm);
            return parent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    private void initialize() {
        root.setStyle("-fx-background-color: " + getRandomColor() + ";");
    }

    private void initData(AdPM pm) {
        name.setText(pm.getName());
        seller.setText(pm.getSeller());
        root.setOnMouseClicked(event -> gotoProduct(pm.getProductId()));
        Image imageBy = new Image(new ByteArrayInputStream(pm.getImage()));
        image.setImage(imageBy);
    }

    private void gotoProduct(int productId) {
        CacheData.getInstance().setProductId(productId);
        try {
            Scene scene = new Scene(CFK.loadFXML("ProductDigest", "MainPage"));
            Stage stage = new Stage(StageStyle.UNDECORATED);
            CFK.moveSceneOnMouse(scene, stage);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getRandomColor() {
        return COLORS[new Random().nextInt(COLORS.length)];
    }
}
