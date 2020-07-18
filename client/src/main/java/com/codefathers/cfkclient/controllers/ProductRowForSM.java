package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ProductRowForSM {
    @FXML
    private ImageView image;
    @FXML private Label name;
    @FXML private Label id;
    @FXML private JFXButton showBut;
    @FXML private JFXButton editButt;
    @FXML private JFXButton delete;

    private int idProduct;
    private boolean canEditThis;

    private static String nameStr;
    private static int productId;
    private final CacheData cacheData = CacheData.getInstance();
    private final Connector connector = Connector.getInstance();

    public static Parent generate(String name, int id) throws IOException {
        nameStr = name;
        productId = id;
        return CFK.loadFXML("ProductRow");
    }

    @FXML
    public void initialize(){
        canEditThis = cacheData.getRole().equals("Seller");
        name.setText(nameStr);
        id.setText("" + productId);
        editButt.setDisable(!canEditThis);
        idProduct = productId;
        try {
            image.setImage(connector.productMainImage(idProduct));
        } catch (Exception ignore) {}
        buttonInitialize();
    }

    private void buttonInitialize() {
        showBut.setOnAction(e -> show());
        editButt.setOnAction(event -> editButtHandle());
        delete.setOnAction(event -> sendDeleteRequest());
    }

    private void editButtHandle() {
        try {
            CacheData.getInstance().setProductId(idProduct);
            Scene scene = new Scene(CFK.loadFXML("EditProduct", "MainPage", "ProductPage"));
            CFK.setSceneToStage(new Stage(StageStyle.UNDECORATED), scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendDeleteRequest() {
        if (canEditThis) {
            try {
                connector.sellerRemoveProduct(idProduct);
            } catch (Exception e) {
                Notification.show("Error", e.getMessage(), delete.getScene().getWindow(), true);
            }
        } else {
            try {
                connector.removeProduct_Manager(Integer.toString(idProduct));
            } catch (Exception e) {
                Notification.show("Error", e.getMessage(), delete.getScene().getWindow(), true);
            }
        }
    }

    private void show() {
        CacheData.getInstance().setProductId(idProduct);
        try {
            Scene scene = new Scene(CFK.loadFXML("ProductDigest", "MainPage", "ProductPage"));
            CFK.setSceneToStage(new Stage(StageStyle.UNDECORATED), scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
