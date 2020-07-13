package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class Cart {
    @FXML private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private Label totalPrice;
    @FXML private JFXButton purchase;
    @FXML private TableView tableView;
    @FXML private TableColumn product;
    @FXML private TableColumn amount;
    @FXML private TableColumn price;
    @FXML private TableColumn afterOff;
    @FXML private TableColumn totalCol;
    @FXML private Button decrease;
    @FXML private Button increase;
    @FXML private Button delete;
    @FXML private Button goToProduct;
}
