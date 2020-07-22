package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.auction.CreateAuctionDTO;
import com.codefathers.cfkclient.dtos.product.MicroProductDto;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

public class CreateAuction extends BackAbleController {
    public JFXButton back;
    public JFXButton minimize;
    public JFXButton close;

    public DatePicker startDate;
    public DatePicker endDate;
    public JFXButton create;
    public ComboBox<MicroProductDto> products;

    private final Connector connector = Connector.getInstance();
    private final CacheData cacheData = CacheData.getInstance();

    @FXML
    public void initialize(){
        initCombo();
        initButts();
    }

    private void initButts() {
        back.setOnAction(event -> handleBackButt());
        minimize.setOnAction(event -> minimize());
        close.setOnAction(event -> close());
        create.setOnAction(event -> handleCreate());
    }

    private void handleCreate() {
        create.disableProperty().bind(
                products.valueProperty().isNull()
                .or(startDate.valueProperty().isNull()
                .or(endDate.valueProperty().isNull()))
        );
        if (convertToDateViaInstant(startDate.getValue()).after(convertToDateViaInstant(endDate.getValue()))) {
            Notification.show("Error", "Start Date Should Be Before End Date!", back.getScene().getWindow(), true);
        } else {
            int productId = products.getSelectionModel().getSelectedItem().getId();
            CreateAuctionDTO createAuctionDTO = getCreateAuctionDTO(productId);
            try {
                connector.createAuction(createAuctionDTO);
                Notification.show("Successful", "Auction Created Successfully!", back.getScene().getWindow(), false);
            } catch (Exception e) {
                Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            }
        }
    }

    private CreateAuctionDTO getCreateAuctionDTO(int productId) {
        return new CreateAuctionDTO(
            productId,
            convertToDateViaInstant(startDate.getValue()),
            convertToDateViaInstant(endDate.getValue())
        );
    }

    private java.util.Date convertToDateViaInstant(LocalDate dateToConvert) {
        return java.util.Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }


    private void close() {
        Stage window = (Stage) back.getScene().getWindow();
        window.close();
    }

    private void minimize() {
        Stage window = (Stage) back.getScene().getWindow();
        window.setIconified(true);
    }

    private void handleBackButt() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException ignore){}
    }

    private void initCombo() {
        try {
            products.getItems().setAll(connector.sellerMicroProduct(cacheData.getUsername()));
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), create.getScene().getWindow(), true);
        }
    }
}
