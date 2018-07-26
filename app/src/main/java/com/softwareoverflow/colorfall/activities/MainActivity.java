package com.softwareoverflow.colorfall.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.softwareoverflow.colorfall.AdvertHandler;
import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.game.Level;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;
import com.softwareoverflow.colorfall.media.SoundEffectHandler;

public class MainActivity extends AppCompatActivity {

    //TODO - FIX music starting on app launch, and staying when auto-opening consent activity!

    private final int SETTINGS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void playGame(View v) {
        String difficulty = Level.BEGINNER.name(); //default to beginner
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

        BackgroundMusicService.changingActivity = true;
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("difficulty", difficulty);
        startActivity(gameIntent);
    }

    public void showHiScores(View v) {
        BackgroundMusicService.changingActivity = true;
        startActivity(new Intent(this, HiScoresActivity.class));
    }

    public void showSettings(View v) {
        BackgroundMusicService.changingActivity = true;
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivityForResult(settingsIntent, SETTINGS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){
            case SETTINGS_REQUEST_CODE:
                if(resultCode == RESULT_OK) {//settings modified
                    Snackbar snackbar = Snackbar
                            .make(findViewById(android.R.id.content),
                                    R.string.settings_updated, Snackbar.LENGTH_SHORT);

                    View snackView = snackbar.getView();
                    snackView.setAlpha(0.4f);
                    TextView tv = snackView.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    snackbar.show();

                    checkSettings();
                }
                break;
        }
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
        //setup advert in advance
        //TODO use gradle for this to hide away private keys
        MobileAds.initialize(this, getString(R.string.app_ad_id));
        new AdvertHandler().setupGameBanner(this);

        checkSettings();

        if(!BackgroundMusicService.changingActivity) {
            startService(new Intent(this, BackgroundMusicService.class));
        }

        BackgroundMusicService.changingActivity = false;

        if(ConsentActivity.userConsent == ConsentActivity.Consent.UNKNOWN){
            BackgroundMusicService.changingActivity = true;
            startActivity(new Intent(this, ConsentActivity.class));
        }

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