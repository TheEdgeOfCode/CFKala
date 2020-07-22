package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.codefathers.cfkclient.utils.Connector;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class SingleMiniProduct {
    @FXML // TODO: 7/21/2020 change to rectangle
    private ImageView productPreViewImage;
    @FXML private ImageView specialOffer;
    @FXML private Label productName;
    @FXML private Label price;
    @FXML private Label offPrice;
    @FXML private Pane pane;
    @FXML private ImageView soldOut;

    public static Parent generate(MiniProductDto pm) throws IOException {
        FXMLLoader loader = CFK.getFXMLLoader("SingleMiniProduct");
        Parent parent = loader.load();
        SingleMiniProduct controller = loader.getController();
        controller.initData(pm);
        return parent;
    }

    @FXML
    private void initialize() {

    }

    private void initData(MiniProductDto pm) {
        int offPrc = pm.getOffPrice();
        int prc = pm.getPrice();
        String name = pm.getName();
        int id = pm.getId();
        boolean onOff = (offPrc != 0);
        specialOffer.setVisible(onOff);
        offPrice.setText("Offer : " + offPrc + "$");
        price.setText("" + prc + "$");
        productName.setText(name);
        offPrice.setVisible(onOff);
        pane.setOnMouseClicked(e -> {
            CacheData.getInstance().setProductId(id);
            try {
                Scene scene = new Scene(CFK.loadFXML("ProductDigest", "MainPage", "ProductsPage"));
                CFK.setSceneToStage(pane, scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        try {
            productPreViewImage.setImage(Connector.getInstance().productMainImage(id));
        } catch (Exception ignore) {}
        soldOut.setVisible(!pm.isAvailable());
    }
}
