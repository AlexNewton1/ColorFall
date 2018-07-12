package com.softwareoverflow.colorfall.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.game.GameView;
import com.softwareoverflow.colorfall.game.Level;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;

public class GameActivity extends Activity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_screen);

        //default value
        Level level = Level.EASY;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String levelDifficulty = extras.getString("difficulty");
            level = Level.valueOf(levelDifficulty);
        }
        level.resetSpeed();
        level.setColours();

        gameView = findViewById(R.id.gameView);
        gameView.setLevel(level, this);

        setupAd();
        sendAnalytics(level.name());
    }

    private void setupAd() {
        AdView adView = findViewById(R.id.game_banner_ad);
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void sendAnalytics(String levelName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Started new game");
        bundle.putString(FirebaseAnalytics.Param.LEVEL_NAME, levelName);

        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.LEVEL_START, bundle);
    }

    public void resumeGame(View v) {
        gameView.startCountdown();
    }

    public void quitGame(View v) {
        BackgroundMusicService.changingActivity = true;
        this.finish();
    }

    @Override
    protected void onResume() {
        if (!BackgroundMusicService.changingActivity) {
            startService(new Intent(this, BackgroundMusicService.class));
        }
        BackgroundMusicService.changingActivity = false;

        if (gameView != null) {
            gameView.onResume();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        if (!BackgroundMusicService.changingActivity) {
            stopService(new Intent(this, BackgroundMusicService.class));
        }

        if (gameView != null) {
            gameView.onPause();
        }

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        BackgroundMusicService.changingActivity = true;
        onPause();
        onResume();
        BackgroundMusicService.changingActivity = false;
    }
}
