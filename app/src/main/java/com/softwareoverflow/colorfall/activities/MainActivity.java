package com.softwareoverflow.colorfall.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.free_trial.UpgradeManager;
import com.softwareoverflow.colorfall.game.Level;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;
import com.softwareoverflow.colorfall.media.SoundEffectHandler;

public class MainActivity extends AppCompatActivity {

    private String difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void playGame(View v) {
        difficulty = Level.BEGINNER.name(); //default to beginner
        switch (v.getId()) {
            case R.id.playBeginner:
                difficulty = Level.BEGINNER.name();
                break;
            case R.id.playEasy:
                difficulty = Level.EASY.name();
                break;
            case R.id.playMedium:
                difficulty = Level.MEDIUM.name();
                break;
            case R.id.playHard:
                difficulty = Level.HARD.name();
                break;
            case R.id.playInsane:
                difficulty = Level.INSANE.name();
                break;
        }

        startGame(difficulty);
    }

    private void startGame(String difficulty){
        BackgroundMusicService.changingActivity = true;
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("difficulty", difficulty);
        startActivity(gameIntent);
    }

    public void playFreeVersion(View v) {
        startGame(difficulty);
    }

    public void upgradeNow(View v) {
        UpgradeManager.upgrade(this);
    }

    public void showHiScores(View v) {
        BackgroundMusicService.changingActivity = true;
        startActivity(new Intent(this, HiScoresActivity.class));
    }

    public void showSettings(View v) {
        BackgroundMusicService.changingActivity = true;
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void checkSettings(){
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        BackgroundMusicService.setPlayMusic(sharedPreferences.getBoolean(
                "music", true));
        SoundEffectHandler.setPlaySounds(sharedPreferences.getBoolean(
                "sounds", true));

        String consentValue = sharedPreferences.getString(
                "consent", ConsentActivity.Consent.UNKNOWN.name());
        try {
            ConsentActivity.userConsent = ConsentActivity.Consent.valueOf(consentValue);
        } catch (IllegalArgumentException ex){
            ConsentActivity.userConsent = ConsentActivity.Consent.UNKNOWN;
        }
    }

    @Override
    protected void onResume() {
        checkSettings();

        if(!BackgroundMusicService.changingActivity) {
            startService(new Intent(this, BackgroundMusicService.class));
        }
        BackgroundMusicService.changingActivity = false;

        if(ConsentActivity.userConsent == ConsentActivity.Consent.UNKNOWN){
            BackgroundMusicService.changingActivity = true;
            startActivity(new Intent(this, ConsentActivity.class));
        }

        UpgradeManager.checkUserPurchases(this);

        super.onResume();
    }

    @Override
    protected void onPause() {
        if(!BackgroundMusicService.changingActivity) {
            stopService(new Intent(this, BackgroundMusicService.class));
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, BackgroundMusicService.class));
        super.onDestroy();
    }
}