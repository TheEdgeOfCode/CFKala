package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.File;

public class NewProduct {
    @FXML
    private ComboBox category;
    @FXML private JFXTextField productName;
    @FXML private JFXTextField price;
    @FXML private JFXTextField stock;
    @FXML private JFXTextField company;
    @FXML private JFXTextArea description;
    @FXML private TableView table;
    @FXML private TableColumn Features;
    @FXML private TableColumn TextFields;
    @FXML private JFXButton createProduct;
    @FXML private JFXButton back;
    @FXML private Button view;
    @FXML private Button sellThis;
    @FXML private VBox sellThisBox;
    @FXML private ListView similarProduct;
    @FXML private ListView<File> pictureList;
    @FXML private JFXButton pickPic;
    @FXML private JFXButton reset;
}
