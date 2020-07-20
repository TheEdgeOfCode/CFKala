package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.auction.AuctionDTO;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.Data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Random;
import java.util.function.Consumer;

public class AuctionPage extends BackAbleController {
    public JFXButton back;
    public JFXButton minimize;
    public JFXButton close;

    public JFXTextArea messageArea;
    public VBox messageContainer;
    public JFXButton send;

    public JFXTextField priceEntry;
    public JFXButton applyChange;
    public JFXButton increaseBy100;
    public JFXButton increaseBy1000;

    public ImageView productImage;
    public Label productName;
    public Label firstPrice;
    public Label currentPrice;
    public Label yourPrice;
    public Label mostPriceUser;

    public VBox logContainer;

    private static String[] TEST_USERS = {"Ali", "Hatam", "Marmof", "Sapa", "You"};
    private static final String PRODUCT_PHOTO = "/Images/product.png";
    private Connector connector = Connector.getInstance();
    private CacheData cacheData = CacheData.getInstance();
    private AuctionDTO auctionDTO = cacheData.getAuctionDTO();

    private static int PORT = 9090;
    private static String IP = "127.0.0.1";
    private AuctionClient auctionClient = createClient();

    private AuctionClient createClient() {
        return new AuctionClient(PORT, IP, data -> Platform.runLater(() ->{
            //TODO: Implement!!!
        }));
    }

    @FXML
    public void initialize(){
        initButts();
        initLabels();
        auctionClient.startConnection();
        //initMessages();
    }

    private void initButts() {
        minimize.setOnAction(e -> {
            Stage stage = (Stage) minimize.getScene().getWindow();
            stage.setIconified(true);
        });
        close.setOnAction(e -> {
            Stage stage = (Stage) close.getScene().getWindow();
            stage.close();
            closeConnection();
        });
        back.setOnAction(event -> handleBack());
        send.setOnAction(event -> handleSend());
        applyChange.setOnAction(event -> handleApply());
        applyChange.setTooltip(new Tooltip("Apply Price"));
        increaseBy100.setOnAction(event -> handelIncrease(100));
        increaseBy1000.setOnAction(event -> handelIncrease(1000));
    }

    private void handleBack() {
        try {
            Scene scene = new Scene(CFK.loadFXML(back(), backForBackward()));
            CFK.setSceneToStage(back, scene);
            closeConnection();
        } catch (IOException e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void closeConnection() {
        try {
            auctionClient.closeConnection();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void handelIncrease(int amount) {
        long previous = Long.parseLong(currentPrice.getText().substring(0, currentPrice.getText().length() - 1));
        String finalPrice = (amount + previous) + "$";
        try {
            //connector.addPriceAuction(finalPrice);
            logContainer.getChildren().add(new AuctionLog().createLog(cacheData.getUsername(), finalPrice));
            currentPrice.setText(finalPrice);
        } catch (IOException e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
    }

    private void handleApply() {
        if (checkInput()) {
            try {
                //connector.suggestPriceAuction(finalPrice);
                logContainer.getChildren().add(new AuctionLog().createLog(cacheData.getUsername(), priceEntry.getText()));
            } catch (IOException e) {
                Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
                e.printStackTrace();
            }
            currentPrice.setText(priceEntry.getText() + "$");
            priceEntry.clear();
        }
    }

    private boolean checkInput() {
        if (priceEntry.getText().isEmpty()){
            Notification.show("Error", "Please Enter a Number", back.getScene().getWindow(), true);
            return false;
        } else if (!priceEntry.getText().matches("\\d+")){
            Notification.show("Error", "Should be Numeric", back.getScene().getWindow(), true);
            return false;
        } else if (Long.parseLong(priceEntry.getText()) < Long.parseLong(currentPrice.getText())){
            Notification.show("Error", "Should be More Than Current Price", back.getScene().getWindow(), true);
            return false;
        }
        return true;
    }

    private String getRandomUser() {
        return TEST_USERS[new Random().nextInt(TEST_USERS.length)];
    }

    private void handleSend() {
        try {
            String message = messageArea.getText();
            messageContainer.getChildren().add(new AuctionMessage().createMessage(cacheData.getUsername(), message));
            //connector.sendAuctionMessage(message);
        } catch (IOException e) {
            Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
            e.printStackTrace();
        }
        messageArea.clear();
    }

    private void initLabels() {
        try {
            productImage.setImage(connector.productMainImage(auctionDTO.getProductId()));
            productName.setText(auctionDTO.getProductName());
            firstPrice.setText(String.valueOf(auctionDTO.getSellPackageDto().getPrice()));
            currentPrice.setText(String.valueOf(auctionDTO.getCurrentPrice()));
            yourPrice.setText(String.valueOf(auctionDTO.getUserPrice()));
            mostPriceUser.setText(auctionDTO.getMostPriceUser());
        } catch (Exception e) {
            productImage.setImage(new Image(PRODUCT_PHOTO));
            e.printStackTrace();
        }

    }

    private void initMessages() {
        //TODO: Get Previously Sent Messages!!!
    }
}

@Data
class AuctionClient {
    private Consumer<Serializable> onReceiveCallBack;
    private AuctionThread auctionThread = new AuctionThread();
    private int port;
    private String ip;

    public AuctionClient(int port, String ip, Consumer<Serializable> onReceiveCallBack){
        this.onReceiveCallBack = onReceiveCallBack;
        this.port = port;
        this.ip = ip;
        this.auctionThread.setDaemon(true);
    }

    public void startConnection(){
        auctionThread.start();
    }

    public void send(Serializable data) throws IOException {
        auctionThread.out.writeObject(data);
    }

    public void closeConnection() throws IOException {
        auctionThread.socket.close();
    }

    private class AuctionThread extends Thread{
        private ObjectOutputStream out;
        private Socket socket;

        @Override
        public void run() {
            try(Socket socket = new Socket(ip, port);
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                this.out = out;
                this.socket = socket;
                socket.setTcpNoDelay(true);

                while (true){
                    Serializable data = (Serializable) in.readObject();
                    onReceiveCallBack.accept(data);
                }

            } catch(Exception e){
                onReceiveCallBack.accept("Connection Lost");
            }
        }
    }
}
