package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.shape.Rectangle;

public class EditProduct {
    @FXML
    private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private Rectangle imageViewer;
    @FXML private ListView imageList;
    @FXML private JFXButton addPicture;
    @FXML private JFXButton removePicture;
    @FXML private JFXButton submitPicChange;
    @FXML private TableView featureTable;
    @FXML private TableColumn featureCol;
    @FXML private TableColumn currentValCol;
    @FXML private TableColumn EdValCol;
    @FXML private TextField editVal;
    @FXML private JFXButton editFeatureBtn;
    @FXML private JFXButton resetTable;
    @FXML private JFXButton submitFeatures;
    @FXML private TextField name;
    @FXML private ChoiceBox colorBox;
    @FXML private TextField weigh;
    @FXML private TextField dimension;
    @FXML private TextField price;
    @FXML private TextField stock;
    @FXML private JFXButton submitMain;


}
