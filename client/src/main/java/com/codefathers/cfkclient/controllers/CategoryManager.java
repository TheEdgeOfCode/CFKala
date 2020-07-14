package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class CategoryManager {
    @FXML private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private TextField categoryName;
    @FXML private JFXButton editName;
    @FXML private JFXButton deleteCategory;
    @FXML private ListView featureList;
    @FXML private TextField newFeature;
    @FXML private Button newFeatureButt;
    @FXML private Button removeFeatureButt;
    @FXML private JFXTextField crName;
    @FXML private ComboBox crParent;
    @FXML private ListView featureListCr;
    @FXML private TextField featureInput;
    @FXML private Button addFeatureCr;
    @FXML private JFXButton createCategory;
    @FXML private ListView categories;
}
