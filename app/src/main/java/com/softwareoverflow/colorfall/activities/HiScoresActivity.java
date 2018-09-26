package com.softwareoverflow.colorfall.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.softwareoverflow.colorfall.game.Level;
import com.softwareoverflow.colorfall.R;

public class HiScoresActivity extends AppCompatActivity {

    private TextView beginnerHiScore, easyHiScore, mediumHiScore, hardHiScore, insaneHiScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hi_scores);

        beginnerHiScore = findViewById(R.id.hi_score_beginner);
        easyHiScore = findViewById(R.id.hi_score_easy);
        mediumHiScore = findViewById(R.id.hi_score_medium);
        hardHiScore = findViewById(R.id.hi_score_hard);
        insaneHiScore = findViewById(R.id.hi_score_insane);

        displayHiScores();
    }

    private void displayHiScores(){
        SharedPreferences sharedPreferences = getSharedPreferences("scores", MODE_PRIVATE);

        beginnerHiScore.setText(String.valueOf(sharedPreferences.getInt(Level.BEGINNER.name(), 0)));
        easyHiScore.setText(String.valueOf(sharedPreferences.getInt(Level.EASY.name(), 0)));
        mediumHiScore.setText(String.valueOf(sharedPreferences.getInt(Level.MEDIUM.name(), 0)));
        hardHiScore.setText(String.valueOf(sharedPreferences.getInt(Level.HARD.name(), 0)));
        insaneHiScore.setText(String.valueOf(sharedPreferences.getInt(Level.INSANE.name(), 0)));
    }
}
