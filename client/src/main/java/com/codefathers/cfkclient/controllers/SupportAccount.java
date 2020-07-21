package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CFK;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.edit.UserEditAttributes;
import com.codefathers.cfkclient.dtos.support.Message;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Data;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class SupportAccount extends BackAbleController {
    @FXML private HBox chatsLoading;
    @FXML private HBox usersLoading;

    @FXML private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;

    @FXML private VBox settings;
    @FXML private Circle image;
    @FXML private JFXButton changeProfile;
    @FXML private Label username;
    @FXML private JFXButton changePass;
    
    @FXML private VBox chat;
    @FXML private VBox messageBox;
    @FXML private TextField text;
    @FXML private JFXButton send;

    @FXML private VBox chatBar;

    private static final String userPhoto = "/Images/user-png-icon-male-user-icon-512.png";
    private static Connector connector = Connector.getInstance();

    private Messenger messenger = new Messenger();

    private StringProperty currentChat = new SimpleStringProperty("");
    private HashMap<String,Chat> chatHashMap = new HashMap<>();

    @FXML
    private void initialize(){
        personalInfo();
        buttons();
        binds();
        listeners();
        startMessagingUnit();
    }

    private void listeners() {
        currentChat.addListener((o, oldValue,newValue) -> {
            // TODO: 7/21/2020
        });
    }

    private void startMessagingUnit() {
        sendOnline();
        new Thread(messenger).start();
        text.setOnAction(event -> {
            if (!text.getText().isEmpty())sendMessage();
        });
    }

    private void sendOnline() {
        try {
            connector.setThisSupportOnline();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void binds() {
        send.disableProperty().bind(text.textProperty().isEmpty());
        chat.disableProperty().bind(currentChat.isEmpty());
    }

    private void personalInfo() {
        String username = CacheData.getInstance().getUsername();
        this.username.setText(username);
        Image image = loadImage(username);
        this.image.setFill(new ImagePattern(image));
    }

    private void buttons() {
        close.setOnAction(event -> handleClose());
        minimize.setOnAction(event -> ((Stage)back.getScene().getWindow()).setIconified(true));
        back.setOnAction(event -> logoutHandle());
        changeProfile.setOnAction(event -> handleChangeProfile());
        changePass.setOnAction(event -> handleChangePass());
        send.setOnAction(event -> sendMessage());
        text.setOnAction(event -> sendMessage());
    }

    private void sendMessage() {
        Message message = new Message(username.getText(),text.getText());
        String receiver = currentChat.getValue();
        messenger.send(message,receiver);
        Chat chat = chatHashMap.get(receiver);
        chat.send(message);
        text.clear();
    }

    private void receiveMessage(Message message){
        Platform.runLater(() -> {
            String sender = message.getSender();
            Chat chat = chatHashMap.get(sender);
            if (chat != null) {
                chat.receive(message,isCurrent(sender));
            }else {
                createAChat(sender,message,this);
            }
        });
        System.out.println(message);
    }

    private void createAChat(String sender, Message message,SupportAccount supportAccount) {
        usersLoading.setVisible(true);
        Chat chat = new Chat(sender);
        try {
            Entry<Parent, ChatUser> build = ChatUser.build(sender,supportAccount);
            chatBar.getChildren().add(build.getKey());
            chat.setUserPlate(build.getValue());
            chatHashMap.put(sender,chat);
            chat.receive(message,isCurrent(sender));
        } catch (IOException ignore) {
        } finally {
            usersLoading.setVisible(false);
        }
    }

    private void handleChangePass() {
        EditPassDialog editPassDialog = new EditPassDialog();
        String newPass = editPassDialog.show();

        if (newPass != null) {
            UserEditAttributes attributes = new UserEditAttributes();
            attributes.setNewPassword(newPass);
            try {
                connector.editPersonalInfo(attributes);
            } catch (Exception e) {
                Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
                e.printStackTrace();
            }
            Notification.show("Successful", "Your Password was Changed!!!", back.getScene().getWindow(), false);
        }
    }

    private void handleChangeProfile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg"));
        File selected = fileChooser.showOpenDialog(back.getScene().getWindow());

        if (selected != null) {
            Image toImage = new Image(String.valueOf(selected.toURI()));
            image.setFill(new ImagePattern(toImage));
            try {
                InputStream inputStream = new FileInputStream(selected);
                connector.saveUserImage(inputStream);
            } catch (Exception e) {
                Notification.show("Error", e.getMessage(), back.getScene().getWindow(), true);
                e.printStackTrace();
            }
            Notification.show("Successful", "Your Profile Pic was Updated!!!", back.getScene().getWindow(), false);
        }
    }

    private void logoutHandle() {
        shutdownService();
        try {
            Scene scene = new Scene(CFK.loadFXML("MainPage"));
            CFK.setSceneToStage(back,scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClose() {
        shutdownService();
        ((Stage)back.getScene().getWindow()).close();
    }

    private void shutdownService() {
        try {
            connector.setThisSupportOffline();
            messenger.stomp.stop();
        } catch (Exception ignore) {}
    }

    private void exceptionHandler(Throwable throwable){
        Notification.show("Error", throwable.getMessage(),back.getScene().getWindow(),true);
    }

    void changeChat(String username){
        Chat chat =  chatHashMap.get(username);
        if (chat != null) {
            messageBox.getChildren().clear();
            messageBox.getChildren().addAll(chat.getMessageTemplates());
            chat.setRead();
            currentChat.setValue(username);
        }
    }

    private boolean isCurrent(String username){
        return currentChat.getValue().equalsIgnoreCase(username);
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

    private class Messenger implements Runnable{
        CFStomp stomp;
        private String url;

        {
            stomp = new CFStomp();
            url = "ws://127.0.0.1:8050/chat";
        }

        @Override
        public void run() {
            WebSocketClient client = new StandardWebSocketClient();
            WebSocketStompClient stompClient = new WebSocketStompClient(client);
            stompClient.setMessageConverter(new MappingJackson2MessageConverter());
            StompSessionHandler handler = stomp;
            stompClient.connect(url,handler);
        }

        void send(Message message,String receiver){
            stomp.send(message, receiver);
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
                String username = CacheData.getInstance().getUsername();
                System.out.println("New session established : " + session.getSessionId());
                session.subscribe("/topic/messages/" + username,this);
                System.out.println("Subscribed");
                stompSession = session;
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                exceptionHandler(exception);
            }

            void send(Message message,String receiver){
                stompSession.send("/app/chat/" + receiver,message);
            }

            void stop(){
                stompSession.disconnect();
            }
        }
    }

    @Data
    private class Chat{
        private String username;
        private ArrayList<HBox> messageTemplates;
        private ChatUser userPlate;
        private byte pastSender;
        private IntegerProperty unread;

        Chat(String username) {
            this.username = username;
            pastSender = 0;
            unread = new SimpleIntegerProperty(0);
            messageTemplates = new ArrayList<>();
            bindings();
        }

        private void bindings() {
            unread.addListener((o,oldValue,newValue) -> userPlate.setUnreadAmount(newValue.intValue()));
        }

        void send(Message message){
            HBox template = (HBox) MessageBuilder.build(message.getMessage(),message.getSender(),
                    true,pastSender==1);
            pastSender = 0;
            messageTemplates.add(template);
            messageBox.getChildren().add(template);
        }

        void receive(Message message,boolean isCurrent){
            HBox template = (HBox) MessageBuilder.build(message.getMessage(),message.getSender(),
                    false,pastSender==0);
            pastSender = 1;
            if (isCurrent){
                unread.setValue(0);
                messageTemplates.add(template);
                messageBox.getChildren().add(template);
            }else {
                unread.setValue(unread.get() + 1);
                messageTemplates.add(template);
            }
        }

        void setRead(){
            userPlate.setRead();
        }
    }
}