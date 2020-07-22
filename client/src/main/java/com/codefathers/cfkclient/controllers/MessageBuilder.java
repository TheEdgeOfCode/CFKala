package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

import static javafx.geometry.Pos.*;

public class MessageBuilder {
    public HBox root;
    public VBox box;
    public Label sender;
    public TextFlow textContainer;
    public Text text;

    public static Parent build(String text,String sender,boolean you,boolean needHeader){
        FXMLLoader loader = CFK.getFXMLLoader("message");
        try {
            Parent parent = loader.load();
            MessageBuilder controller = loader.getController();
            controller.init(text,
                    you? "You":capitalize(sender),
                    you? "#686de0":"#e056fd",
                    you? CENTER_RIGHT:CENTER_LEFT,
                    needHeader);
            return parent;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void initialize(){}

    private void init(String text, String sender, String color, Pos position, boolean needHeader){
        root.setAlignment(position);
        box.setAlignment(position);
        this.text.setText(text);
        this.textContainer.setStyle("-fx-background-color:" + color +  ";" +
                "-fx-background-radius: 10;");
        this.sender.setText(sender);
        if (!needHeader){
            box.getChildren().removeAll(this.sender);
        }
    }

    private static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
