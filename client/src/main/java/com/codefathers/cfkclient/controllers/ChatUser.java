package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.utils.Connector;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import lombok.Setter;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

public class ChatUser{
    @FXML
    private HBox root;
    @FXML private Circle image;
    @FXML private Label username;
    @FXML private Circle newMessage;
    @FXML private Label unreadAmount;

    @Setter
    private SupportAccount supportAccount;

    private static final String userPhoto = "/Images/user-png-icon-male-user-icon-512.png";
    private static Connector connector = Connector.getInstance();

    static Entry<Parent,ChatUser> build(String username,SupportAccount supportAccount) throws IOException {
        FXMLLoader loader = CFK.getFXMLLoader("chatUser");
        try {
            Parent parent = loader.load();
            ChatUser controller = loader.getController();
            controller.setSupportAccount(supportAccount);
            controller.init(username);
            return new SimpleEntry<>(parent,controller);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @FXML
    private void initialize(){}

    private void init(String username){
        Image image = loadImage(username);
        this.image.setFill(new ImagePattern(image));
        this.username.setText(username);
        root.setOnMouseClicked(event -> supportAccount.changeChat(username));
    }

    private Image loadImage(String username) {
        try {
            Image image = connector.userImage(username);
            if (image != null) {
                return image;
            }else throw new Exception();
        } catch (Exception e) {
            return new Image(userPhoto);
        }
    }

    void setRead(){
        unreadAmount.setVisible(false);
        newMessage.setVisible(false);
    }

    void setUnreadAmount(int amount){
        if (amount==0) setRead();
        else {
            unreadAmount.setVisible(true);
            unreadAmount.setText("+" + amount);
            newMessage.setVisible(true);
        }
    }
}
