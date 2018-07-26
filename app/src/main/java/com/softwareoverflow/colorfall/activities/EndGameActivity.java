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
import com.softwareoverflow.colorfall.AdvertHandler;
import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.animations.FadeInOutAnimation;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;

public class EndGameActivity extends AppCompatActivity {

    private InterstitialAd interstitialAd;
    private TextView playAgainButton, scoreTextView, hiScoreTextView;
    private String difficulty;

    private boolean isPlayingAgain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        //setup advert in advance of playing again
        new AdvertHandler().setupGameBanner(this);

        playAgainButton = findViewById(R.id.playAgainButton);
        scoreTextView = findViewById(R.id.endGameScoreTextView);
        hiScoreTextView = findViewById(R.id.endGameHiScoreTextView);

        Bundle extras = getIntent().getExtras();
        int score = 0;
        difficulty = "EASY";
        if(extras != null){
            score = extras.getInt("score");
            difficulty = extras.getString("difficulty");
        }

        animatePlayAgainButton();
        showScore(score);
        checkIfHiScore(score);

        interstitialAd = new AdvertHandler().createEndGameInterstitialAd(this);
        interstitialAd.setAdListener(new AdListener(){

            @Override
            public void onAdClosed() {
                leaveActivity();
            }
        });
    }

    private void leaveActivity(){
        BackgroundMusicService.changingActivity = true;

        if(isPlayingAgain){
            Intent gameIntent = new Intent(this, GameActivity.class);
            gameIntent.putExtra("difficulty", difficulty);
            startActivity(gameIntent);
            this.finish();
        } else {
            super.onBackPressed();
        }
    }

    private void animatePlayAgainButton(){
        playAgainButton.startAnimation(new FadeInOutAnimation(Animation.INFINITE));
    }

    private void showScore(int score){
        scoreTextView.setText(String.valueOf(score));
    }

    private void checkIfHiScore(int score){
        SharedPreferences sharedPrefs = getSharedPreferences("scores", MODE_PRIVATE);
        int levelHiScore = sharedPrefs.getInt(difficulty, 0);

        if(score > levelHiScore){
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

    public void playAgain(View v){
        isPlayingAgain = true;
        if(interstitialAd.isLoaded()){
            BackgroundMusicService.changingActivity = true;
            interstitialAd.show();
        } else {
            leaveActivity();
        }
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
        if(interstitialAd != null && interstitialAd.isLoaded()){
            BackgroundMusicService.changingActivity = true;
            interstitialAd.show();
        } else {
            leaveActivity();
        }
    }
}
