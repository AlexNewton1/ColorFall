package com.softwareoverflow.colorfall.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.softwareoverflow.colorfall.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void playGame(View v) {

        String difficulty;
        switch (v.getId()) {
            case R.id.playEasy:
                difficulty = "EASY";
                break;
            case R.id.playMedium:
                difficulty = "MEDIUM";
                break;
            case R.id.playHard:
                difficulty = "HARD";
                break;
            case R.id.playInsane:
                difficulty = "INSANE";
                break;
            default:
                difficulty = "EASY";
                break;
        }

        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("difficulty", difficulty);
        startActivity(gameIntent);
    }

    public void rollCredits(View v) {


    }

    public void showSettings(View v) {

    }
}
