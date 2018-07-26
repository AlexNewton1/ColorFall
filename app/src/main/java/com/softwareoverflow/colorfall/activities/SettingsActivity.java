package com.softwareoverflow.colorfall.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;
import com.softwareoverflow.colorfall.media.SoundEffectHandler;

//TODO - Fix outline not appearing properly
public class SettingsActivity extends AppCompatActivity {

    private final int CONSENT_REQUEST_CODE = 1;

    private SharedPreferences sharedPreferences;
    private boolean playMusic, playSounds;

    private SoundEffectHandler soundEffectHandler;

    TextView playMusicOn, playMusicOff, playSoundsOn, playSoundsOff, consentOn, consentOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        soundEffectHandler = SoundEffectHandler.getInstance(this);

        playMusicOn = findViewById(R.id.play_music_on);
        playMusicOff = findViewById(R.id.play_music_off);
        playSoundsOn = findViewById(R.id.play_sounds_on);
        playSoundsOff = findViewById(R.id.play_sounds_off);
        consentOn = findViewById(R.id.consent_on);
        consentOff = findViewById(R.id.consent_off);

        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        playMusic = sharedPreferences.getBoolean("music", true);
        playSounds = sharedPreferences.getBoolean("sounds", true);

        updateViews();
    }

    private void updateViews(){
        if (playMusic) {
            playMusicOn.setTextColor(Color.WHITE);
            playMusicOff.setTextColor(Color.GRAY);
        } else {
            playMusicOn.setTextColor(Color.GRAY);
            playMusicOff.setTextColor(Color.WHITE);
        }

        if (playSounds) {
            playSoundsOn.setTextColor(Color.WHITE);
            playSoundsOff.setTextColor(Color.GRAY);
        } else {
            playSoundsOn.setTextColor(Color.GRAY);
            playSoundsOff.setTextColor(Color.WHITE);
        }

        if(ConsentActivity.userConsent == ConsentActivity.Consent.GIVEN){
            consentOn.setTextColor(Color.WHITE);
            consentOff.setTextColor(Color.GRAY);
        } else {
            consentOn.setTextColor(Color.GRAY);
            consentOff.setTextColor(Color.WHITE);
        }
    }

    public void changeSettings(View v){
        switch (v.getId()){
            case R.id.play_music_off:
                playMusic = false;
                BackgroundMusicService.setPlayMusic(false);
                BackgroundMusicService.stopMusic();
                break;
            case R.id.play_music_on:
                playMusic = true;
                BackgroundMusicService.setPlayMusic(true);
                BackgroundMusicService.restartMusic();
                break;
            case R.id.play_sounds_off:
                playSounds = false;
                break;
            case R.id.play_sounds_on:
                soundEffectHandler.playSound(SoundEffectHandler.Sound.SCORE);
                playSounds = true;
                break;
            case R.id.consent_on: //fall through
            case R.id.consent_off:
                BackgroundMusicService.changingActivity = true;
                startActivityForResult(new Intent(this, ConsentActivity.class),
                        CONSENT_REQUEST_CODE);
                break;
        }

        updateViews();
    }

    public void cancelSettings(View v){
        if(playMusic != sharedPreferences.getBoolean("music", true)) {
            playMusic = sharedPreferences.getBoolean("music", true);
            BackgroundMusicService.setPlayMusic(playMusic);
            if(playMusic) {
                BackgroundMusicService.restartMusic();
            } else {
                BackgroundMusicService.stopMusic();
            }
        }
        onBackPressed();
    }

    public void saveSettings(View v){
        sharedPreferences.edit().putBoolean("music", playMusic).apply();
        sharedPreferences.edit().putBoolean("sounds", playSounds).apply();
        sharedPreferences.edit().putString("consent", ConsentActivity.userConsent.name()).apply();

        BackgroundMusicService.changingActivity = true;
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

        sendAnalytics();
    }

    private void sendAnalytics(){
        if(ConsentActivity.userConsent != ConsentActivity.Consent.GIVEN) return;

        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(this);
        analytics.setUserProperty("background_music", String.valueOf(playMusic));
        analytics.setUserProperty("sound_effects", String.valueOf(playSounds));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode != CONSENT_REQUEST_CODE) return;
        if(resultCode == RESULT_OK) updateViews();
    }

    @Override
    protected void onResume() {
        if(!BackgroundMusicService.changingActivity) {
            startService(new Intent(this, BackgroundMusicService.class));
        }
        BackgroundMusicService.changingActivity = false;

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
    public void onBackPressed() {
        playMusic = sharedPreferences.getBoolean("music", true);
        BackgroundMusicService.setPlayMusic(playMusic);

        BackgroundMusicService.changingActivity = true;
        super.onBackPressed();
    }
}
