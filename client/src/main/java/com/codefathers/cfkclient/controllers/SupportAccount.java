package com.codefathers.cfkclient.controllers;

import com.codefathers.cfkclient.BackAbleController;
import com.codefathers.cfkclient.CacheData;
import com.codefathers.cfkclient.dtos.edit.UserEditAttributes;
import com.codefathers.cfkclient.dtos.support.Message;
import com.codefathers.cfkclient.utils.Connector;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.codefathers.cfkclient.controllers.MessageBuilder.build;

public class SupportAccount extends BackAbleController {
    @FXML private HBox chatsLoading;
    @FXML private HBox usersLoading;

    @FXML private JFXButton changePass;
    @FXML private JFXButton back;
    @FXML private JFXButton minimize;
    @FXML private JFXButton close;

    @FXML private VBox settings;
    @FXML private Circle image;
    @FXML private JFXButton changeProfile;
    @FXML private Label username;
    
    @FXML private VBox chat;
    @FXML private VBox messageBox;
    @FXML private TextField text;
    @FXML private JFXButton send;

    @FXML private VBox chatBar;

    private static final String userPhoto = "/Images/user-png-icon-male-user-icon-512.png";
    private static Connector connector = Connector.getInstance();

    private Messenger messenger = new Messenger();
    
    @FXML
    private void initialize(){
        personalInfo();
        buttons();
        binds();
        startMessagingUnit();
//        test();
    }

    private void startMessagingUnit() {
        new Thread(messenger).start();
    }

    private void binds() {
        send.disableProperty().bind(text.textProperty().isEmpty());
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
    }

    private void sendMessage() {
        Message message = new Message();
        String receiver = "";
        // TODO: 7/20/2020
        messenger.send(message,receiver);
    }

    private void receiveMessage(Message message){
        // TODO: 7/20/2020
        System.out.println(message);
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
        // TODO: 7/20/2020
    }

    private void handleClose() {
        shutdownService();
        ((Stage)back.getScene().getWindow()).close();
    }

    private void shutdownService() {
        // TODO: 7/20/2020
    }

    private void exceptionHandler(Throwable throwable){
        // TODO: 7/20/2020
    }

    private void test() {
        chatBar.getChildren().addAll(generateUser("hatam"));
        messageBox.getChildren().addAll(build("Hello!\nHow You Doin' ?!","hatam",false,true),
                build("Hi, Thanks!!","s",true,true),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false),
                build("Hi, Thanks!!","s",true,false));
    }

    private HBox generateUser(String username){
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
        box.getChildren().addAll(circle,label);
        return box;
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

        public void send(Message message,String receiver){
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
                System.out.println("Message sent tp ws server");
            }
        }
    }
}

@Data
@AllArgsConstructor
@Builder
class Chat{
    private static ArrayList<Chat> chats;
    private String user;
    private ArrayList<Message> messages;

    public static void reciveMessage(String user, Message message) {
        for (Chat chat : chats) {
            if (chat.user.equalsIgnoreCase(user)){
                chat.getMessages().add(message);
                return;
            }
        }
        chats.add(createNewChat(user,message));
    }

    private static Chat createNewChat(String user, Message message) {
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        return new Chat(user,messages);
    }

    public static void sendMessage(String to,Message message){
        for (Chat chat : chats) {
            if (chat.user.equalsIgnoreCase(to)){
                chat.getMessages().add(message);
                return;
            }
        }
    }

    public static ArrayList<Message> getAllMessagesFrom(String username) throws Exception {
        for (Chat chat : chats) {
            if (chat.user.equalsIgnoreCase(username)){
                return chat.getMessages();
            }
        }
        throw new Exception("No Such A User");
    }

    public static void deleteAChat(){
        // TODO: 7/20/2020
    }
}