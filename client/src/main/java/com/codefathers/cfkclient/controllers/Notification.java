package com.codefathers.cfkclient.controllers;

import javafx.geometry.Pos;
import javafx.stage.Window;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import static com.codefathers.cfkclient.Sound.ERROR;
import static com.codefathers.cfkclient.Sound.SUCCESSFUL;
import static com.codefathers.cfkclient.SoundCenter.play;

public class Notification {
    public static void show(String title, String message, Window window, boolean error){
        Notifications notification = Notifications.create()
                .title(title)
                .text(message)
                .hideAfter(Duration.seconds(2))
                .position(Pos.TOP_CENTER)
                .owner(window);
        if (error){
            notification.showError();
            play(ERROR);
        } else {
            notification.showInformation();
            play(SUCCESSFUL);
        }
    }
}
