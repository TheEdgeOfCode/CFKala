package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import static com.codefathers.cfkclient.controllers.MessageBuilder.build;

public class SupportAccount extends BackAbleController {
    public HBox chatsLoading;
    public HBox usersLoading;
    @FXML private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;

    @FXML private VBox settings;
    @FXML private Circle image;
    @FXML private JFXButton changeProfile;
    @FXML private Label username;
    @FXML private Label name;
    
    @FXML private VBox chat;
    @FXML private VBox messageBox;
    @FXML private TextField text;
    @FXML private JFXButton send;

    @FXML private VBox chatBar;

    private static final String userPhoto = "/Images/user-png-icon-male-user-icon-512.png";
    private static Connector connector = Connector.getInstance();
    
    @FXML
    private void initialize(){
        test();
    }

    private void test() {
        chatBar.getChildren().addAll(generateUser("hatam"));
        messageBox.getChildren().addAll(build("Hello!\nHow You Doin' ?!","hatam",false,true),
                build("Hi, Thanks!!","s",true,true),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false));
    }

    private HBox generateUser(String username){
        HBox box = new HBox(10);
        box.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color : rgba(0, 0, 0, 0.15);");
        box.setPrefHeight(50);
        box.setPrefWidth(212);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER_LEFT);
        Image image = loadImage(username);
        Circle circle = new Circle(65,new ImagePattern(image));
        circle.setRadius(30);
        Label label = new Label(username);
        box.getChildren().addAll(circle,label);
        return box;
    }

    private Image loadImage(String username) {
        try {
            return connector.userImage(username);
        } catch (Exception e) {
            return new Image(userPhoto);
        }
    }

    private class Messenger{

    }
}
