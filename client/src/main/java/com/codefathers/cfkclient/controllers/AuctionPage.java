package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Random;

public class AuctionPage {
    public JFXButton back;
    public JFXButton minimize;
    public JFXButton close;

    public JFXTextArea message;
    public VBox messageContainer;
    public JFXButton send;

    public JFXTextField priceEntry;
    public JFXButton applyChange;
    public JFXButton increaseBy100;
    public JFXButton increaseBy1000;

    public ImageView productImage;
    public Label productName;
    public Label price;

    public VBox logContainer;

    private static String[] TEST_USERS = {"Ali", "Hatam", "Marmof", "Sapa", "You"};

    @FXML
    public void initialize(){
        initButts();
        initLabels();
        initMessages();
    }

    private void initButts() {
        send.setOnAction(event -> handleSend());
        applyChange.setOnAction(event -> handleApply());
        applyChange.setTooltip(new Tooltip("Apply Price"));
        increaseBy100.setOnAction(event -> handelIncrease(100));
        increaseBy1000.setOnAction(event -> handelIncrease(1000));
    }

    private void handelIncrease(int amount) {
        long previous = Long.parseLong(price.getText().substring(0, price.getText().length() - 1));
        String finalPrice = (amount + previous) + "$";
        price.setText(finalPrice);
        try {
            logContainer.getChildren().add(new AuctionLog().createLog(getRandomUser(), finalPrice));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleApply() {
        try {
            logContainer.getChildren().add(new AuctionLog().createLog(getRandomUser(), priceEntry.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        price.setText(priceEntry.getText() + "$");
        priceEntry.clear();
    }

    private String getRandomUser() {
        return TEST_USERS[new Random().nextInt(TEST_USERS.length)];
    }

    private void handleSend() {
        try {
            messageContainer.getChildren().add(new AuctionMessage().createMessage(getRandomUser(), message.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        message.clear();
    }

    private void initLabels() {

    }

    private void initMessages() {

    }
}
