package com.codefathers.cfkserver.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class BankUtil {
    private static BankUtil bankUtil = new BankUtil();

    public BankUtil() {
        run();
    }

    public static BankUtil getInstance() {
        return bankUtil;
    }

    public static final int PORT = 8090;
    public static final String IP = "192.168.1.4";

    private static DataOutputStream outputStream;
    private static DataInputStream inputStream;

    private String message;

    private void ConnectToBankServer() throws IOException {
        try {
            Socket socket = new Socket(IP, PORT);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new IOException("Exception while initiating connection:");
        }
    }

    public String getMessage() {
        return message;
    }

    private void startListeningOnInput() {
        new Thread(() -> {
            while (true) {
                try {
                    message = inputStream.readUTF();
                } catch (IOException e) {
                    System.out.println("disconnected");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendMessage(String msg) throws IOException {
        try {
            outputStream.writeUTF(msg);
        } catch (IOException e) {
            throw new IOException("Exception while sending message:");
        }
    }

    private void run() {
        try {
            ConnectToBankServer();
            startListeningOnInput();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


}
