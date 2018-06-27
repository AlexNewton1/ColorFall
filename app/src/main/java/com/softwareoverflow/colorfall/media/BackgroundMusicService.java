package com.softwareoverflow.colorfall.media;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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
                if(volume < 1) {
                    volume += 0.05;
                    mediaPlayer.setVolume(volume, volume);

                    fadeHandler.postDelayed(this, 500);
                }

            }
        };

        fadeHandler.post(fadeInRunnable);
    }

    @Override
    public void onDestroy() {
        if(mediaPlayer != null  && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    public static void stopMusic(){
        Log.e("debug", "Stopping music: " + mediaPlayer.toString() + ", " +  changingActivity);
        if(mediaPlayer != null && !changingActivity) {
            volume = 0;
            mediaPlayer.pause();

            Log.e("debug", "Stopped music");
        }
    }

    public static void resumeMusic() {
        if(mediaPlayer != null && playMusic) {
            mediaPlayer.start();
            fadeIn();

            Log.e("debug", "resumed music");
        }
    }

    public static void restartMusic(){
        Log.e("debug", "restartMusic: " + mediaPlayer + ", " + playMusic);
        if(mediaPlayer != null && playMusic) {
            volume = 0;
            mediaPlayer.seekTo(0);
            resumeMusic();
        }
    }

    public static void releaseResources() {
        if(mediaPlayer != null ) {
            mediaPlayer.stop();
            mediaPlayer.release();

            Log.e("debug", "Released resources");
        }
    }

    public static void setPlayMusic(boolean playMusic){
        BackgroundMusicService.playMusic = playMusic;
    }
}
