package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.dtos.content.MainContent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.util.Random;

import static com.codefathers.cfkclient.CFK.getFXMLLoader;
import static com.codefathers.cfkclient.controllers.Advertise.COLORS;

public class Content {
    @FXML private Label title;
    @FXML private AnchorPane root;
    @FXML private Text content;

    /**
     * @param content the {@Code MainContent} from Content Controller
     * @return a {@Code Parent} or
     * {@Code null} if  there is a problem loading FXML "Content"
     */
    public static Parent createContent(MainContent content) {
        FXMLLoader loader = getFXMLLoader("Content");
        try {
            Parent parent = loader.load();
            Content controller = loader.getController();
            controller.initData(content);
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

    private String getRandomColor() {
        return COLORS[new Random().nextInt(COLORS.length)];
    }

    private void initData(MainContent content) {
        this.title.setText(content.getTitle());
        this.content.setText(content.getContent());
    }
}
