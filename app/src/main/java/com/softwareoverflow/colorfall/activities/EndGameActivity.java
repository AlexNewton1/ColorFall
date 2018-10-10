package com.softwareoverflow.colorfall.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.animations.FadeInOutAnimation;
import com.softwareoverflow.colorfall.free_trial.AdvertHandler;
import com.softwareoverflow.colorfall.free_trial.UpgradeManager;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;
import com.softwareoverflow.colorfall.media.SoundEffectHandler;

public class EndGameActivity extends AppCompatActivity {

    private InterstitialAd interstitialAd;
    private TextView playAgainButton, scoreTextView, hiScoreTextView;
    private String difficulty;

    private View freeTrialPopup;

    private boolean isPlayingAgain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);


        playAgainButton = findViewById(R.id.playAgainButton);
        scoreTextView = findViewById(R.id.endGameScoreTextView);
        hiScoreTextView = findViewById(R.id.endGameHiScoreTextView);

        Bundle extras = getIntent().getExtras();
        int score = 0;
        difficulty = "EASY";
        if (extras != null) {
            score = extras.getInt("score");
            difficulty = extras.getString("difficulty");
        }

        animatePlayAgainButton();
        showScore(score);
        checkIfHiScore(score);

        freeTrialPopup = findViewById(R.id.popup_free_trial);
        freeTrialPopup.findViewById(R.id.dialog_popup_bg).setClipToOutline(true);


        //setup advert in advance
        if (UpgradeManager.isFreeUser()) {
            new AdvertHandler().setupGameBanner(this);
            interstitialAd = new AdvertHandler().createEndGameInterstitialAd(this);
            if(interstitialAd != null)
            {
                interstitialAd.setAdListener(new AdListener() {

                    @Override
                    public void onAdClosed() {
                        BackgroundMusicService.changingActivity = false;
                        leaveActivity();
                    }
                });
            }

            freeTrialPopup.setVisibility(View.VISIBLE);
        }
    }

    private void leaveActivity() {
        if (isPlayingAgain) {
            Intent gameIntent = new Intent(this, GameActivity.class);
            gameIntent.putExtra("difficulty", difficulty);
            startActivity(gameIntent);
        }

        this.finish();
    }

    private void animatePlayAgainButton() {
        playAgainButton.startAnimation(new FadeInOutAnimation(Animation.INFINITE));
    }

    private void showScore(int score) {
        scoreTextView.setText(String.valueOf(score));
    }

    private void checkIfHiScore(int score) {
        SharedPreferences sharedPrefs = getSharedPreferences("scores", MODE_PRIVATE);
        int levelHiScore = sharedPrefs.getInt(difficulty, 0);

        if (score > levelHiScore) {
            levelHiScore = score;
            sharedPrefs.edit().putInt(difficulty, levelHiScore).apply();

            TextView hiScoreLabel = findViewById(R.id.hi_score_label);
            hiScoreLabel.setVisibility(View.GONE);


            FadeInOutAnimation fade = new FadeInOutAnimation(Animation.INFINITE);
            TextView hiScore = findViewById(R.id.endGameHiScoreTextView);
            hiScore.startAnimation(fade);
            TextView newHiScore = findViewById(R.id.new_hi_score);
            newHiScore.setVisibility(View.VISIBLE);
            newHiScore.startAnimation(fade);
        }

        hiScoreTextView.setText(String.valueOf(levelHiScore));
    }

    public void playAgain(View v) {
        isPlayingAgain = true;
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            BackgroundMusicService.changingActivity = false;
            interstitialAd.show();
        } else {
            BackgroundMusicService.changingActivity = true;
            leaveActivity();
        }
    }

    public void playFreeVersion(View v) {
        SoundEffectHandler.getInstance(this).playSound(SoundEffectHandler.Sound.GAME_OVER);
        freeTrialPopup.setVisibility(View.GONE);
    }

    public void upgradeNow(View v) {
        UpgradeManager.upgrade(this);
    }


    @Override
    protected void onResume() {
        UpgradeManager.checkUserPurchases(this);
        if (!UpgradeManager.isFreeUser()) {
            interstitialAd = null;
            if (freeTrialPopup != null) freeTrialPopup.setVisibility(View.GONE);
        }

        if (!BackgroundMusicService.changingActivity) {
            startService(new Intent(this, BackgroundMusicService.class));
        }
        BackgroundMusicService.changingActivity = false;

        super.onResume();
    }

    @Override
    protected void onPause() {
        if (!BackgroundMusicService.changingActivity) {
            stopService(new Intent(this, BackgroundMusicService.class));
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (freeTrialPopup.getVisibility() == View.VISIBLE) {
            playFreeVersion(null);
            return;
        }

        if (interstitialAd != null && interstitialAd.isLoaded()) {
            BackgroundMusicService.changingActivity = false;
            interstitialAd.show();
        } else {
            BackgroundMusicService.changingActivity = true;
            leaveActivity();
        }
    }
}