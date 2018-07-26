package com.softwareoverflow.colorfall.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.softwareoverflow.colorfall.AdvertHandler;
import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.game.GameView;
import com.softwareoverflow.colorfall.game.Level;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;

public class GameActivity extends Activity {

    private GameView gameView;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        setupAd();
        setupGame();
    }

    private void setupAd(){
        adView = new AdvertHandler().getGameBannerAd();
        ConstraintLayout layout = findViewById(R.id.game_constraint_layout);
        ViewGroup adParent = (ViewGroup) adView.getParent();
        if(adParent != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }
        layout.addView(adView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        adView.resume();
    }

    private void setupGame() {
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
        //new WebView(this).resumeTimers();
        if (!BackgroundMusicService.changingActivity) {
            startService(new Intent(this, BackgroundMusicService.class));
        }
        BackgroundMusicService.changingActivity = false;

        if (gameView != null) {
            gameView.onResume();
        }
        if(adView != null){
            adView.resume();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        //new WebView(this).pauseTimers();
        if (!BackgroundMusicService.changingActivity) {
            stopService(new Intent(this, BackgroundMusicService.class));
        }

        if (gameView != null) {
            gameView.onPause();
        }
        if(adView != null){
            adView.pause();
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

    @Override
    protected void onDestroy() {
        gameView = null;
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
