package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class OrderHistory {
    @FXML
    private JFXButton back;
    @FXML private JFXButton cartButt;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;

    @FXML private Button viewProduct;
    @FXML private Button giveScore;
    @FXML private TableView orderTable;
    @FXML private TableColumn orderNoColumn;
    @FXML private TableColumn dateColumn;

    @FXML private Label no;
    @FXML private Label date;
    @FXML private Label price;
    @FXML private Label discount;
    @FXML private Label delStatus;

    @FXML private TableView productsTable;
    @FXML private TableColumn pNameCol;
    @FXML private TableColumn pSellerCol;
    @FXML private TableColumn pPriceCol;
    
}
