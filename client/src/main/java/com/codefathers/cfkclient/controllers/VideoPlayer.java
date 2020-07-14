package com.codefathers.cfkclient.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class VideoPlayer {
    @FXML
    private JFXButton close;
    @FXML private JFXButton play;
    @FXML private JFXButton pause;
    @FXML private MediaView player;

    private static int id;
    private MediaPlayer mediaPlayer;
}
