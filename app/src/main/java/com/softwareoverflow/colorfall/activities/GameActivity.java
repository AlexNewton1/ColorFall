package com.softwareoverflow.colorfall.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.softwareoverflow.colorfall.AdvertHandler;
import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.free_trial.FreeTrialCountdown;
import com.softwareoverflow.colorfall.game.GameView;
import com.softwareoverflow.colorfall.game.Level;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;

public class GameActivity extends Activity {

    private GameView gameView;
    private AdView adView;
    private InterstitialAd interstitialAd;

    private boolean isFreeTrial = true;
    private FreeTrialCountdown countdown;
    private TextView countdownTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);

        //TODO - set isFreeTrial based on if user purchased and if level is HARD / INSANE
        countdownTextView = findViewById(R.id.free_trial_countdown_tv);
        if(isFreeTrial){
            countdownTextView.setVisibility(View.VISIBLE);
        } else {
            countdownTextView.setVisibility(View.GONE);
        }

        setupAds();
        setupGame();
    }

    private void setupAds(){
        adView = new AdvertHandler().getGameBannerAd();
        ConstraintLayout layout = findViewById(R.id.game_constraint_layout);
        ViewGroup adParent = (ViewGroup) adView.getParent();
        if(adParent != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }
        layout.addView(adView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        adView.resume();

        interstitialAd = new AdvertHandler().createQuitGameInterstitialAd(this);
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                finish();
            }

        });
    }

    private void setupGame() {
        findViewById(R.id.popup_free_trial).setVisibility(View.GONE);

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
        gameView.setFreeTrial(isFreeTrial, countdownTextView);

        sendAnalytics(level.name());
    }

    private void sendAnalytics(String levelName) {
        if(ConsentActivity.userConsent != ConsentActivity.Consent.GIVEN) return;

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Started new game");
        bundle.putString(FirebaseAnalytics.Param.LEVEL_NAME, levelName);

        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.LEVEL_START, bundle);
    }

    public void endFreeTrial(){
        //show end free trial screen here, promote full version

        gameView.onPause();

        gameView.setAlpha(0.8f);

        /*ViewGroup viewRoot =  findViewById(android.R.id.content);
        new FreeTrialDialog(this, viewRoot).show();*/
        View freeTrialPopup = findViewById(R.id.popup_free_trial);
        freeTrialPopup.findViewById(R.id.dialog_popup_bg).setClipToOutline(true);
        freeTrialPopup.setVisibility(View.VISIBLE);

    }

    public void resumeGame(View v) {
        gameView.startCountdown();
        setupAds();
    }

    public void quitGame(View v) {
        if(interstitialAd != null && interstitialAd.isLoaded()){
            stopService(new Intent(this, BackgroundMusicService.class));
            interstitialAd.show();
        } else {
            BackgroundMusicService.changingActivity = true;
            finish();
        }
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
        if(adView != null){
            adView.resume();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("debug2", "game onPause");
        if (!BackgroundMusicService.changingActivity) {
            stopService(new Intent(this, BackgroundMusicService.class));
        }

        if(isFreeTrial && countdown != null){
            countdown.cancel();
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

        FreeTrialCountdown.reset();
        super.onDestroy();
    }
}
