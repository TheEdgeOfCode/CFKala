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
    public static final String IP = "127.0.0.1";

    private static DataOutputStream outputStream;
    private static DataInputStream inputStream;

    private String message;

    private void connectToBankServer() throws IOException {
        try {
            Socket socket = new Socket(IP, PORT);
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new IOException("Exception while initiating connection:");
        }
    }

    public String getMessage() {
        try {
            message = inputStream.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

    public void sendMessage(String msg) throws IOException {
        try {
            outputStream.writeUTF(msg);
            Thread.sleep(100);
        } catch (IOException e) {
            throw new IOException("Exception while sending message:");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void run() {
        try {
            connectToBankServer();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

}
