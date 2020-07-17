package com.codefathers.cfkclient;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class SoundCenter {
    private static MediaPlayer mediaPlayer;

    public static void play(Sound sound) {
        URL resource = getSoundSource(sound);
        AudioClip audioClip = new AudioClip(resource.toString());
        audioClip.play();
    }

    private static URL getSoundSource(Sound sound) {
        return CFK.class.getClassLoader().getResource("sounds/" + sound.toString() + ".wav");
    }

    public static void background() {
        URL pass = CFK.class.getClassLoader().getResource("sounds/background.mp3");
        mediaPlayer = new MediaPlayer(new Media(pass.toString()));
        mediaPlayer.setVolume(0.15);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }
}

