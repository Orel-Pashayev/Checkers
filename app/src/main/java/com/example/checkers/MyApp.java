package com.example.checkers;

import android.app.Application;
import android.media.MediaPlayer;

public class MyApp extends Application {
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void startBackgroundMusic(String song) {
        if (mediaPlayer == null || !mediaPlayer.isPlaying()) {
            if (song == "chill")
                mediaPlayer = MediaPlayer.create(this, R.raw.chill);
            else
                mediaPlayer = MediaPlayer.create(this, R.raw.electric);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    public void pauseBackgroundMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void resumeBackgroundMusic() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void stopBackgroundMusic() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.prepareAsync();
        }
    }
}
