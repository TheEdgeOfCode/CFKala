package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.user.MessagePM;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class Messages extends BackAbleController {
    @FXML
    private JFXButton back;
    @FXML private JFXButton cartButt;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private Label subject;
    @FXML private Label date;
    @FXML private Text message;
    @FXML private TableView<MessagePM> listTable;
    @FXML private TableColumn<MessagePM, String> subjectColumn;
    @FXML private TableColumn<MessagePM, Date> dateColumn;
    @FXML private TableColumn<MessagePM, Boolean> readColumn;

    private static CacheData cacheData = CacheData.getInstance();
    private static Connector connector = Connector.getInstance();

    @FXML
    public void initialize() {
        initButtons();
        loadTable();
        listeners();
        binds();
    }

    private void binds() {
        cartButt.disableProperty().bind(cacheData.roleProperty.isEqualTo("Customer").not());
    }

    private void listeners() {
        listTable.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> {
            if (newValue != null) {
                loadMessage(newValue);
            }
        });
    }

    private void loadMessage(MessagePM pm) {
        connector.openMessage(pm.getId());
        subject.setText(pm.getSubject());
        date.setText(pm.getDate().toString());
        message.setText(pm.getMessage());
    }

    private void loadTable() {
        try {
            ArrayList<MessagePM> list = connector.getMessagesForUser();
            ObservableList<MessagePM> data = FXCollections.observableArrayList(list);
            subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            readColumn.setCellValueFactory(new PropertyValueFactory<>("isRead"));
            listTable.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initButtons() {
        back.setOnAction(e -> handleBackButton());
        minimize.setOnAction(e -> minimize());
        close.setOnAction(e -> close());
        cartButt.setOnAction(e -> handleCartButt());
    }

    private void handleCartButt() {
        try {
            Scene scene = new Scene(CFK.loadFXML("Cart", backForForward("Messages")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException ignore) {}
    }

    private void close() {
        Stage window = (Stage) back.getScene().getWindow();
        window.close();
    }

    private void minimize() {
        Stage window = (Stage) back.getScene().getWindow();
        window.setIconified(true);
    }

    private void handleBackButton() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException ignore) {}
    }
}
