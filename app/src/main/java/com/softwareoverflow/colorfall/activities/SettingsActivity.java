package com.softwareoverflow.colorfall.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;

//TODO - Fix outline not appearing properly
//TODO - Start / Stop music when setting gets changed, instead of only when returning to homepage
//TODO - add sound effect for turning on sound effects
public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private boolean playMusic, playSounds;

    TextView playMusicOn, playMusicOff, playSoundsOn, playSoundsOff;
    private Drawable borderDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        BackgroundMusicService.changingActivity = false;
        Log.e("debug", "Settings onCreate: " + BackgroundMusicService.changingActivity);

        playMusicOn = findViewById(R.id.play_music_on);
        playMusicOff = findViewById(R.id.play_music_off);
        playSoundsOn = findViewById(R.id.play_sounds_on);
        playSoundsOff = findViewById(R.id.play_sounds_off);

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        playMusic = sharedPreferences.getBoolean("music", true);
        playSounds = sharedPreferences.getBoolean("sounds", true);

        borderDrawable = getDrawable(R.drawable.white_border);
        if (playMusic) {
            playMusicOn.setBackground(borderDrawable);
        } else {
            playMusicOff.setBackground(borderDrawable);
        }

        if (playSounds) {
            playSoundsOn.setBackground(borderDrawable);
        } else {
            playSoundsOff.setBackground(borderDrawable);
        }
    }

    public void changeSettings(View v){
        Log.e("debug", "Called changeSettings");
        switch (v.getId()){
            case R.id.play_music_off:
                setMusic(false);
                Log.e("debug", "Called stopMusic");
                break;
            case R.id.play_music_on:
                setMusic(true);
                Log.e("debug", "Called restart");
                break;
            case R.id.play_sounds_off:
                playSounds = false;
                playSoundsOn.setBackground(null);
                playSoundsOff.setBackground(borderDrawable);
                break;
            case R.id.play_sounds_on:
                playSounds = true;
                playSoundsOff.setBackground(null);
                playSoundsOn.setBackground(borderDrawable);
                break;
        }
    }

    public void setMusic(boolean music){
        playMusic = music;
        BackgroundMusicService.setPlayMusic(music);
        if(music) {
            BackgroundMusicService.restartMusic();
            playMusicOff.setBackground(null);
            playMusicOn.setBackground(borderDrawable);
        } else {
            BackgroundMusicService.stopMusic();
            playMusicOn.setBackground(null);
            playMusicOff.setBackground(borderDrawable);
        }
    }

    public void cancelSettings(View v){
        onBackPressed();
    }

    public void saveSettings(View v){
        sharedPreferences.edit().putBoolean("music", playMusic).apply();
        sharedPreferences.edit().putBoolean("sounds", playSounds).apply();

        BackgroundMusicService.changingActivity = true;
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        playMusic = sharedPreferences.getBoolean("music", true);
        BackgroundMusicService.setPlayMusic(playMusic);

        BackgroundMusicService.changingActivity = true;
        super.onBackPressed();
    }
}
