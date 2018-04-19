package com.softwareoverflow.colorfall.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.softwareoverflow.colorfall.R;

public class EndGameActivity extends AppCompatActivity {

    private TextView playAgainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        playAgainButton = findViewById(R.id.playAgainButton);

        Animation fade = new AlphaAnimation(0.1f, 1f);
        fade.setInterpolator(new LinearInterpolator());
        fade.setRepeatCount(Animation.INFINITE);
        fade.setRepeatMode(Animation.REVERSE);
        fade.setDuration(1000);

        playAgainButton.startAnimation(fade);
    }


    public void playAgain(View v){

    }
}
