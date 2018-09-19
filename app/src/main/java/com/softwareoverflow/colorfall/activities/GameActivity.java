package com.softwareoverflow.colorfall.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.free_trial.AdvertHandler;
import com.softwareoverflow.colorfall.free_trial.FreeTrialCountdown;
import com.softwareoverflow.colorfall.free_trial.FreeTrialPopup;
import com.softwareoverflow.colorfall.free_trial.UpgradeManager;
import com.softwareoverflow.colorfall.game.GameView;
import com.softwareoverflow.colorfall.game.Level;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;
import com.softwareoverflow.colorfall.media.SoundEffectHandler;

public class GameActivity extends Activity implements FreeTrialPopup{

    private GameView gameView;
    private AdView adView;
    private InterstitialAd interstitialAd;

    private boolean isFreeTrial = false;
    private TextView countdownTextView;
    private View freeTrialPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);


        Level level = getLevel();
        isFreeTrial = UpgradeManager.isFreeUser() &&
                (level.equals(Level.HARD) || level.equals(Level.INSANE));

        countdownTextView = findViewById(R.id.free_trial_countdown_tv);
        if(isFreeTrial){
            FreeTrialCountdown.reset();
            countdownTextView.setVisibility(View.VISIBLE);
        } else {
            countdownTextView.setVisibility(View.GONE);
        }

        freeTrialPopup = findViewById(R.id.popup_free_trial);
        freeTrialPopup.findViewById(R.id.dialog_popup_bg).setClipToOutline(true);

        setupAds();
        setupGame(level);

        if(ConsentActivity.userConsent == ConsentActivity.Consent.GIVEN){
            sendAnalytics(level.name());
        }
    }

    @Override
    public void playFreeVersion(View v) {
        SoundEffectHandler.getInstance(this).playSound(SoundEffectHandler.Sound.GAME_OVER);
        gameView.endGame();
    }

    @Override
    public void upgradeNow(View v) {
        //TODO - upgrade
    }

    private void setupAds(){
        if(!UpgradeManager.isFreeUser()){
            return;
        }

        adView = new AdvertHandler().getGameBannerAd();
        FrameLayout layout = findViewById(R.id.game_banner_ad_frame_wrapper);
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

    private void setupGame(Level level) {
        findViewById(R.id.popup_free_trial).setVisibility(View.GONE);

        //default value
        level.resetSpeed();
        level.setColours();

        gameView = findViewById(R.id.gameView);
        gameView.setLevel(level, this);
        gameView.setFreeTrial(isFreeTrial, countdownTextView);
    }

    private Level getLevel(){
        Level level = Level.EASY;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String levelDifficulty = extras.getString("difficulty");
            level = Level.valueOf(levelDifficulty);
        }

        return level;
    }

    private void sendAnalytics(String levelName) {
        if(ConsentActivity.userConsent != ConsentActivity.Consent.GIVEN) return;

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Started new game");
        bundle.putString(FirebaseAnalytics.Param.LEVEL_NAME, levelName);

        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.LEVEL_START, bundle);
    }

    public void endFreeTrial(){
        SoundEffectHandler.getInstance(this).playSound(SoundEffectHandler.Sound.GAME_OVER);

        gameView.onPause();
        freeTrialPopup.setVisibility(View.VISIBLE);
    }

    public void resumeGame(View v) {
        gameView.startCountdown();
        setupAds();
    }

    public void quitGame(View v) {
        if(UpgradeManager.isFreeUser())
            new AdvertHandler().setupGameBanner(this);

        if(interstitialAd != null && interstitialAd.isLoaded() && UpgradeManager.isFreeUser()){
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
        if(freeTrialPopup.getVisibility() == View.VISIBLE){
            return; //do nothing
        }

        if(gameView.getTutorial().isCurrentlyShowing){
            gameView.getTutorial().onClick(null);
            gameView.startGame();
            return;
        }


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
