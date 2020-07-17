package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.dtos.content.MainContent;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import static com.codefathers.cfkclient.controllers.Notification.show;

public class ContentManager extends BackAbleController {
    @FXML private JFXButton back;
    @FXML private JFXButton refresh;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private JFXTextField crTitle;
    @FXML private JFXTextArea crContent;
    @FXML private JFXButton submit;
    @FXML private Label title;
    @FXML private Text content;
    @FXML private JFXButton remove;
    @FXML private ListView<MainContent> contentList;

    private static Connector  connector = Connector.getInstance();

    @FXML
    public void initialize() {
        load();
        buttons();
        binds();
        listeners();
    }

    private void listeners() {
        contentList.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                loadContent(newValue);
            }
        });
    }

    private void loadContent(MainContent newValue) {
        title.setText(newValue.getTitle());
        content.setText(newValue.getContent());
    }

    private void binds() {
        remove.disableProperty().bind(Bindings.isEmpty(contentList.getSelectionModel().getSelectedItems()));
        submit.disableProperty().bind(crTitle.textProperty().isEmpty().or(crContent.textProperty().isEmpty()));
    }

    private void load() {
        try {
            ObservableList<MainContent> data = FXCollections.observableArrayList(connector.mainContents());
            contentList.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void buttons() {
        close.setOnAction(event -> ((Stage) close.getScene().getWindow()).close());
        minimize.setOnAction(event -> ((Stage) close.getScene().getWindow()).setIconified(true));
        back.setOnAction(event -> handleBack());
        submit.setOnAction(event -> handleSubmit());
        remove.setOnAction(event -> handleDelete());
        refresh.setOnAction(event -> {
            contentList.getItems().clear();
            load();
        });
    }

    private void handleDelete() {
        MainContent item = contentList.getSelectionModel().getSelectedItem();
        try {
            connector.deleteContent(
                    item.getId()
            );
            show("Successful", "Done!",
                    close.getScene().getWindow(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleSubmit() {
        try {
            connector.addContent(crTitle.getText(), crContent.getText());
            show("Successful", "Done! Reload To See It",
                    close.getScene().getWindow(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
