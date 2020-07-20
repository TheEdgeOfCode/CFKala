package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuctionMessage {
    public Label time;
    public Circle image;
    public Label user;
    public TextFlow textFlow;
    public HBox root;

    @FXML
    public void initialize(){
        initLabels();
    }

    private void initLabels() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:SS");
        time.setText(LocalDateTime.now().format(formatter));
    }

    public HBox createMessage(String username, String message) throws IOException {
        FXMLLoader loader = CFK.getFXMLLoader("messageInAuction");
        root = loader.load();
        AuctionMessage controller = loader.getController();
        controller.init(username, message);
        return root;
    }

    private void init(String username, String message) {
        user.setText(username);
        textFlow.getChildren().add(new Text(message));
        if (username.equals("You")){
            root.setAlignment(Pos.CENTER_RIGHT);
        } else {
            root.setAlignment(Pos.CENTER_LEFT);
        }
    }
}
