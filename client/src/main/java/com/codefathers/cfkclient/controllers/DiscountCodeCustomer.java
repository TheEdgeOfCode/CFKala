package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.discount.DisCodeUserDTO;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DiscountCodeCustomer extends BackAbleController {
    @FXML private JFXButton back;
    @FXML private JFXButton cartButt;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private TableView<DisCodeUserDTO> codesTable;
    @FXML private TableColumn<DisCodeUserDTO, String> codeCol;
    @FXML private TableColumn<DisCodeUserDTO, Integer> countCol;
    @FXML private Pane disCode;
    @FXML private Label percent;
    @FXML private Label code;
    @FXML private Label max;
    @FXML private Label date;

    private Connector connector = Connector.getInstance();
    private CacheData cacheData = CacheData.getInstance();

    @FXML
    public void initialize() {
        initButtons();
        initCodeTable();
        disCode.visibleProperty().bind(Bindings.isEmpty(codesTable.getSelectionModel().getSelectedItems()).not());
    }

    private void initCodeTable() {
        codeCol.setCellValueFactory(new PropertyValueFactory<>("discountCode"));
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));

        codesTable.setItems(getDisCodes());

        //codesTable.setItems(getTestDisCodes());

        codesTable.getSelectionModel().selectedItemProperty().addListener((v, oldCode, newCode) -> updateCoupon(newCode));
    }

    private ObservableList<DisCodeUserDTO> getDisCodes() {
        ObservableList<DisCodeUserDTO> discodes = FXCollections.observableArrayList();

        try {
            discodes.addAll(connector.showDiscountCodes());
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }

        return discodes;
    }

    private ObservableList<DisCodeUserDTO> getTestDisCodes() {
        ObservableList<DisCodeUserDTO> discodes = FXCollections.observableArrayList();

        discodes.add(new DisCodeUserDTO("new_year", new Date(), new Date(1321354), 50, 100, 20));
        discodes.add(new DisCodeUserDTO("welcome_to_school99", new Date(3232), new Date(211), 98, 250, 3));
        discodes.add(new DisCodeUserDTO("covid19", new Date(4984), new Date(564), 10, 50, 10));
        discodes.add(new DisCodeUserDTO("hay_day2020", new Date(), new Date(321354), 80, 10, 2));

        return discodes;
    }

    private void updateCoupon(DisCodeUserDTO newCode) {
        code.setText("\"" + newCode.getDiscountCode() + "\"");
        percent.setText(String.valueOf(newCode.getOffPercentage()));
        max.setText(newCode.getMaxOfPriceDiscounted() + "$");
        date.setText(new SimpleDateFormat("EEE, d MMM yyyy").format(newCode.getEndTime()));
    }

    private void initButtons() {
        minimize.setOnAction(e -> {
            Stage stage = (Stage) minimize.getScene().getWindow();
            stage.setIconified(true);
        });
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
        });
        back.setOnAction(event -> handleBack());
        cartButt.setOnAction(event -> handleCart());
    }

    private void handleCart() {
        try {
            Scene scene = new Scene(CFK.loadFXML("Cart", backForForward("DiscountCodeCustomer")));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
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
}
