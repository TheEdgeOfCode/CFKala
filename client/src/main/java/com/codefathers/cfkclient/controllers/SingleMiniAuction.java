package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.auction.MiniAuctionDTO;
import com.codefathers.cfkclient.utils.Connector;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class SingleMiniAuction {
    @FXML private Pane pane;
    @FXML private Label auctionPrice;
    @FXML private Label endDate;
    @FXML private Label startDate;
    @FXML private Label sellerName;
    @FXML private Label productName;
    @FXML private ImageView productPreViewImage;

    public static Parent generate(MiniAuctionDTO pm) throws IOException {
        FXMLLoader loader = CFK.getFXMLLoader("SingleMiniAuction");
        Parent parent = loader.load();
        SingleMiniAuction controller = loader.getController();
        controller.initData(pm);
        return parent;
    }

    private void initData(MiniAuctionDTO pm) {
        String nameOfProduct = pm.getAuctionDTO().getProductName();
        String nameOfSeller = pm.getAuctionDTO().getSellPackageDto().getSellerUsername();
        long money = pm.getAuctionDTO().getCurrentPrice();
        String dateStart = String.valueOf(pm.getStartDate());
        String dateEnd = String.valueOf(pm.getEndDate());
        auctionPrice.setText(money + "$");
        startDate.setText(dateStart);
        endDate.setText(dateEnd);
        productName.setText(nameOfProduct);
        sellerName.setText(nameOfSeller);
        pane.setOnMouseClicked(e -> {
            CacheData.getInstance().setAuctionDTO(pm.getAuctionDTO());
            try {
                Scene scene = new Scene(CFK.loadFXML("auction_page", "MainPage", "AllAuctionPage"));
                CFK.setSceneToStage(pane, scene);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        try {
            productPreViewImage.setImage(Connector.getInstance().productMainImage(pm.getAuctionDTO().getProductId()));
        } catch (Exception ignore) {}
    }


}
