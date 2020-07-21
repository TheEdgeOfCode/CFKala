package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.SoundCenter;
import com.codefathers.cfkclient.dtos.content.AdPM;
import com.codefathers.cfkclient.dtos.content.MainContent;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkclient.Sound.CLICK;
import static com.codefathers.cfkclient.Sound.POP_UP;
import static com.codefathers.cfkclient.controllers.Notification.show;

public class MainPage extends BackAbleController {
    @FXML
    private JFXButton support;
    @FXML
    private JFXButton minimize;
    @FXML private JFXButton close;
    @FXML private JFXButton account;
    @FXML private JFXButton cart;
    @FXML private JFXButton products;
    @FXML private VBox mainBox;

    private PopOver accountPopOver;
    private PopOver supportPopOver;
    private static CacheData cacheData = CacheData.getInstance();
    private static Connector connector = Connector.getInstance();

    @FXML
    public void initialize(){
        buttons();
        popOver();
        binds();
        listeners();
        slider();
        advertising();
        menus();
    }

    private void menus() {
        products.setOnAction(event -> gotoProducts());
    }

    private void gotoProducts() {
        try {
            Scene scene = new Scene(CFK.loadFXML("ProductsPage", "MainPage"));
            CFK.setSceneToStage(products, scene);
            SoundCenter.play(CLICK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void advertising() {
        List<Node> nodes = new ArrayList<>();
        List<AdPM> pms = null;
        try {
            pms = connector.getAds();
            pms.forEach(pm -> {
                Parent parent = Advertise.createAdvertise(pm);
                if (parent != null) {
                    nodes.add(parent);
                }
            });
            Parent slideshow = SlideShow.makeSlideShow(nodes);
            mainBox.getChildren().add(slideshow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void slider() {
        List<Node> nodes = new ArrayList<>();
        try {
            List<MainContent> contents = connector.mainContents();
            contents.forEach(content -> {
                Parent parent = Content.createContent(content);
                if (parent != null) {
                    nodes.add(parent);
                }
            });
            Parent slideshow = SlideShow.makeSlideShow(nodes);
            mainBox.getChildren().add(slideshow);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void listeners() {
        cacheData.roleProperty.addListener((v, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty()) {
                    accountPopOver.setContentNode(CFK.loadFXML("accountManagerPopUp"));
                } else {
                    accountPopOver.setContentNode(CFK.loadFXML("accountPopUp"));
                }
            } catch (IOException e) {
                show("Error", e.getMessage(), close.getScene().getWindow(), true);
                e.printStackTrace();
            }
        });
    }

    private void popOver() {
        accountPop();
        supportPop();
    }

    private void supportPop() {
        String fxml = "SupportPopup";

        try {
            supportPopOver = new PopOver(CFK.loadFXML(fxml));
            supportPopOver.setTitle("Support");
            supportPopOver.setArrowLocation(PopOver.ArrowLocation.RIGHT_BOTTOM);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void accountPop() {
        String fxml;
        if (CacheData.getInstance().getRole().isEmpty()) {
            fxml = "accountPopUp";
        } else {
            fxml = "accountManagerPopUp";
        }

        try {
            accountPopOver = new PopOver(CFK.loadFXML(fxml));
            accountPopOver.setTitle("Account");
            accountPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void binds() {
        cart.disableProperty().bind(cacheData.roleProperty.isEqualTo("Customer").not());
    }

    private void buttons() {
        close.setOnAction(event -> ((Stage) close.getScene().getWindow()).close());
        minimize.setOnAction(event -> ((Stage) close.getScene().getWindow()).setIconified(true));
        cart.setOnAction(event -> gotoCart());
        account.setOnAction(event -> accountManager());
        support.setOnAction(event -> supportManager());
    }

    private void supportManager() {
        if (supportPopOver.isShowing()) {
            supportPopOver.hide();
        } else {
            SoundCenter.play(POP_UP);
            supportPopOver.show(support);
        }
    }

    private void accountManager() {
        if (accountPopOver.isShowing()) {
            accountPopOver.hide();
        } else {
            SoundCenter.play(POP_UP);
            accountPopOver.show(account);
        }
    }

    private void gotoCart() {
        try {
            Scene scene = new Scene(CFK.loadFXML("Cart", "MainPage"));
            CFK.setSceneToStage(cart, scene);
        } catch (IOException e) {
            show("Error", e.getMessage(), cart.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }
}
