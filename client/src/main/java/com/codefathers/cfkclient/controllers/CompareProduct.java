package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class CompareProduct {
    @FXML private JFXButton back;
    @FXML private JFXButton cartButt;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private ImageView mainImage;
    @FXML private Rectangle secondProductImage;
    @FXML private JFXComboBox choose;
    @FXML private TableView table;
    @FXML private TableColumn feature;
    @FXML private TableColumn product1Col;
    @FXML private TableColumn product2Col;
}
