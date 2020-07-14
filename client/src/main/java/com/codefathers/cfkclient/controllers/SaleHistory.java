package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class SaleHistory {
    @FXML
    private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private TableView saleTable;
    @FXML private TableColumn sellNoCol;
    @FXML private TableColumn sellDateCol;
    @FXML private Label totalSale;
    @FXML private VBox infoBox;
    @FXML private Label saleId;
    @FXML private Label productId;
    @FXML private JFXButton viewProduct;
    @FXML private Label date;
    @FXML private Label moneyGotten;
    @FXML private Label off;
    @FXML private Label buyer;
    @FXML private Label deliveryStatus;
}
