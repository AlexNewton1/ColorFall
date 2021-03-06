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
import com.softwareoverflow.colorfall.free_trial.UpgradeManager;
import com.softwareoverflow.colorfall.game.GameView;
import com.softwareoverflow.colorfall.game.Level;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;

public class GameActivity extends Activity {

    private GameView gameView;
    private AdView adView;
    private InterstitialAd interstitialAd;

    private boolean isFreeTrial = false;
    private TextView countdownTextView;


    //boolean for track if the game activity is running (used in the AdvertHandler class)
    public static boolean isGameRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_screen);


        Level level = getLevel();
        isFreeTrial = UpgradeManager.isFreeUser() &&
                (level.equals(Level.HARD) || level.equals(Level.INSANE));

        countdownTextView = findViewById(R.id.free_trial_countdown_tv);
        if (isFreeTrial) {
            FreeTrialCountdown.reset();
            countdownTextView.setVisibility(View.VISIBLE);
        } else {
            countdownTextView.setVisibility(View.GONE);
        }

        setupAds();
        setupGame(level);

        if (ConsentActivity.userConsent == ConsentActivity.Consent.GIVEN) {
            sendAnalytics(level.name());
        }
    }

    public GameView getGameView() {
        return gameView;
    }

    private void setupAds() {
        if (!UpgradeManager.isFreeUser()) {
            return;
        }

        adView = new AdvertHandler().getGameBannerAd();
        FrameLayout layout = findViewById(R.id.game_banner_ad_frame_wrapper);
        ViewGroup adParent = (ViewGroup) adView.getParent();
        if (adParent != null) {
            ((ViewGroup) adView.getParent()).removeView(adView);
        }
        layout.addView(adView, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

        adView.resume();

        interstitialAd = new AdvertHandler().getQuitGameInterstitialAd(this);
        if (interstitialAd != null) {
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    finish();
                }

            });
        }
    }

    private void setupGame(Level level) {
        //default value
        level.resetSpeed();
        level.setColours();

        gameView = findViewById(R.id.gameView);
        gameView.setLevel(level, this);
        gameView.setFreeTrial(isFreeTrial, countdownTextView);
    }

    private Level getLevel() {
        Level level = Level.EASY;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String levelDifficulty = extras.getString("difficulty");
            level = Level.valueOf(levelDifficulty);
        }

        return level;
    }

    private void sendAnalytics(String levelName) {
        if (ConsentActivity.userConsent != ConsentActivity.Consent.GIVEN) return;

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "Started new game");
        bundle.putString(FirebaseAnalytics.Param.LEVEL_NAME, levelName);

        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.LEVEL_START, bundle);
    }

    public void resumeGame(View v) {
        gameView.startCountdown();
        setupAds();
    }

    public void quitGame(View v) {
        if (interstitialAd != null && interstitialAd.isLoaded() && UpgradeManager.isFreeUser()) {
            stopService(new Intent(this, BackgroundMusicService.class));
            interstitialAd.show();
        } else {
            BackgroundMusicService.changingActivity = true;
            finish();
        }

        if(UpgradeManager.isFreeUser()) {
            AdvertHandler advertHandler = new AdvertHandler();
            advertHandler.setupGameBanner(this);
            advertHandler.setupQuitGameInterstitial(this);

        }


    }


    @Override
    protected void onResume() {
        isGameRunning = true;

        if (!BackgroundMusicService.changingActivity) {
            startService(new Intent(this, BackgroundMusicService.class));
        }
        BackgroundMusicService.changingActivity = false;

        UpgradeManager.checkUserPurchases(this);
        if (adView != null) {
            if (!UpgradeManager.isFreeUser()) {
                adView.setVisibility(View.GONE);
                interstitialAd = null;
            } else {
                adView.resume();
            }
        }


        if (gameView != null) {
            gameView.onResume();
        }

        super.onResume();
    }

    @Override
    protected void onPause() {
        isGameRunning = false;

        if (!BackgroundMusicService.changingActivity) {
            stopService(new Intent(this, BackgroundMusicService.class));
        }

        if (gameView != null) {
            gameView.onPause();
        }
        if (adView != null) {
            adView.pause();
        }

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (gameView.getTutorial() != null && gameView.getTutorial().isCurrentlyShowing) {
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

        isGameRunning = false;
        super.onDestroy();
    }
}
