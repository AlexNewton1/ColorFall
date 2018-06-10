package com.softwareoverflow.colorfall.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.softwareoverflow.colorfall.R;
import com.softwareoverflow.colorfall.media.BackgroundMusicService;

public class EndGameActivity extends AppCompatActivity {

    private TextView playAgainButton, scoreTextView, hiScoreTextView;
    private String difficulty;

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
        if(extras != null){
            score = extras.getInt("score");
            difficulty = extras.getString("difficulty");
        }

        animatePlayAgainButton();
        showScore(score);
        checkIfHiScore(score);
    }

    private void animatePlayAgainButton(){
        Animation fade = new AlphaAnimation(0.1f, 1f);
        fade.setInterpolator(new LinearInterpolator());
        fade.setRepeatCount(Animation.INFINITE);
        fade.setRepeatMode(Animation.REVERSE);
        fade.setDuration(1000);

        playAgainButton.startAnimation(fade);
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

            //TODO - add 'New Hi Score' visual user feedback on layout
        }

        hiScoreTextView.setText(String.valueOf(levelHiScore));
    }


    public void playAgain(View v){
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("difficulty", difficulty);
        startActivity(gameIntent);
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BackgroundMusicService.stopMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BackgroundMusicService.resumeMusic();
    }
}
