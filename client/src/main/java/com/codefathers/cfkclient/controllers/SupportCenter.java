package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.support.Message;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.codefathers.cfkclient.controllers.Notification.show;

public class SupportCenter {
    @FXML private ScrollPane scroll;
    @FXML private Label label;
    @FXML private JFXButton back;
    
    @FXML private Pane supportPane;
    @FXML private VBox availableSupports;
    
    @FXML private VBox chatBox;
    @FXML private VBox messageBox;
    @FXML private TextField text;
    @FXML private JFXButton send;

    private String username;
    private StringProperty supportName = new SimpleStringProperty("");
    private Messenger messenger;
    private Thread messengerThread;
    private byte pastSender = 1;

    private static CacheData cacheData = CacheData.getInstance();
    private static Connector connector = Connector.getInstance();
    private static final String userPhoto = "/Images/user-png-icon-male-user-icon-512.png";


    @FXML
    private void initialize(){
        initUsername();
        initMessenger();
        initChatBox();
        binds();
        listeners();
        initOnlineSupports();
    }

    private void listeners() {

    }

    private void binds() {
        back.visibleProperty().bind(chatBox.visibleProperty());
        send.disableProperty().bind(text.textProperty().isEmpty());
    }

    private void initChatBox() {
        back.setOnAction(event -> closeChat());
        text.setOnAction(event -> {
            if (!text.getText().isEmpty())
                sendMessage();
        });
        send.setOnAction(event -> sendMessage());
    }

    private void sendMessage() {
        Message message = new Message(username,text.getText());
        messenger.send(message);
        messageBox.getChildren().add(MessageBuilder.build(message.getMessage(),message.getSender(),
                true,pastSender!=0));
        text.clear();
        pastSender = 0;
    }

    private void initOnlineSupports() {
        try {
            ArrayList<String> allSupports = connector.getAllSupports();
            for (String support : allSupports) {
                availableSupports.getChildren().add(generateSupportItem(support));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initMessenger() {
        messenger = new Messenger();
        messengerThread = new Thread(messenger);
        messengerThread.start();
    }

    private void initUsername() {
        String username = cacheData.getUsername();
        if (username.isEmpty()){
            try {
                this.username = connector.getGuestToken();
                System.out.println(this.username);
            } catch (Exception e) {
                new OopsAlert().show(e.getMessage());
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }else {
            this.username = username;
        }
    }

    private void receiveMessage(Message message){
        messageBox.getChildren().add(MessageBuilder.build(message.getMessage(),message.getSender(),
                false,pastSender==0));
        pastSender = 1;
    }

    private HBox generateSupportItem(String username){
        HBox box = new HBox(10);
        box.setStyle("-fx-background-radius: 10;" +
                "-fx-background-color : rgba(0, 0, 0, 0.15);");
        box.setPrefHeight(50);
        box.setPrefWidth(212);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER_LEFT);
        Image image = loadImage(username);
        Circle circle = new Circle(65,new ImagePattern(image));
        circle.setRadius(30);
        Label label = new Label(username);
        label.setFont(Font.font("System", FontWeight.BOLD,13));
        box.getChildren().addAll(circle,label);
        box.setOnMouseClicked(event -> {
            supportName.setValue(username);
            openChat();
        });
        return box;
    }

    private void openChat() {
        messageBox.getChildren().clear();
        supportPane.setVisible(false);
        chatBox.setVisible(true);
        label.setText(supportName.getValue());
    }

    private void closeChat(){
        availableSupports.getChildren().clear();
        supportPane.setVisible(true);
        chatBox.setVisible(false);
        initOnlineSupports();
    }

    private Image loadImage(String username) {
        try {
            Image image = connector.userImage(username);
            if (image != null) {
                return image;
            }else throw new Exception();
        } catch (Exception e) {
            return new Image(userPhoto);
        }
    }

    private void exceptionHandler(Throwable exception) {
        List<Window> stages = Stage.getWindows().filtered(Window::isFocused);
        show("Error", exception.getMessage(), stages.get(0), true);
    }

    private class Messenger implements Runnable{
        CFStomp stomp;
        private String url;

        {
            stomp = new Messenger.CFStomp();
            url = "ws://2.tcp.ngrok.io:13766/chat";
        }

        @Override
        public void run() {
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());
            StompSessionHandler handler = stomp;
            stompClient.connect(url,handler);
        }

        void send(Message message){
            stomp.send(message);
        }

        public class CFStomp extends StompSessionHandlerAdapter {
            StompSession stompSession;


            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Message.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                Message message = (Message)payload;
                receiveMessage(message);
            }

            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                System.out.println("New session established : " + session.getSessionId());
                session.subscribe("/topic/messages/" + username,this);
                System.out.println("Subscribed");
                stompSession = session;
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                exceptionHandler(exception);
            }

            void send(Message message){
                stompSession.send("/app/chat/" + supportName.getValue(),message);
                System.out.println("Message sent tp ws server");
            }

            void stop(){
                stompSession.disconnect();
            }
        }
    }
}
