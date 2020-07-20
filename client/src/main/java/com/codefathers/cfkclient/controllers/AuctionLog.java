package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.dtos.auction.AuctionLogDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.Random;

public class AuctionLog {
    public Label expression;
    public Label user;
    public Label price;
    public HBox root;

    private static final String[] EXPRESSIONS = {"WOW", "Ridiculous", "Great", "Weird", "Shit", "Nooooo", "Really?"};
    private AuctionLogDTO dto;

    @FXML
    public void initialize(){
    }

    private String generateExpression() {
        return EXPRESSIONS[new Random().nextInt(EXPRESSIONS.length)];
    }

    public HBox createLog(String username, String price) throws IOException {
        FXMLLoader loader = CFK.getFXMLLoader("auction_log");
        root = loader.load();
        AuctionLog controller = loader.getController();
        controller.init(username, price);
        return root;
    }

    private void init(String username, String price) {
        this.expression.setText(generateExpression());
        this.user.setText(username);
        this.price.setText(price);

        dto = createDTO(expression.getText(), username, price);
    }

    private AuctionLogDTO createDTO(String expression, String username, String price) {
        return new AuctionLogDTO(expression, username, price);
    }

    public AuctionLogDTO getAuctionLogDto() {
        return dto;
    }
}
