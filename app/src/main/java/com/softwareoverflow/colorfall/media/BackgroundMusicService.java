package com.softwareoverflow.colorfall.media;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.softwareoverflow.colorfall.R;

public class BackgroundMusicService extends Service {

    private static MediaPlayer mediaPlayer;
    private static float volume = 0;
    public static boolean changingActivity;

    private static boolean playMusic = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        volume = 0;
        mediaPlayer.start();
        fadeIn();

        if(!playMusic){
            mediaPlayer.pause();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private static void fadeIn(){
        final Handler fadeHandler = new Handler();
        final Runnable fadeInRunnable = new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer == null || !playMusic || !mediaPlayer.isPlaying()){
                    return;
                }

                if(volume < 1) {
                    volume += 0.05;
                    mediaPlayer.setVolume(volume, volume);

                    fadeHandler.postDelayed(this, 500);
                }

            }
        };

        fadeHandler.post(fadeInRunnable);
    }

    public static boolean getPlayMusic(){
        return playMusic;
    }

    @Override
    public void onDestroy() {
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    public static void stopMusic(){
        if(mediaPlayer != null && !changingActivity && mediaPlayer.isPlaying()) {
            volume = 0;
            mediaPlayer.pause();
        }
    }

    public static void resumeMusic() {
        if(mediaPlayer != null && playMusic) {
            mediaPlayer.start();
            fadeIn();
        }
    }
    public static void restartMusic(){
        if(mediaPlayer != null && playMusic) {
            volume = 0;
            mediaPlayer.seekTo(0);
            resumeMusic();
        }
    }

    public static void setPlayMusic(boolean playMusic){
        BackgroundMusicService.playMusic = playMusic;
    }
}
