package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Map;

public class ProductDigest {
    @FXML
    private JFXButton back;
    @FXML private JFXButton cartButt;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private ImageView mainImage;
    @FXML private JFXButton video;
    @FXML private JFXButton nextPhoto;
    @FXML private JFXButton prePhoto;
    @FXML private JFXButton lastPhoto;
    @FXML private JFXButton firstPhoto;
    @FXML private Label productName;
    @FXML private Label company;
    @FXML private FontIcon star1;
    @FXML private FontIcon star2;
    @FXML private FontIcon star3;
    @FXML private FontIcon star4;
    @FXML private FontIcon star5;
    @FXML private Text description;
    @FXML private ComboBox sellerBox;
    @FXML private Button addToCart;
    @FXML private Label price;
    @FXML private Label offPrice;
    @FXML private Button compare;
    @FXML private TableView<Map.Entry<String, String>> features;
    @FXML private VBox commentVBox;
    @FXML private JFXButton addComment;
    @FXML private ImageView specialOffer;
    @FXML private ImageView notAvailableSign;
}
