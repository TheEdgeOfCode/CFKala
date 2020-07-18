package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.product.CommentPM;
import com.codefathers.cfkclient.dtos.product.FullProductPM;
import com.codefathers.cfkclient.dtos.product.MiniProductDto;
import com.codefathers.cfkclient.dtos.product.SellPackageDto;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductDigest extends BackAbleController {
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
    @FXML private ComboBox<SellPackageDto> sellerBox;
    @FXML private Button addToCart;
    @FXML private Label price;
    @FXML private Label offPrice;
    @FXML private Button compare;
    @FXML private TableView<Map.Entry<String, String>> features;
    @FXML private VBox commentVBox;
    @FXML private JFXButton addComment;
    @FXML private ImageView specialOffer;
    @FXML private ImageView notAvailableSign;


    private int id;
    private static final CacheData cacheData = CacheData.getInstance();
    private static Connector connector = Connector.getInstance();
    private ArrayList<Image> images;
    private FullProductPM fullProductPM;

    @FXML
    public void initialize() {
        id = CacheData.getInstance().getProductId();
        listeners();
        loadProduct();
        buttonInit();
        commentSection();
        binds();
    }

    private void binds() {
        cartButt.disableProperty().bind(cacheData.roleProperty.isEqualTo("Customer").not());

    }

    private void buttonInit() {
        upBarButtons();
        photoButtons();
        productButtons();
        commentButton();
        //todo : videoSection();
    }

    /*
    private void videoSection() {
        boolean hasVideo = SellerController.getInstance().hasVideo(id);
        video.setDisable(!hasVideo);
        video.setOnMouseClicked(event -> VideoPlayer.playVideoFor(id));
    }*/

    private void commentButton() {
        addComment.setOnAction(event -> handleCreateComment());
    }

    private void handleCreateComment() {
        try {
            String[] comment = new CommentGetter().returnAComment();
            String[] info = {cacheData.getUsername(), comment[0], comment[1], "" + id};
            connector.assignComment(info);
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void productButtons() {
        compare.setOnAction(event -> gotoCompare());
        addToCart.setOnAction(event -> handleAdd());
    }

    private void handleAdd() {
        String role = cacheData.getRole();
        String sellerId = sellerBox.getSelectionModel().getSelectedItem().getSellerUsername();
        if (role.equalsIgnoreCase("customer")) {
            String[] info = {cacheData.getUsername(), "" + id, sellerId, "1"};
            try {
                connector.addToCart(info);
                Notification.show("Successful", "Item Was Added To Your Cart!!!", back.getScene().getWindow(), false);
            } catch (Exception e) {
                Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
                e.printStackTrace();
            }
        } else {
            Notification.show("Error", "You must be A Customer To Buy", back.getScene().getWindow(), true);
        }
    }

    private void gotoCompare() {
        try {
            Scene scene = new Scene(CFK.loadFXML("ComparePage", backForForward("ProductDigest")));
            CFK.setSceneToStage(back,scene);
        } catch (IOException e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void photoButtons() {
        firstPhoto.setOnAction(event -> goToFirstPhoto());
        lastPhoto.setOnAction(event -> goToLastPhoto());
        nextPhoto.setOnAction(event -> gotoNextPhoto());
        prePhoto.setOnAction(event -> gotoPrePhoto());
    }

    private void gotoPrePhoto() {
        Image current = mainImage.getImage();
        int currentIndex = images.indexOf(current);
        if (currentIndex > 0) {
            mainImage.setImage(images.get(currentIndex - 1));
        }
    }

    private void gotoNextPhoto() {
        Image current = mainImage.getImage();
        int currentIndex = images.indexOf(current);
        int lastIndex = images.size() - 1;
        if (currentIndex < lastIndex) {
            mainImage.setImage(images.get(currentIndex + 1));
        }
    }

    private void goToLastPhoto() {
        mainImage.setImage(images.get(images.size() - 1));
    }

    private void goToFirstPhoto() {
        mainImage.setImage(images.get(0));
    }

    private void upBarButtons() {
        back.setOnAction(event -> backHandle());
        close.setOnAction(event -> handleClose());
        minimize.setOnAction(event -> handleMinimize());
        cartButt.setOnAction(event -> gotoCart());
    }

    private void handleClose() {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }

    private void handleMinimize() {
        Stage stage = (Stage) close.getScene().getWindow();
        stage.setIconified(true);
    }

    private void gotoCart() {
        try {
            Scene scene = new Scene(CFK.loadFXML("Cart", backForForward("ProductDigest")));
            CFK.setSceneToStage(new Stage(StageStyle.UNDECORATED), scene);
        } catch (IOException e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void backHandle() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
        } catch (IOException e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void commentSection() {
        try {
            List<CommentPM> comments = connector.viewProductComments(id);
            loadComments(comments);
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void loadComments(List<CommentPM> comments) {
        for (CommentPM comment : comments) {
            Parent parent = loadComment(comment);
            if (parent != null) {
                commentVBox.getChildren().add(parent);
            }
        }
    }

    private Parent loadComment(CommentPM comment) {
        try {
            return Comment.generateComment(comment);
        } catch (IOException e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
            return null;
        }
    }

    private void listeners() {
        sellerBox.getSelectionModel().selectedItemProperty().addListener((v, oldValue, newValue) -> loadPricing(newValue));
    }

    private void loadPricing(SellPackageDto pm) {
        if (pm.getOffPercent() != 0) {
            double price = pm.getPrice() * (100 - pm.getOffPercent()) / 100;
            this.price.setText("" + price + "$");
            offPrice.setText("RealPrice : " + pm.getPrice() + "$");
            offPrice.setVisible(true);
            specialOffer.setVisible(true);
        } else {
            price.setText("" + pm.getPrice());
            offPrice.setVisible(false);
            specialOffer.setVisible(false);
        }
        if (pm.isAvailable()) {
            notAvailableSign.setVisible(false);
        } else {
            notAvailableSign.setVisible(true);
        }
    }

    private void loadProduct() {
        try {
            FullProductPM pm = connector.viewAttributes(id);
            connector.addViewDigest(id);
            fullProductPM = pm;
            loadInformation(pm.getProduct());
            loadImage();
            initialFeatures(pm.getFeatures());
        } catch (Exception e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void initialFeatures(Map<String, String> features) {
        TableColumn<Map.Entry<String, String>, String> featureCol = new TableColumn<>("Features");
        featureCol.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getKey()));
        TableColumn<Map.Entry<String, String>, String> valueCol = new TableColumn<>("Features");
        valueCol.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getValue()));
        featureCol.setMinWidth(288);
        valueCol.setMinWidth(288);
        ObservableList<Map.Entry<String, String>> data = FXCollections.observableArrayList(features.entrySet());
        this.features.setItems(data);
        this.features.getColumns().setAll(featureCol, valueCol);
    }

    private void loadImage() {
        try {
            images = connector.loadImages(id);
            mainImage.setImage(images.get(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadInformation(MiniProductDto product) {
        productName.setText(product.getName());
        company.setText(product.getBrand());
        initStars(product.getScore());
        description.setText(product.getDescription());
        loadSellers(product.getSellPackages());
    }

    private void loadSellers(List<SellPackageDto> pms) {
        ObservableList<SellPackageDto> data = FXCollections.observableArrayList(pms);
        sellerBox.setItems(data);
        sellerBox.getSelectionModel().selectFirst();
    }

    private void initStars(double score) {
        if (score < 1) {
            star1.setIconLiteral("fa-star-o");
        }
        if (score < 2) {
            star2.setIconLiteral("fa-star-o");
        }
        if (score < 3) {
            star3.setIconLiteral("fa-star-o");
        }
        if (score < 4) {
            star4.setIconLiteral("fa-star-o");
        }
        if (score < 5) {
            star5.setIconLiteral("fa-star-o");
        }
    }
}
