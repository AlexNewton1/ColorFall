package com.softwareoverflow.colorfall.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.softwareoverflow.colorfall.Level;
import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;
import com.softwareoverflow.colorfall.media.SoundEffectHandler;

public class MainActivity extends AppCompatActivity {

    private Intent backgroundMusic;
    private final int SETTINGS_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkSettings();
        backgroundMusic = new Intent(this, BackgroundMusicService.class);
        startService(backgroundMusic);
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

        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("difficulty", difficulty);
        startActivity(gameIntent);
    }

    public void showHiScores(View v) {
       // startActivity(new Intent(this, HiScoresActivity.class));
    }

    public void showSettings(View v) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivityForResult(settingsIntent, SETTINGS_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode != SETTINGS_REQUEST_CODE) return;

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
    }

    private void checkSettings(){
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        BackgroundMusicService.setPlayMusic(sharedPreferences.getBoolean("music", true));
        SoundEffectHandler.setPlaySounds(sharedPreferences.getBoolean("sounds", true));
    }

    @Override
    protected void onResume() {
        BackgroundMusicService.resumeMusic();

        super.onResume();
    }

    @Override
    protected void onPause() {
        BackgroundMusicService.stopMusic();

        super.onPause();
    }

    @Override
    protected void onStop() {
        BackgroundMusicService.stopMusic();

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        BackgroundMusicService.releaseResources();
        stopService(backgroundMusic);

        super.onDestroy();
    }
}
