package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.auction.AuctionDTO;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class AuctionPage extends BackAbleController {
    public JFXButton back;
    public JFXButton minimize;
    public JFXButton close;

    public JFXTextArea messageArea;
    public VBox messageContainer;
    public JFXButton send;

    public JFXTextField priceEntry;
    public JFXButton applyChange;
    public JFXButton increaseBy100;
    public JFXButton increaseBy1000;

    public ImageView productImage;
    public Label productName;
    public Label firstPrice;
    public Label currentPrice;
    public Label yourPrice;
    public Label mostPriceUser;

    public VBox logContainer;

    private static String[] TEST_USERS = {"Ali", "Hatam", "Marmof", "Sapa", "You"};
    private static final String PRODUCT_PHOTO = "/Images/product.png";
    private Connector connector = Connector.getInstance();
    private CacheData cacheData = CacheData.getInstance();
    private AuctionDTO auctionDTO = cacheData.getAuctionDTO();

    @FXML
    public void initialize(){
        initButts();
        initLabels();
        //initMessages();
    }

    private void initButts() {
        minimize.setOnAction(e -> {
            Stage stage = (Stage) minimize.getScene().getWindow();
            stage.setIconified(true);
        });
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
        });
        back.setOnAction(event -> handleBack());
        send.setOnAction(event -> handleSend());
        applyChange.setOnAction(event -> handleApply());
        applyChange.setTooltip(new Tooltip("Apply Price"));
        increaseBy100.setOnAction(event -> handelIncrease(100));
        increaseBy1000.setOnAction(event -> handelIncrease(1000));
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void handelIncrease(int amount) {
        long previous = Long.parseLong(currentPrice.getText().substring(0, currentPrice.getText().length() - 1));
        String finalPrice = (amount + previous) + "$";
        currentPrice.setText(finalPrice);
        try {
            //connector.suggestPriceAuction(finalPrice);
            logContainer.getChildren().add(new AuctionLog().createLog(cacheData.getUsername(), finalPrice));
        } catch (IOException e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void handleApply() {
        if (checkInput()) {
            try {
                //connector.suggestPriceAuction(finalPrice);
                logContainer.getChildren().add(new AuctionLog().createLog(cacheData.getUsername(), priceEntry.getText()));
            } catch (IOException e) {
                Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
                e.printStackTrace();
            }
            currentPrice.setText(priceEntry.getText() + "$");
            priceEntry.clear();
        }
    }

    private boolean checkInput() {
        if (priceEntry.getText().isEmpty()){
            Notification.show("Error", "Please Enter a Number", back.getScene().getWindow(), true);
            return false;
        } else if (!priceEntry.getText().matches("\\d+")){
            Notification.show("Error", "Should be Numeric", back.getScene().getWindow(), true);
            return false;
        } else if (Long.parseLong(priceEntry.getText()) < Long.parseLong(currentPrice.getText())){
            Notification.show("Error", "Should be Numeric", back.getScene().getWindow(), true);
            return false;
        }
        return true;
    }

    private String getRandomUser() {
        return TEST_USERS[new Random().nextInt(TEST_USERS.length)];
    }

    private void handleSend() {
        try {
            String message = messageArea.getText();
            messageContainer.getChildren().add(new AuctionMessage().createMessage(cacheData.getUsername(), message));
            //connector.sendAuctionMessage(message);
        } catch (IOException e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
        messageArea.clear();
    }

    private void initLabels() {
        try {
            productImage.setImage(connector.productMainImage(auctionDTO.getProductId()));
            productName.setText(auctionDTO.getProductName());
            firstPrice.setText(String.valueOf(auctionDTO.getSellPackageDto().getPrice()));
            currentPrice.setText(String.valueOf(auctionDTO.getCurrentPrice()));
            yourPrice.setText(String.valueOf(auctionDTO.getUserPrice()));
            mostPriceUser.setText(auctionDTO.getMostPriceUser());
        } catch (Exception e) {
            productImage.setImage(new Image(PRODUCT_PHOTO));
            e.printStackTrace();
        }

    }

    private void initMessages() {
        //TODO: Get Previously Sent Messages!!!
    }
}
