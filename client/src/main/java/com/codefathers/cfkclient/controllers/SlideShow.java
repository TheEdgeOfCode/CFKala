package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

import static com.codefathers.cfkclient.CFK.getFXMLLoader;
import static com.codefathers.cfkclient.CFK.makeFade;

public class SlideShow {
    @FXML
    private AnchorPane container;
    @FXML private JFXButton previous;
    @FXML private JFXButton next;
    @FXML private Label noContent;

    private List<Node> nodeList;
    private int currentIndex = 0;
    private int total = 0;
    private Timeline timeline;

    public static Parent makeSlideShow(List<Node> nodes) {
        FXMLLoader loader = getFXMLLoader("SlideShow");
        try {
            Parent parent = loader.load();
            SlideShow controller = loader.getController();
            controller.initData(nodes);
            return parent;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    private void initialize() {
        buttons();
        timeline();
    }

    private void timeline() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> showNext()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void buttons() {
        next.setOnAction(event -> showNext());
        previous.setOnAction(event -> showPrevious());
    }

    private void showNext() {
        if (total == -1) return;
        if (currentIndex < total) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        show(nodeList.get(currentIndex));
    }

    private void showPrevious() {
        if (total == -1) return;
        if (currentIndex == 0) {
            currentIndex = total;
        } else {
            currentIndex--;
        }
        show(nodeList.get(currentIndex));
    }

    private void show(Node node) {
        try {
            Node current = container.getChildren().get(0);
            FadeTransition fadeOut = makeFade(current, 1, 0, 200);
            fadeOut.setOnFinished(event -> {
                container.getChildren().clear();
                FadeTransition fadeIn = makeFade(node, 0, 1, 200);
                container.getChildren().add(node);
                fadeIn.play();
            });
            fadeOut.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initData(List<Node> list) {
        nodeList = list;
        currentIndex = 0;
        total = list.size() - 1;
        if (total != -1) {
            container.getChildren().clear();
            container.getChildren().add(list.get(0));
        }
        if (!list.isEmpty()) {
            noContent.setVisible(false);
        }
        if (list.size() == 1 | list.size() == 0) {
            timeline.stop();
        }
    }
}
