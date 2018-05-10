package com.softwareoverflow.colorfall.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.Window;
import android.view.WindowManager;

import com.softwareoverflow.colorfall.GameView;
import com.softwareoverflow.colorfall.Level;
import com.softwareoverflow.colorfall.R;

public class GameActivity extends Activity {

    private GameView gameView;
    private ConstraintLayout pauseScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_screen);

        //default value
        Level level  = Level.EASY;
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String levelDifficulty = extras.getString("difficulty");
            level = Level.valueOf(levelDifficulty);
        }
        level.resetSpeed();

        gameView = findViewById(R.id.gameView);
        gameView.setLevel(level);

        //TODO - add a pauseScreen (R.id.pauseScreen);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(gameView != null){
            gameView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(gameView != null){
            gameView.onResume();
        }
    }
}
